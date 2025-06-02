package com.gravitlauncher.simplecabinet.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.gravitlauncher.simplecabinet.web.model.user.HardwareId;
import com.gravitlauncher.simplecabinet.web.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u left join fetch u.groups where u.username = :usernameOrEmail or u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmailWithGroups(String usernameOrEmail);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(UUID uuid);

    @Query("select u from User u left join fetch u.assets where u.username = :username")
    Optional<User> findByUsernameFetchAssets(String username);

    @Query("select u from User u left join fetch u.assets where u.uuid = :uuid")
    Optional<User> findByUuidFetchAssets(UUID uuid);

    @Query("select u from User u left join fetch u.assets where u.id = :id")
    Optional<User> findByIdFetchAssets(Long id);

    @Query("select u from UserSession session, User u where session.user = u and session.hardwareId = :hardwareId")
    List<User> findByHardwareId(HardwareId hardwareId);

    @Query("select u from UserSession session, User u join fetch u.assets where session.user = u and session.hardwareId = :hardwareId")
    List<User> findByHardwareIdFetchAssets(HardwareId hardwareId);

    @Query("update User u set u.reputation = u.reputation + :value where u.id = :userId")
    @Modifying
    void changeReputation(Long userId, Long value);
}
