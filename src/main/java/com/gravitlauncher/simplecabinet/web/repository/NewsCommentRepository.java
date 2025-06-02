package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
}
