package org.oka.searchengines.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.SearchRequest;
import org.oka.searchengines.model.SearchResponse;
import org.oka.searchengines.repository.ExtendedBookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service layer to offer features on book instance.
 */
@Service
@RequiredArgsConstructor
@Log
public class BookService {
    @Value("classpath:data")
    private Resource dataFolder;

    private final ExtendedBookRepository bookRepository;

    /**
     * Deletes all the books and re-populates SOLR instance with a set of 55 epub files (located in the /resource folder)
     */
    @SneakyThrows
    public void populateBooks() {
        bookRepository.deleteAll();
        EpubReader epubReader = new EpubReader();

        File[] matchingFiles = dataFolder.getFile().listFiles((dir, name) -> name.endsWith("epub"));
        List<IndexedBook> indexedBooks = new ArrayList<>();

        for (File epub : matchingFiles) {
            Book book = epubReader.readEpub(FileUtils.openInputStream(epub));
            log.info("Book: " + book.getTitle());
            StringBuilder sb = new StringBuilder();
            for (nl.siegmann.epublib.domain.Resource resource : book.getContents()) {
                sb.append(new String(resource.getData()));
            }

            List<String> authors = book.getMetadata().getAuthors().stream().map(Author::toString).collect(toList());
            List<String> subjects = new ArrayList<>(book.getMetadata().getSubjects());

            indexedBooks.add(IndexedBook.builder()
                    .title(book.getTitle())
                    .authors(authors)
                    .content(sb.toString())
                    .language(book.getMetadata().getLanguage())
                    .subjects(subjects).build());
        }
        bookRepository.saveIndexedBooks(indexedBooks);
    }

    /**
     * Finds a book in the SOLR instance based on the unique id
     */
    public IndexedBook findById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    /**
     * Returns a set of books based on the criteria defined on the search request.
     */
    public SearchResponse search(SearchRequest searchRequest) {
        return bookRepository.search(searchRequest);
    }

    /**
     * Returns a set of suggestions (based on the previously configured SOLR request handler/search component) on
     * the title and authors field
     */
    public SuggesterResponse suggest(String term) {
        return bookRepository.suggest(term);
    }
}
