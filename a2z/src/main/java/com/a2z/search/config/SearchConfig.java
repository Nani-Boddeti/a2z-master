package com.a2z.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@ComponentScan(basePackages = { "com.a2z.dao" })
public class SearchConfig extends ElasticsearchConfiguration {
	@Value("${spring.data.elasticsearch.client.rest.username}")
	String userName ;
	@Value("${spring.data.elasticsearch.client.rest.password}")
	String password ;
	@Override
	public ClientConfiguration clientConfiguration() {
		// TODO Auto-generated method stub
		return ClientConfiguration.builder()
				.connectedTo("localhost:9200")
                .withBasicAuth(userName,password).build();
	}

}
