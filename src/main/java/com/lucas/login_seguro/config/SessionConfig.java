package com.lucas.login_seguro.config;

import org.mongodb.spring.session.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {
}