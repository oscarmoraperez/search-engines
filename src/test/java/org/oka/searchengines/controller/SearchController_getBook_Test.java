package org.oka.searchengines.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.repository.BookRepository;
import org.oka.searchengines.repository.ExtendedBookRepository;
import org.oka.searchengines.service.BookService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchController_getBook_Test {
    @InjectMocks
    SearchController searchController;
    @Mock
    BookService bookService;

    @Test
    public void shouldCallService() {
        // Given
        when(bookService.findById(anyLong())).thenReturn(new IndexedBook());

        // When
        searchController.getBook(44L);

        // Then
        verify(bookService).findById(44L);
    }
}
