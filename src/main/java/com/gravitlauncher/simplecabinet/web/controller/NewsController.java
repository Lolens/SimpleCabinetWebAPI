package com.gravitlauncher.simplecabinet.web.controller;

import com.gravitlauncher.simplecabinet.web.dto.NewsCommentDto;
import com.gravitlauncher.simplecabinet.web.dto.NewsDto;
import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.model.News;
import com.gravitlauncher.simplecabinet.web.model.NewsComment;
import com.gravitlauncher.simplecabinet.web.service.NewsService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;

    @GetMapping("/page/{pageId}")
    public PageDto<NewsDto> getPage(@PathVariable int pageId) {
        var list = newsService.findAll(PageRequest.of(pageId, 10, Sort.Direction.ASC, "id"));
        return new PageDto<>(list.map(newsService::toMiniNewsWithPictureUrl));
    }

    @GetMapping("/id/{newsId}")
    public NewsDto getById(@PathVariable long newsId) {
        var news = newsService.findByIdFetchComments(newsId);
        if (news.isEmpty()) {
            throw new EntityNotFoundException("News not found");
        }
        return newsService.toNewsWithPictureUrl(news.get());
    }

    @PostMapping("/id/{newsId}/update")
    public void updateById(@PathVariable long newsId, @RequestBody NewsCreateRequest request) {
        var newsOptional = newsService.findByIdFetchComments(newsId);
        if (newsOptional.isEmpty()) {
            throw new EntityNotFoundException("News not found");
        }
        var news = newsOptional.get();
        news.setHeader(request.header);
        news.setMiniText(request.miniText);
        news.setText(request.text);
        news.setPicture(request.pictureURL);
        newsService.save(news);
    }

    @DeleteMapping("/id/{newsId}/comment/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteCommentById(@PathVariable long newsId, @PathVariable long commentId) {
        var commentOptional = newsService.findCommentById(commentId);
        if (commentOptional.isEmpty()) {
            throw new EntityNotFoundException("NewsComment not found");
        }
        newsService.delete(commentOptional.get());
    }

    @PutMapping("/id/{newsId}/newcomment")
    @PreAuthorize("isAuthenticated()")
    public NewsCommentDto createComment(@PathVariable long newsId, @RequestBody NewsCommentCreateRequest createCommentRequest) {
        var news = newsService.findById(newsId);
        if (news.isEmpty()) {
            throw new EntityNotFoundException("News not found");
        }
        var comment = new NewsComment();
        comment.setNews(news.get());
        comment.setUser(userService.getCurrentUser().getReference());
        comment.setText(createCommentRequest.text);
        newsService.save(comment);
        return new NewsCommentDto(comment);
    }

    @PutMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public NewsDto create(@RequestBody NewsCreateRequest request) {
        News news = new News();
        news.setHeader(request.header);
        news.setMiniText(request.miniText);
        news.setText(request.text);
        news.setPicture(request.pictureURL);
        newsService.save(news);
        return newsService.toMiniNewsWithPictureUrl(news);
    }

    public record NewsCreateRequest(String header, String miniText, String text , String pictureURL ) {
    }

    public record NewsCommentCreateRequest(String text) {

    }
}
