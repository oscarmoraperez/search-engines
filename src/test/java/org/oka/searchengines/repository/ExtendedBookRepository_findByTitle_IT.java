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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ExtendedBookRepository_findByTitle_IT {
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

    @SneakyThrows
    @Test
    public void shouldSaveListOfBooks() {
        // Given
        String title1 = RandomStringUtils.randomAlphabetic(8);
        String title2 = RandomStringUtils.randomAlphabetic(8);
        IndexedBook book1 = IndexedBook.builder().title(title1).authors(List.of("author1")).content("content").language("en").subjects(List.of()).build();
        IndexedBook book2 = IndexedBook.builder().title(title2).authors(List.of("author2")).content("content2").language("es").subjects(List.of()).build();
        bookRepository.saveIndexedBooks(List.of(book1, book2));

        // When
        Collection<IndexedBook> byTitle = bookRepository.findByTitle(title1);

        // Then
        assertThat(byTitle).isNotEmpty().hasSize(1);
    }
}
