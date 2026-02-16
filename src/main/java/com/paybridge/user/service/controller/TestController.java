package com.paybridge.user.service.controller;


import com.paybridge.user.service.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("api/v1/test")
public class TestController {

    @GetMapping("/downstream/1")
    public ResponseEntity<?> downstream1() {
        ApiResponse apiResponse = ApiResponse.success(
                "Success",
                "Response from Downstream Service 1 (User Service)"
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/downstream/2")
    public ResponseEntity<?> downstream2() {
        ApiResponse apiResponse = ApiResponse.success(
                "Success",
                "Response from Downstream Service 2 (User Service)"
        );
        return ResponseEntity.ok(apiResponse);
    }
}
