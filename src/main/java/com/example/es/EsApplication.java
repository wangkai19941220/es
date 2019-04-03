package com.example.es;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EsApplication {


    public static void main(String[] args) {
		SpringApplication.run(EsApplication.class, args);
	}

}
