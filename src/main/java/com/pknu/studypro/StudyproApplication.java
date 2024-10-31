package com.pknu.studypro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyproApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyproApplication.class, args);
    }

}
