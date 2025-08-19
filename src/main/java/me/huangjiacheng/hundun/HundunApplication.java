package me.huangjiacheng.hundun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HundunApplication {
    public static void main(String[] args) {
        SpringApplication.run(HundunApplication.class, args);
    }
}