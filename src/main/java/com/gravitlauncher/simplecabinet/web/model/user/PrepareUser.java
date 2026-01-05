package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "prepare_users")
public class PrepareUser {

    @Setter
    @Column(unique = true)
    public String username;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prepare_users_generator")
    @SequenceGenerator(name = "prepare_users_generator", sequenceName = "prepare_users_seq", allocationSize = 1)
    private long id;

    @Setter
    @Column(unique = true)
    private String email;

    @Setter
    private String password;

    @Setter
    @Column(name = "hash_type")
    @Enumerated(EnumType.ORDINAL)
    private User.HashType hashType = User.HashType.BCRYPT;

    @Setter
    @Column(name = "confirm_token")
    private String confirmToken;

    @Setter
    private LocalDateTime date;

}
