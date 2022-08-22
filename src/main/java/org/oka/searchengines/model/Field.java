package org.oka.searchengines.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Field {
    private final String name;
}
