package com.example.es;


import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@RestController
public class EsApplication {


    @Autowired
    private TransportClient client;

	@GetMapping("/wangkai")
	public String index(){
		return "index";
	}

    @GetMapping("/get/people/man")
	public ResponseEntity get(@RequestParam(name = "id",defaultValue = "") String id){
	    if (id.isEmpty()){
	        return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        GetResponse result=this.client.prepareGet("people","man",id).get();
	    if (!result.isExists()){
	        return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result.getSource(),HttpStatus.OK);
    }

    /**
     * 增加
     * @param name
     * @param age
     * @param date
     * @param country
     * @return
     */
    @PostMapping("/add/people/man")
    @ResponseBody
    public ResponseEntity add(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age, @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date, @RequestParam(name = "country") String country,@RequestParam(name = "sex") String sex){
        try {
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("name", name)
                    .field("age", age)
                    .field("date", date.getTime())
                    .field("country", country)
                    .field("sex",sex)
                    .endObject();
            // prepareIndex构建索引
            IndexResponse result = this.client.prepareIndex("people", "man")
                    .setSource(content)
                    .get();
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/del/people/man")
    @ResponseBody
    public ResponseEntity del(@RequestParam(name = "id") String id){
        DeleteResponse result=this.client.prepareDelete("people","man",id).get();
        return new ResponseEntity(result.getResult().toString(),HttpStatus.OK);
    }

    /**
     * 根据ID更新
     * @param id
     * @param name
     * @param age
     * @param date
     * @param country
     * @return
     */
    @PutMapping("/update/people/man")
    @ResponseBody
    public ResponseEntity update(@RequestParam(name = "id") String id,
                                 @RequestParam(name = "name", required = false) String name,
                                 @RequestParam(name = "age", required = false) Integer age,
                                 @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date,
                                 @RequestParam(name = "country", required = false) String country){
        UpdateRequest update = new UpdateRequest("people", "man", id);
        try {
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject();
            if(null != name){
                content.field("name", name);
            }
            if(null != age){
                content.field("age", age);
            }
            if(null != date){
                content.field("date", date);
            }
            if(null != country){
                content.field("country", country);
            }
            content.endObject();
            update.doc(content);


        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            UpdateResponse result = this.client.update(update).get();
            return new ResponseEntity(result.getResult().toString(), HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 复合条件查询
     * @param name
     * @param minAge
     * @param maxAge
     * @return
     */
    @PostMapping("/query/people/man")
    @ResponseBody
    public ResponseEntity query(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "gt_age", defaultValue = "0") Integer minAge,
            @RequestParam(name = "lt_age", required = false) Integer maxAge
    ){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(null != name){
            boolQuery.must(QueryBuilders.matchQuery("name", name));

        }

        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age")
                .from(minAge);

        if(null != maxAge && 0 < maxAge){
            rangeQuery.to(maxAge);
        }

        boolQuery.filter(rangeQuery);

        SearchRequestBuilder builder = this.client.prepareSearch("people")
                .setTypes("man")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery)
                .setFrom(0)
                .setSize(10);

//        System.out.println(builder);
        SearchResponse response = builder.get();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : response.getHits()){
            result.add(hit.getSource());
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }



    /**
     * 高亮查询
     * @param name
     * @param minAge
     * @param maxAge
     * @return
     */
    @GetMapping("/query/people/man/high")
    @ResponseBody
    public ResponseEntity highLighterQuery(@RequestParam(name = "name", required = false) String name){

        QueryBuilder matchQuery= QueryBuilders.matchQuery("name",name);

        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<h2>");
        highlightBuilder.postTags("</h2>");
        highlightBuilder.field("name");

        SearchResponse response=this.client.prepareSearch("people")
                .setTypes("man")
                .setQuery(matchQuery)
                .highlighter(highlightBuilder)
                .execute().actionGet();
        List<Man> result = new ArrayList<>();
        for(SearchHit hit : response.getHits()){
            Man man=new Man();
            Map<String, Object> entityMap = hit.getSourceAsMap();;
            if(!StringUtils.isEmpty(hit.getHighlightFields().get("name"))) {
                Text[] text = hit.getHighlightFields().get("name").getFragments();
                man.setName(text[0].toString());
                man.setAge(Integer.parseInt(String.valueOf(entityMap.get("age"))));
                man.setCountry(String.valueOf(entityMap.get("country")));
            }
            result.add(man);
        }

       return new ResponseEntity(result, HttpStatus.OK);
    }



    public static void main(String[] args) {
		SpringApplication.run(EsApplication.class, args);
	}

}
