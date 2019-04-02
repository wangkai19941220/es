package com.example.es;


import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@SpringBootApplication
@RestController
public class EsApplication {


    @Autowired
    private TransportClient client;

	@GetMapping("/wangkai")
	public String index(){
		return "index";
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
    public ResponseEntity add(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age, @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date, @RequestParam(name = "country") String country){
        try {
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("name", name)
                    .field("age", age)
                    .field("date", date.getTime())
                    .field("country", country)
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



    public static void main(String[] args) {
		SpringApplication.run(EsApplication.class, args);
	}

}
