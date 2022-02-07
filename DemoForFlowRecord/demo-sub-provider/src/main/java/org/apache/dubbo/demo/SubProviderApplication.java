package org.apache.dubbo.demo;

import org.apache.dubbo.demo.subprovider.SubProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SubProviderApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SubProviderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        SubProvider.subProvider();
    }
}
