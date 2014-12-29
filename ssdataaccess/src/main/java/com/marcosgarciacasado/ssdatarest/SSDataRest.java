package com.marcosgarciacasado.ssdatarest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class SSDataRest {

    public static void main(String[] args) {
        SpringApplication.run(SSDataRest.class, args);
    }
}
