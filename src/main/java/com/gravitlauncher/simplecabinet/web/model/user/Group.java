package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "Group")
@Table(name = "groups")
public class Group {
    @Id
    private String id;
    private String displayName;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Group parent;
}
