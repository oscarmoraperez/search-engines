package org.oka.searchengines.init;

import lombok.extern.java.Log;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.commons.io.FileUtils;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
//@Profile("dev")
@Log
public class Initializer {
    @Autowired
    private BookRepository bookRepository;
    @Value("classpath:data")
    private Resource dataFolder;

    @PostConstruct
    public void init() throws IOException {
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
            indexedBooks.add(new IndexedBook(book.getTitle(), authors, sb.toString(), book.getMetadata().getLanguage(), subjects));
        }
        this.bookRepository.saveIndexedBooks(indexedBooks);
    }
}
