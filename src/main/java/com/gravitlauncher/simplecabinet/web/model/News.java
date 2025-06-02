package com.gravitlauncher.simplecabinet.web.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "News")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(
                name = News.WITH_COMMENTS,
                attributeNodes = @NamedAttributeNode("comments")
        )
})
@Table(name = "news")
public class News {
    public static final String WITH_COMMENTS = "News[comments]";
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_generator")
    @SequenceGenerator(name = "news_generator", sequenceName = "news_seq", allocationSize = 1)
    private long id;
    @Setter
    @Getter
    private String header;
    @Setter
    @Getter
    @Column(name = "mini_text")
    private String miniText;
    @Setter
    @Getter
    private String text;
    @Setter
    @Getter
    private int commentsCount;
    private String pictureURL;
    @Setter
    @Getter
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NewsComment> comments;

    public String getPicture () {
        return pictureURL;
    }

    public void setPicture (String pictureURL) {
        this.pictureURL = pictureURL;
    }

}
