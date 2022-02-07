package org.apache.dubbo.demo;

import org.apache.dubbo.demo.provider.ApplicationProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProviderApplication implements ApplicationRunner {

	public static void main(String[] args){
		SpringApplication.run(ProviderApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ApplicationProvider.provider();
	}
}
