package com.gravitlauncher.simplecabinet.web.model.updates;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "UpdateDirectory")
@Table(name = "update_directories")
public class UpdateDirectory {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "update_directories_generator")
    @SequenceGenerator(name = "update_directories_generator", sequenceName = "update_directories_seq", allocationSize = 1)
    private long id;
    @Getter
    @Setter
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode content;
}
