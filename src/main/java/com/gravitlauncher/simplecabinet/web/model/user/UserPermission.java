package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(name = "UserPermission")
@Table(name = "user_permissions")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_permissions_generator")
    @SequenceGenerator(name = "user_permissions_generator", sequenceName = "user_permissions_seq", allocationSize = 1)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Setter
    private String name;

    @Setter
    private String value;

}
