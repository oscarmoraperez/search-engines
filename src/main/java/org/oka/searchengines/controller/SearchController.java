package org.oka.searchengines.controller;

import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.SearchRequest;
import org.oka.searchengines.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class SearchController {
    @Autowired
    BookRepository bookRepository;

    @GetMapping(value = "/api/v1/book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IndexedBook> getBook(@PathVariable("id") final Long id) {
        IndexedBook byId = bookRepository.findById(id).orElseThrow();

        return new ResponseEntity<>(byId, OK);
    }

    @PostMapping(value = "/api/v1/book/search")
    public ResponseEntity<SearchRequest> search(@RequestBody SearchRequest searchRequest) {
        return null;
    }
}
