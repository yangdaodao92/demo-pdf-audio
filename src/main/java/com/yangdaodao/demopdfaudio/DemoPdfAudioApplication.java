package com.yangdaodao.demopdfaudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.apache.tomcat.util.compat.JrePlatform.IS_WINDOWS;

@SpringBootApplication
public class DemoPdfAudioApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoPdfAudioApplication.class, args);
		context.getEnvironment().setDefaultProfiles(IS_WINDOWS ? "dev" : "online");
	}

}

