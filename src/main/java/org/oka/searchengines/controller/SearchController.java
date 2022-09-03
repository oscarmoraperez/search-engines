package org.oka.searchengines.controller;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.SearchRequest;
import org.oka.searchengines.model.SearchResponse;
import org.oka.searchengines.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

/**
 * Controller / entry point of the application.
 */
@RestController
@RequiredArgsConstructor
public class SearchController {
    private final BookService bookService;

    @PostMapping(value = "/api/v1/book/populate")
    @ApiOperation("Deletes all the existing books and repopulates solr instance with a new set of books (from a set of epub files)")
    public ResponseEntity<Void> populateBooks() {
        bookService.populateBooks();

        return new ResponseEntity<>(OK);
    }

    @GetMapping(value = "/api/v1/book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Retrieves book based on the unique id")
    public ResponseEntity<IndexedBook> getBook(@PathVariable("id") final Long id) {
        IndexedBook byId = bookService.findById(id);

        return new ResponseEntity<>(byId, OK);
    }

    @PostMapping(value = "/api/v1/book/search")
    @ApiOperation("Returns a set of books based on the SearchRequest parameters")
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest searchRequest) {

        return new ResponseEntity<>(bookService.search(searchRequest), OK);
    }

    @GetMapping(value = "/api/v1/book/suggest")
    @ApiOperation("Returns a set of suggestions based on the request handler and search component of the solr instance")
    public ResponseEntity<SuggesterResponse> suggest(@RequestParam(name = "q") String term) {

        return new ResponseEntity<>(bookService.suggest(term), OK);
    }
}
