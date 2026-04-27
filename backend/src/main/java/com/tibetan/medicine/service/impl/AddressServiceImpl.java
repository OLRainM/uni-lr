package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.Address;
import com.tibetan.medicine.mapper.AddressMapper;
import com.tibetan.medicine.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现类
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    
    @Override
    public List<Address> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.orderByDesc(Address::getIsDefault);
        wrapper.orderByDesc(Address::getCreateTime);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(address.getUserId());
        }
        this.save(address);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(address.getUserId());
        }
        this.updateById(address);
    }
    
    @Override
    public void deleteAddress(Long id) {
        this.removeById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long id) {
        // 取消其他默认地址
        cancelDefaultAddress(userId);
        
        // 设置当前地址为默认
        Address address = this.getById(id);
        if (address != null) {
            address.setIsDefault(1);
            this.updateById(address);
        }
    }
    
    /**
     * 取消默认地址
     */
    private void cancelDefaultAddress(Long userId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.eq(Address::getIsDefault, 1);
        wrapper.set(Address::getIsDefault, 0);
        this.update(wrapper);
    }
}
