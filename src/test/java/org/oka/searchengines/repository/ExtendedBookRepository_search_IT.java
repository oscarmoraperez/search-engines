package org.oka.searchengines.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.SearchRequest;
import org.oka.searchengines.model.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.SolrContainer;
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ExtendedBookRepository_search_IT {
    @Autowired
    ExtendedBookRepository bookRepository;
    @Container
    static SolrContainer container = new SolrContainer(DockerImageName.parse("solr:8.8"))
            .withCollection("books")
            .withStartupCheckStrategy(new IsRunningStartupCheckStrategy().withTimeout(Duration.ofSeconds(15)));

    @DynamicPropertySource
    static void registerSolrProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.solr.host", () -> "http://" + container.getHost() + ":" + container.getSolrPort() + "/solr");
    }

    @Test
    public void shouldRerturnTheBooks() {
        // Given
        Collection<IndexedBook> books = prepare100Books();
        bookRepository.saveIndexedBooks(books);
        SearchRequest searchRequest = SearchRequest.builder().field("content").value("demo").facetField("subjects").build();

        // When
        SearchResponse search = bookRepository.search(searchRequest);

        // Then
        assertThat(search.getBooks()).hasSize(100);
    }

    @SneakyThrows
    @Test
    public void shouldReturnTheBooksWithFullText() {
        // Given
        Collection<IndexedBook> books = prepare100Books();
        bookRepository.saveIndexedBooks(books);
        SearchRequest searchRequest = SearchRequest.builder().facetField("subjects").fullText(true).q("demo").build();

        // When
        SearchResponse search = bookRepository.search(searchRequest);

        // Then
        assertThat(search.getBooks()).hasSize(100);
    }

    private Collection<IndexedBook> prepare100Books() {
        Collection<IndexedBook> books = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            books.add(new IndexedBook("title" + i, List.of("author" + i), "demo", "en", List.of("subject" + (i % 10))));
        }
        return books;
    }
}
