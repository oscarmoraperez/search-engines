package org.oka.searchengines.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Facet {
    private long valueCount;
    private String value;
    private Field field;
    private Key key;
}