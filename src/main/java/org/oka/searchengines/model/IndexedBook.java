package org.oka.searchengines.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@Getter
@Setter
@SolrDocument(solrCoreName = "books")
@NoArgsConstructor
@AllArgsConstructor
public class IndexedBook {
    @Id
    @Indexed(name = "identifier", type = "long")
    private Long id;
    @Indexed(name = "title", type = "string")
    private String title = null;
    @Indexed(name = "authors", type = "string")
    private List<String> authors = null;
    @Indexed(name = "content", type = "string")
    private String content = null;
    @Indexed(name = "language", type = "string")
    private String language = null;
    @Indexed(name = "subjects", type = "string")
    private List<String> subjects = null;

    public IndexedBook(List<Long> id, List<String> title, List<String> authors, List<String> content, List<String> language, List<String> subjects) {
        this.id = id.get(0);
        this.title = title.get(0);
        this.authors = authors;
        this.content = content.get(0);
        this.language = language.get(0);
        this.subjects = subjects;
    }

    public IndexedBook(String title, List<String> authors, String content, String language, List<String> subjects) {
        this.title = title;
        this.authors = authors;
        this.content = content;
        this.language = language;
        this.subjects = subjects;
    }
}
