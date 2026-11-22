package com.hotel.controller.api;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.LoaiPhongRequestDTO;
import com.hotel.dto.LoaiPhongResponseDTO;
import com.hotel.service.LoaiPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/loai-phong")
@RequiredArgsConstructor
@Validated
public class LoaiPhongRestController {
    private final LoaiPhongService loaiPhongService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoaiPhongResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách loại phòng thành công.",
                loaiPhongService.getAllLoaiPhong()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoaiPhongResponseDTO>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy loại phòng thành công.",
                loaiPhongService.getLoaiPhongById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoaiPhongResponseDTO>> create(
            @Valid @RequestBody LoaiPhongRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Tạo loại phòng thành công.",
                loaiPhongService.createLoaiPhong(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoaiPhongResponseDTO>> update(
            @PathVariable Integer id,
            @Valid @RequestBody LoaiPhongRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật loại phòng thành công.",
                loaiPhongService.updateLoaiPhong(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        loaiPhongService.deleteLoaiPhong(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Xóa loại phòng thành công.", null));
    }
}
