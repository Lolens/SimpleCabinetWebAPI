package com.gravitlauncher.simplecabinet.web.dto;

import com.gravitlauncher.simplecabinet.web.model.News;

import java.util.List;
import java.util.stream.Collectors;

public class NewsDto {
    public final long id;
    public final String header;
    public final String miniText;
    public final String text;
    public final String pictureURL;
    public final List<NewsCommentDto> comments;


    public NewsDto(long id, String header, String pictureURL , String miniText, String text, List<NewsCommentDto> comments) {
        this.id = id;
        this.header = header;
        this.miniText = miniText;
        this.text = text;
        this.pictureURL = pictureURL;
        this.comments = comments;
    }

    public static NewsDto makeMiniNews(News news) {
        return new NewsDto(news.getId(), news.getHeader(),news.getPicture(), news.getMiniText(), null, null);
    }

    public static NewsDto makeFullNews(News news) {
        return new NewsDto(news.getId(), news.getHeader(), news.getPicture(), news.getMiniText(), news.getText(),
                news.getComments().stream().map(NewsCommentDto::new).collect(Collectors.toList()));
    }
}
