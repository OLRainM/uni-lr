package com.tibetan.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tibetan.medicine.entity.Address;
import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService extends IService<Address> {
    
    /**
     * 获取地址列表
     */
    List<Address> getAddressList(Long userId);
    
    /**
     * 添加地址
     */
    void addAddress(Address address);
    
    /**
     * 更新地址
     */
    void updateAddress(Address address);
    
    /**
     * 删除地址
     */
    void deleteAddress(Long id);
    
    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long id);
}
