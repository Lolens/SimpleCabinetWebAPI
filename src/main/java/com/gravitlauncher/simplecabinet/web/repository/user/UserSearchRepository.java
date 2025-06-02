package com.gravitlauncher.simplecabinet.web.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.gravitlauncher.simplecabinet.web.model.user.User;

import java.util.UUID;

public interface UserSearchRepository extends PagingAndSortingRepository<User, UUID> {
    @EntityGraph(attributePaths = {"assets"})
    @Query("select u from User u where u.username ilike %:username%")
    Page<User> findByUsernameFetchAssets(String username, Pageable pageable);

    @EntityGraph(attributePaths = {"assets"})
    @Query("select u from User u where u.email ilike %:Email%")
    Page<User> findByEmail(String Email,Pageable pageable);
}