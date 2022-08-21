package org.oka.searchengines.repository;

import org.oka.searchengines.model.IndexedBook;
import org.oka.searchengines.model.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Component
public interface BookRepository extends SolrCrudRepository<IndexedBook, Long> {
    @Autowired
    SolrTemplate solrTemplate = null;

    AtomicLong id = new AtomicLong(0);

    default void saveIndexedBooks(final Collection<IndexedBook> indexedBooks) {
        indexedBooks.forEach(i -> i.setId(id.getAndAdd(1)));
        this.saveAll(indexedBooks);
    }

    Collection<IndexedBook> findByTitle(final String title);

//    default List<IndexedBook> search(SearchRequest searchRequest) {
//        SimpleFacetQuery simpleFacetQuery = new SimpleFacetQuery();
//        FacetQuery query = new SimpleFacetQuery(
//                new Criteria(searchRequest.getField())
//                .contains(searchRequest.getFacetField()))
//                .setFacetOptions(new FacetOptions().addFacetOnField(searchRequest.getFacetField()));
//
//        Criteria criteria = new Criteria(searchRequest.getField()
//                .contains(searchRequest.getValue()));
//                .;
//        criteria.
//        simpleFacetQuery.set
//        searchRequest.
//        return solrTemplate.query("books", new Query() {
//        });
//    }
}
