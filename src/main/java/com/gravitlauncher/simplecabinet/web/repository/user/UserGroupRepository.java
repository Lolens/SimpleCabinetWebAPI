package com.gravitlauncher.simplecabinet.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gravitlauncher.simplecabinet.web.model.user.Group;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import com.gravitlauncher.simplecabinet.web.model.user.UserGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Query("select g from UserGroup g where g.user = ?1 and (g.endDate is null or g.endDate > ?2)")
    List<UserGroup> findByUser(User user, LocalDateTime now);

    @Query("select g from UserGroup g where g.group = ?1 and g.user = ?2 and (g.endDate is null or g.endDate > ?3)")
    Optional<UserGroup> findByGroupAndUser(Group group, User user, LocalDateTime now);
}
