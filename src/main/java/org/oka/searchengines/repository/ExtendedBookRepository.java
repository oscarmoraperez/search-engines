package org.oka.searchengines.repository;

import lombok.RequiredArgsConstructor;
import org.oka.searchengines.model.Facet;
import org.oka.searchengines.model.Field;
import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.Key;
import org.oka.searchengines.model.SearchRequest;
import org.oka.searchengines.model.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.FacetEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ExtendedBookRepository {
    private final BookRepository bookRepository;
    private final SolrTemplate solrTemplate;
    private final AtomicLong id = new AtomicLong(0);

    public void saveIndexedBooks(final Collection<IndexedBook> indexedBooks) {
        indexedBooks.forEach(i -> i.setId(id.getAndAdd(1)));
        bookRepository.saveAll(indexedBooks);
    }

    public Optional<IndexedBook> findById(final Long id) {
        return bookRepository.findById(id);
    }

    public Collection<IndexedBook> findByTitle(final String title) {
        return bookRepository.findByTitle(title);
    }

    public SearchResponse search(SearchRequest searchRequest) {
        Criteria criteria = null;
        if (searchRequest.isFullText()) {
            criteria = new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD);
        } else {
            criteria = new Criteria(searchRequest.getField()).contains(searchRequest.getValue());
        }

        FacetQuery query = new SimpleFacetQuery(criteria);
        query.setFacetOptions(new FacetOptions().addFacetOnField(searchRequest.getFacetField()));
        query.setPageRequest(Pageable.ofSize(500)); // for the sake of this demo project, paging is out of the scope

        FacetPage<IndexedBook> books = solrTemplate.queryForFacetPage("books", query, IndexedBook.class);

        // Facet information is retrieved
        Page<? extends FacetEntry> facetsInformation = new ArrayList<>(books.getAllFacets()).get(0);

        List<Facet> facets = new ArrayList<>();
        for (FacetEntry facetEntry : facetsInformation.getContent()) {
            facets.add(Facet.builder()
                    .value(facetEntry.getValue())
                    .field(Field.builder().name(searchRequest.getFacetField()).build())
                    .key(Key.builder().name(facetEntry.getKey().toString()).build())
                    .valueCount(facetEntry.getValueCount())
                    .build());
        }

        return SearchResponse.builder().books(books.getContent()).facets(facets).numFound(books.getContent().size()).build();
    }
}
