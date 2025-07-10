package com.project.lottofun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottoFunApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottoFunApplication.class, args);
    }

}
