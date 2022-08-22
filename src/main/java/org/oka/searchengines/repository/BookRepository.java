package org.oka.searchengines.repository;

import org.oka.searchengines.model.IndexedBook;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.Collection;

public interface BookRepository extends SolrCrudRepository<IndexedBook, Long> {
    Collection<IndexedBook> findByTitle(final String title);
}
