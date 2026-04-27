package com.tibetan.medicine.dto;

import lombok.Data;

/**
 * 订单发货DTO
 */
@Data
public class OrderShipDTO {
    
    /**
     * 快递公司
     */
    private String shipCompany;
    
    /**
     * 快递单号
     */
    private String shipNo;
}
