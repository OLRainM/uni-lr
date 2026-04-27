package com.tibetan.medicine.controller;

import com.tibetan.medicine.common.Result;
import com.tibetan.medicine.entity.Address;
import com.tibetan.medicine.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> getAddressList(@RequestHeader("Authorization") String token) {
        Long userId = 1L;
        List<Address> addresses = addressService.getAddressList(userId);
        return Result.success(addresses);
    }
    
    /**
     * 添加地址
     */
    @PostMapping("/add")
    public Result<Void> addAddress(@RequestHeader("Authorization") String token,
                                   @RequestBody Address address) {
        address.setUserId(1L);
        addressService.addAddress(address);
        return Result.success();
    }
    
    /**
     * 更新地址
     */
    @PostMapping("/update")
    public Result<Void> updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return Result.success();
    }
    
    /**
     * 删除地址
     */
    @PostMapping("/delete/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return Result.success();
    }
    
    /**
     * 设置默认地址
     */
    @PostMapping("/setDefault/{id}")
    public Result<Void> setDefaultAddress(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id) {
        Long userId = 1L;
        addressService.setDefaultAddress(userId, id);
        return Result.success();
    }
}
