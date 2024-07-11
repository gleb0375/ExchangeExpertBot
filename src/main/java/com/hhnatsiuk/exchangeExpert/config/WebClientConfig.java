package com.hhnatsiuk.exchangeExpert.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${webclient.base.url.raiffeisenbank}")
    private String rb_BaseUrl;

    @Value("${webclient.base.url.komercnibanka}")
    private String kb_BaseUrl;

    @Bean
    public WebClient raiffeisenbankWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(rb_BaseUrl).build();
    }

    @Bean
    public WebClient komercniBankaWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(kb_BaseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
                .build();
    }
}
