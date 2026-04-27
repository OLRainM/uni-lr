package com.tibetan.medicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tibetan.medicine.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址Mapper
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
