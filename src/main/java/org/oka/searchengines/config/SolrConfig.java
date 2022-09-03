package org.oka.searchengines.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Solr related object configuration
 */
@Configuration
public class SolrConfig {
    @Value("${spring.data.solr.host}")
    String url;

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(url).build();
    }
}
