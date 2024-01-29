package com.social.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "com.social.demo" })

public class DemoApplication {

	@Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp.initializeApp(firebaseOptions, "social");
    }

    return FirebaseMessaging.getInstance(FirebaseApp.getInstance("social"));
}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
