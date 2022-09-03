package org.oka.searchengines.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.SearchRequest;
import org.oka.searchengines.repository.BookRepository;
import org.oka.searchengines.repository.ExtendedBookRepository;
import org.oka.searchengines.service.BookService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchController_search_Test {
    @InjectMocks
    SearchController searchController;
    @Mock
    BookService bookService;

    @Test
    public void shouldCallService() {
        // Given
        SearchRequest searchRequest = mock(SearchRequest.class);

        // When
        searchController.search(searchRequest);

        // Then
        verify(bookService).search(searchRequest);
    }
}
