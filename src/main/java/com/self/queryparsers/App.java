package com.self.queryparsers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.self.queryparsers"})
public class App {
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
}