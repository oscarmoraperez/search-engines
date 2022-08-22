package org.oka.searchengines.model;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class SearchResponse {
    Collection<IndexedBook> books;
    Collection<Facet> facets;
    int numFound;
}

