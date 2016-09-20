/**
 * 
 */
package com.fastcloud.service;

import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fastcloud.domain.OrderBean;

/**
 * @brief 这是一个Java API注解的例子（必填）
 * @details （必填）
 * @author 彭堃
 * @date 2016年9月8日上午11:47:16
 */
public interface IOrderService {
	OrderBean findOne(String id);
	/**
	 * 
	 * @brief 根据用户id查询订单
	 * @details （必填）
	 * @param userId
	 * @param page
	 * @return
	 * @author 彭堃
	 * @date 2016年9月8日下午2:36:20
	 */
	public Page<Map<String, Object>> queryByUserId(String userId,Pageable page);
	
	public Page<Map<String, Object>> queryByUserIdSort(String userId,Pageable page);
	OrderBean insert(String productName,String userId,Date createDate);
}
