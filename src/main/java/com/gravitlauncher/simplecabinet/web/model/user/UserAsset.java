package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(name = "UserAsset")
@Table(name = "user_assets")
public class UserAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_assets_generator")
    @SequenceGenerator(name = "user_assets_generator", sequenceName = "user_assets_seq", allocationSize = 1)
    private long id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Setter
    private String name;
    @Setter
    private String hash;
    @Setter
    private String metadata;

}
