package org.oka.searchengines.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@Getter
@Setter
@SolrDocument(solrCoreName = "books")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class IndexedBook {
    @Id
    @Indexed(name = "identifier_s", type = "long")
    private Long id;
    @NonNull
    @Indexed(name = "title_s", type = "string")
    private String title;
    @NonNull
    @Indexed(name = "authors_sm", type = "string")
    private List<String> authors;
    @NonNull
    @Indexed(name = "content_t", type = "string")
    private String content;
    @NonNull
    @Indexed(name = "language_s", type = "string")
    private String language;
    @NonNull
    @Indexed(name = "subjects_sm", type = "string")
    private List<String> subjects;
    @Indexed(name = "suggest_t", type = "string")
    private String suggest;
}