package org.oka.searchengines.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.oka.searchengines.model.IndexedBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.SolrContainer;
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class BookRepository_saveIndexedBooks_IT {
    @Autowired
    BookRepository bookRepository;
    @Container
    static SolrContainer container = new SolrContainer(DockerImageName.parse("solr:8.8"))
            .withCollection("books")
            .withStartupCheckStrategy(new IsRunningStartupCheckStrategy().withTimeout(Duration.ofSeconds(15)));

    @DynamicPropertySource
    static void registerSolrProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.solr.host", () -> "http://" + container.getHost() + ":" + container.getSolrPort() + "/solr");
    }

    @SneakyThrows
    @Test
    public void shouldSaveListOfBooks() {
        // Given
        String title1 = RandomStringUtils.randomAlphabetic(8);
        String title2 = RandomStringUtils.randomAlphabetic(8);
        IndexedBook book1 = new IndexedBook(title1, List.of("author1"), "content", "en");
        IndexedBook book2 = new IndexedBook(title2, List.of("author2"), "content2", "es");

        // When
        bookRepository.saveIndexedBooks(List.of(book1, book2));

        // Then
        assertThat(bookRepository.findByTitle(title1)).hasSize(1);
        assertThat(bookRepository.findByTitle(title2)).hasSize(1);
    }
}
