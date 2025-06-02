package com.gravitlauncher.simplecabinet.web.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gravitlauncher.simplecabinet.web.model.user.Group;
import com.gravitlauncher.simplecabinet.web.model.user.UserPermission;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByGroup(Group group);

    Page<UserPermission> findByGroup(Group group, Pageable pageable);

    @Query(value = """
            with recursive r as (
                    select id, parent_id
                        from groups
                        where id in (select group_id from user_groups where user_id = ?1 and (end_date is null or end_date > ?2))
                    union
                    select g.id, rr.parent_id from groups g, r rr where g.id = rr.parent_id
                )
            select up.id, up.group_id, up.name, up.value from user_permissions up join r rr on up.group_id = rr.id
            """, nativeQuery = true)
    List<UserPermission> findByUser(Long userId, LocalDateTime now);

    @Query(value = """
            with recursive r as (
                    select id, parentId
                        from groups
                        where id in ?1
                    union
                    select id, parent_id from groups g, rec r where g.id = r.parent_id
                )
            select up.id, up.group_id, up.name, up.value from user_permissions up join r rr where up.group_id = rr.id
            """, nativeQuery = true)
    List<UserPermission> findByGroups(List<Long> groups);
}
