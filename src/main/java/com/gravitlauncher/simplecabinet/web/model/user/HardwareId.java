package com.gravitlauncher.simplecabinet.web.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(name = "HardwareId")
@Table(name = "hwids")
public class HardwareId {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hwids_generator")
    @SequenceGenerator(name = "hwids_generator", sequenceName = "hwids_seq", allocationSize = 1)
    private long id;

    @Setter
    private int bitness;
    @Setter
    private long totalMemory;
    @Setter
    private int logicalProcessors;
    @Setter
    private int physicalProcessors;
    @Setter
    private long processorMaxFreq;
    @Setter
    private boolean battery;
    @Setter
    private String hwDiskId;
    @Setter
    private byte[] displayId;
    @Setter
    private String baseboardSerialNumber;

    @Setter
    @Column(unique = true)
    private byte[] publicKey;
    @Setter
    private boolean banned;

}
