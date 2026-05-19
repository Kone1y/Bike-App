package com.bike.module.bike.controller;

import com.bike.common.annotation.Log;
import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import com.bike.mapper.BikeMapper;
import com.bike.module.bike.entity.Bike;
import com.bike.security.annotation.RequirePermission;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bike")
@RequiredArgsConstructor
public class BikeController {

    private final BikeMapper bikeMapper;

    @GetMapping("/list")
    @Log("查询车辆列表")
    @RequirePermission("bike:list")
    public Result<List<Bike>> list(@RequestParam(required = false) String bikeCode,
                                    @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) Long areaId) {
        return Result.success(bikeMapper.findList(bikeCode, status, areaId));
    }

    @GetMapping("/{id}")
    @RequirePermission("bike:list")
    public Result<Bike> getById(@PathVariable Long id) {
        return Result.success(bikeMapper.findById(id));
    }

    @PostMapping
    @Log("新增车辆")
    @RequirePermission("bike:add")
    public Result<Void> add(@RequestBody Bike bike) {
        if (bike.getStatus() == null) {
            bike.setStatus(0);
        }
        bikeMapper.insert(bike);
        return Result.success();
    }

    @PutMapping
    @Log("编辑车辆")
    @RequirePermission("bike:edit")
    public Result<Void> edit(@RequestBody Bike bike) {
        bikeMapper.update(bike);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log("删除车辆")
    @RequirePermission("bike:delete")
    public Result<Void> delete(@PathVariable Long id) {
        Bike bike = bikeMapper.findById(id);
        if (bike != null && bike.getStatus() == 1) {
            throw new BusinessException("使用中的车辆不能删除");
        }
        bikeMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Log("变更车辆状态")
    @RequirePermission("bike:status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Bike bike = bikeMapper.findById(id);
        if (bike == null) {
            throw new BusinessException("车辆不存在");
        }
        if (bike.getStatus() == 1 && status != 2) {
            throw new BusinessException("使用中的车辆只能变更为报修状态");
        }
        bikeMapper.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}/qrcode")
    @RequirePermission("bike:list")
    public Result<Map<String, String>> generateQrCode(@PathVariable Long id) {
        Bike bike = bikeMapper.findById(id);
        if (bike == null) {
            throw new BusinessException("车辆不存在");
        }
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(bike.getBikeCode(), BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "PNG", out);
            String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
            Map<String, String> result = new HashMap<>();
            result.put("image", base64);
            result.put("bikeCode", bike.getBikeCode());
            return Result.success(result);
        } catch (Exception e) {
            throw new BusinessException("二维码生成失败");
        }
    }
}
