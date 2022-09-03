package org.oka.searchengines.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oka.searchengines.service.BookService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SearchController_suggest_Test {
    @InjectMocks
    SearchController searchController;
    @Mock
    BookService bookService;

    @Test
    public void shouldCallService() {
        // Given

        // When
        searchController.suggest("term");

        // Then
        verify(bookService).suggest("term");
    }
}
