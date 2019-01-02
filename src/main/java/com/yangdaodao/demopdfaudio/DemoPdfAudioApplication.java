package com.yangdaodao.demopdfaudio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.apache.tomcat.util.compat.JrePlatform.IS_WINDOWS;

@SpringBootApplication
@MapperScan(value = "com.yangdaodao.demopdfaudio.dao")
public class DemoPdfAudioApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoPdfAudioApplication.class);
		app.setAdditionalProfiles(IS_WINDOWS ? "dev" : "online");
		app.run(args);
	}

}

