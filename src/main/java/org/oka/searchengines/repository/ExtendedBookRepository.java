package org.oka.searchengines.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
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
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Extended repository operations on the solr instance.
 */
@Repository
@RequiredArgsConstructor
public class ExtendedBookRepository {
    // Basic repository methods provided by derived spring jpa features
    private final BookRepository bookRepository;
    // Used for specific operation against SOLR instance
    private final SolrTemplate solrTemplate;
    // Used for specific operations not available in the SolrTemplate
    private final SolrClient solrClient;
    // Keeps track of a unique id
    private final AtomicLong id = new AtomicLong(0);

    /**
     * Assigns a unique id, creates the field: 'suggest' (as a combination of title and authors) and persists a
     * list of Books
     */
    public void saveIndexedBooks(final Collection<IndexedBook> indexedBooks) {
        // The Spring JPA does not autogenerate ids for the persisted SOLR documents.
        // It is necessary to use and atomic integer
        indexedBooks.forEach(i -> i.setId(id.getAndAdd(1)));
        // The 'suggest' field is a combination of title and authors. It is used for providing suggestions.
        indexedBooks.forEach(i -> i.setSuggest(i.getTitle() + " " + String.join(" ", i.getAuthors())));

        bookRepository.saveAll(indexedBooks);
    }

    /**
     * Deletes everything
     */
    public void deleteAll() {
        bookRepository.deleteAll();
    }

    /**
     * Find by id
     */
    public Optional<IndexedBook> findById(final Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Find by title
     */
    public Collection<IndexedBook> findByTitle(final String title) {
        return bookRepository.findByTitle(title);
    }

    /**
     * Complex / customizable search
     */
    public SearchResponse search(SearchRequest searchRequest) {
        // Initial criteria
        Criteria criteria = buildCritera(searchRequest);
        FacetQuery query = new SimpleFacetQuery(criteria);
        // Set facet configuration
        query.setFacetOptions(new FacetOptions().addFacetOnField(searchRequest.getFacetField()));
        // Pagination. For the sake of this demo project, no pagination (big size) is defined
        query.setPageRequest(Pageable.ofSize(500));
        // Run the query
        FacetPage<IndexedBook> books = solrTemplate.queryForFacetPage("books", query, IndexedBook.class);
        // Retrieve & extract information about facets
        Page<? extends FacetEntry> facetsInformation = new ArrayList<>(books.getAllFacets()).get(0);
        List<Facet> facets = extractFacetInformation(facetsInformation, searchRequest.getFacetField());

        return SearchResponse.builder()
                .books(books.getContent())
                .facets(facets)
                .numFound(books.getContent().size())
                .build();
    }

    /**
     * Calls the suggester component (previously configured on Solr side)
     */
    @SneakyThrows
    public SuggesterResponse suggest(String term) {
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/books/suggest");
        query.setQuery(term);
        query.setParam("suggest.dictionary", "mySuggester");

        QueryResponse queryResponse = solrClient.query(query);
        return queryResponse.getSuggesterResponse();
    }

    /**
     * Reads the facet information from the solr response and builds the response sent to the consumer
     */
    private List<Facet> extractFacetInformation(Page<? extends FacetEntry> facetsInformation,
                                                final String facetField) {
        List<Facet> facets = new ArrayList<>();

        for (FacetEntry facetEntry : facetsInformation.getContent()) {
            facets.add(Facet.builder()
                    .value(facetEntry.getValue())
                    .field(Field.builder().name(facetField).build())
                    .key(Key.builder().name(facetEntry.getKey().toString()).build())
                    .valueCount(facetEntry.getValueCount())
                    .build());
        }

        return facets;
    }

    /**
     * Builds the Criteria object based on the SearchRequest
     */
    private Criteria buildCritera(SearchRequest searchRequest) {
        Criteria criteria = null;
        if (searchRequest.isFullText()) {
            criteria = new Criteria().expression(searchRequest.getQ());
        } else {
            criteria = new Criteria(searchRequest.getField()).is(searchRequest.getValue());
        }

        return criteria;
    }
}
