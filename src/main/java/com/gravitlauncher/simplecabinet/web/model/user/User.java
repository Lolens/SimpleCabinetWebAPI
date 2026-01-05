package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "User")
@Table(name = "users")
@Cacheable
public class User implements BasicUser {

    public static HashType DEFAULT_PASSWORD_HASH = HashType.BCRYPT;
    //Base and launcher
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @SequenceGenerator(name = "users_generator", sequenceName = "users_seq", allocationSize = 1)
    private long id;

    @Setter
    @Getter
    @Column(unique = true)
    private String username;

    @Setter
    @Getter
    @Column(unique = true)
    private UUID uuid;

    //Password and permissions
    @Setter
    @Getter
    @Column(name = "hash_type")
    @Enumerated(EnumType.ORDINAL)
    private HashType hashType = HashType.BCRYPT;

    @Getter
    private String password;

    //Special
    @Setter
    @Getter
    @Column(unique = true)
    private String email;

    //Addional info (may be null)
    @Setter
    @Getter
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    @Setter
    @Getter
    private String status;

    @Setter
    @Getter
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Setter
    @Getter
    @Column(name = "totp_secret_key")
    private String totpSecretKey;

    @Setter
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserGroup> groups;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSession> sessions;

    @Setter
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAsset> assets;

    @Setter
    @Getter
    private String prefix;

    @Setter
    @Getter
    private Integer reputation;


    public static boolean isCorrectEmail(String email) //Very simple check
    {
        return email != null && email.contains("@") && email.length() >= 3;
    }

    public void setRawPassword(String password) {
        this.password = password;
    }

    public void setRawPasswordType(HashType type) {
        this.hashType = type;
    }

    @Getter
    public enum HashType { //for password
        BCRYPT(true),
        DOUBLEMD5(false),
        MD5(false),
        SHA256(false),
        AUTHMESHA256(true),
        PHPASS(true);
        boolean trusted;

        HashType(boolean trusted) {
            this.trusted = trusted;
        }

    }

    public enum Gender {
        FEMALE,
        MALE
    }
}
