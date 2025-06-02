package com.gravitlauncher.simplecabinet.web.controller.admin;

import com.gravitlauncher.simplecabinet.web.dto.PageDto;
import com.gravitlauncher.simplecabinet.web.dto.user.HardwareInfoDto;
import com.gravitlauncher.simplecabinet.web.dto.user.UserDto;
import com.gravitlauncher.simplecabinet.web.exception.EntityNotFoundException;
import com.gravitlauncher.simplecabinet.web.model.user.HardwareId;
import com.gravitlauncher.simplecabinet.web.service.DtoService;
import com.gravitlauncher.simplecabinet.web.service.user.HardwareIdService;
import com.gravitlauncher.simplecabinet.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/admin/hardware")
public class AdminHardwareController {
    @Autowired
    private HardwareIdService hardwareIdService;
    @Autowired
    private UserService userService;
    @Autowired
    private DtoService dtoService;

    @GetMapping("/id/{id}")
    public HardwareInfoDto findById(@PathVariable long id) {
        var hardwareOptional = hardwareIdService.findById(id);
        if (hardwareOptional.isEmpty()) {
            throw new EntityNotFoundException("HardwareId not found");
        }
        return new HardwareInfoDto(hardwareOptional.get());
    }

    @PostMapping("/id/{id}/setpublickey")
    public void setPublicKey(@PathVariable long id, @RequestBody SetPublicKeyRequest request) {
        var hardwareOptional = hardwareIdService.findById(id);
        if (hardwareOptional.isEmpty()) {
            throw new EntityNotFoundException("HardwareId not found");
        }
        var hardware = hardwareOptional.get();
        hardware.setPublicKey(Base64.getDecoder().decode(request.publicKey));
        hardwareIdService.save(hardware);
    }

    @GetMapping("/publickey/{publicKey}")
    public HardwareInfoDto findByPublicKey(@PathVariable String publicKey) {
        byte[] pubKey = Base64.getUrlDecoder().decode(publicKey);
        var hardwareOptional = hardwareIdService.findByPublicKey(pubKey);
        if (hardwareOptional.isEmpty()) {
            throw new EntityNotFoundException("HardwareId not found");
        }
        return new HardwareInfoDto(hardwareOptional.get());
    }

    @PostMapping("/search")
    public HardwareInfoDto findByData(@RequestBody HardwareSearchRequest request) {
        var hardwareOptional = hardwareIdService.findByHardware(request);
        if (hardwareOptional.isEmpty()) {
            throw new EntityNotFoundException("HardwareId not found");
        }
        return new HardwareInfoDto(hardwareOptional.get());
    }

    @GetMapping("/page/{pageId}")
    public PageDto<HardwareInfoDto> getPage(@PathVariable int pageId) {
        var result = hardwareIdService.findAll(PageRequest.of(pageId, 10));
        return new PageDto<>(result.map(HardwareInfoDto::new));
    }

    @GetMapping("/getusersbyhardware/{hardwareId}")
    public List<UserDto> getUsersByHardwareId(@PathVariable long hardwareId, @RequestParam boolean assets) {
        var hardwareOptional = hardwareIdService.findById(hardwareId);
        if (hardwareOptional.isEmpty()) {
            throw new EntityNotFoundException("HardwareId not found");
        }
        var users = assets ? userService.findByHardwareIdFetchAssets(hardwareOptional.get()) : userService.findByHardwareId(hardwareOptional.get());
        return users.stream().map(dtoService::toMiniUserDto).toList();
    }

    @PutMapping("/new")
    public HardwareInfoDto create(@RequestBody HardwareCreateRequest request) {
        var hw = new HardwareId();
        hw.setBitness(request.bitness());
        hw.setTotalMemory(request.totalMemory());
        hw.setLogicalProcessors(request.logicalProcessors());
        hw.setPhysicalProcessors(request.physicalProcessors());
        hw.setProcessorMaxFreq(request.processorMaxFreq());
        hw.setBattery(request.battery());
        hw.setHwDiskId(request.hwDiskId());
        hw.setDisplayId(request.displayId() == null ? null : Base64.getDecoder().decode(request.displayId()));
        hw.setBaseboardSerialNumber(request.baseboardSerialNumber());
        hw.setPublicKey(Base64.getDecoder().decode(request.publicKey()));
        hardwareIdService.save(hw);
        return new HardwareInfoDto(hw);
    }

    public record HardwareCreateRequest(int bitness,
                                        long totalMemory,
                                        int logicalProcessors,
                                        int physicalProcessors,
                                        long processorMaxFreq,
                                        boolean battery,
                                        String hwDiskId,
                                        String displayId,
                                        String baseboardSerialNumber, String publicKey) {
    }

    public record HardwareSearchRequest(int bitness,
                                        long totalMemory,
                                        int logicalProcessors,
                                        int physicalProcessors,
                                        long processorMaxFreq,
                                        boolean battery,
                                        String hwDiskId,
                                        String displayId,
                                        String baseboardSerialNumber) {
    }

    public record SetPublicKeyRequest(String publicKey) {
    }


}
