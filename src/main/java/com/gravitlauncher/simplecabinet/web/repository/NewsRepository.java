package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.News;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {
    @EntityGraph(value = News.WITH_COMMENTS)
    @Query("select c from News c where c.id = :id")
    Optional<News> findByIdFetchComments(@Param("id") Long id);
}
