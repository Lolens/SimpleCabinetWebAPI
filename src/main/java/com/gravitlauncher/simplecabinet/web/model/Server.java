package com.gravitlauncher.simplecabinet.web.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity(name = "Server")
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servers_generator")
    @SequenceGenerator(name = "servers_generator", sequenceName = "servers_seq", allocationSize = 1)
    private long id;
    private String name;
    private String displayName;
    @Column(name = "max_online")
    private int maxOnline;
    private int online;
    private int tps;
    @SuppressWarnings("JpaAttributeTypeInspection")
    private List<String> users;
    @Column(name = "update_date")
    private LocalDateTime updateDate;

}
