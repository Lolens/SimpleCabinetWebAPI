package com.gravitlauncher.simplecabinet.web.dto.user;

import com.gravitlauncher.simplecabinet.web.model.user.HardwareId;

import java.util.Base64;

public class HardwareInfoDto {
    public final long id;
    public final int bitness;
    public final long totalMemory;
    public final int logicalProcessors;
    public final int physicalProcessors;
    public final long processorMaxFreq;
    public final boolean battery;
    public final String hwDiskId;
    public final String displayId;
    public final String baseboardSerialNumber;
    public final String publicKey;
    public final boolean banned;

    public HardwareInfoDto(HardwareId entity) {
        this.id = entity.getId();
        this.bitness = entity.getBitness();
        this.totalMemory = entity.getTotalMemory();
        this.logicalProcessors = entity.getLogicalProcessors();
        this.physicalProcessors = entity.getPhysicalProcessors();
        this.processorMaxFreq = entity.getProcessorMaxFreq();
        this.battery = entity.isBattery();
        this.hwDiskId = entity.getHwDiskId();
        this.displayId = entity.getDisplayId() == null ? null : Base64.getEncoder().encodeToString(entity.getDisplayId());
        this.baseboardSerialNumber = entity.getBaseboardSerialNumber();
        this.publicKey = Base64.getEncoder().encodeToString(entity.getPublicKey());
        this.banned = entity.isBanned();
    }
}
