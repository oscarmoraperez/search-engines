package org.oka.searchengines.model;

import lombok.Value;

@Value
public class SearchRequest {
    String field;
    String value;
    String facetField;
    String fullText;
    String q;
}