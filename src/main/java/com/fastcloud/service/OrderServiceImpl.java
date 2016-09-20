/**
 * 
 */
package com.fastcloud.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fastcloud.domain.OrderBean;
import com.fastcloud.utils.DistSearchPageHelperer;
import com.fastcloud.utils.SysProperties;

/**
 * @brief 这是一个Java API注解的例子（必填）
 * @details （必填）
 * @author 彭堃
 * @date 2016年9月8日上午11:52:41
 */
@Service
public class OrderServiceImpl extends BaseService implements IOrderService{
	private static final AtomicInteger seq=new AtomicInteger(0);//2位  0-99
	/* (non-Javadoc)
	 * @see com.fastcloud.service.IOrderService#findOne(java.lang.String)
	 */
	@Override
	public OrderBean findOne(String id) {
		JdbcTemplate jdbcTemplate= super.getJdbcTemplate(id);
		   
	    return jdbcTemplate.queryForObject("select * from biz_order where id=", OrderBean.class,id);
	}
	/* (non-Javadoc)
	 * @see com.fastcloud.service.IOrderService#queryByUserId(java.lang.String)
	 */
	@Override
	public Page<Map<String, Object>> queryByUserId(String userId,Pageable page) {
		// TODO Auto-generated method stub
		List<JdbcTemplate> jdbcTemplateList=super.getJdbcTemplateAll();
		
		Object[] para=new Object[1];
		para[0]="43";
		Page<Map<String, Object>> pageRes=new DistSearchPageHelperer().executeQueryByPage("select * from biz_order where userId=?1", "", page, para, jdbcTemplateList);
		//Page p=new PageImpl(list, page, all);
		return pageRes;
	}

	/* (non-Javadoc)
	 * @see com.fastcloud.service.IOrderService#insert(java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public OrderBean insert(String productName, String userId, Date createDate) {
		// TODO Auto-generated method stub
		OrderBean order=new OrderBean();
		order.setId(super.getSeq(seq, SysProperties.getValue("machineId")));
		order.setCreateDate(new Date());
		order.setProductName(productName);
		order.setUserId(userId);
		
		JdbcTemplate jdbcTemplate= super.getJdbcTemplate(order.getId());
		
		jdbcTemplate.update("INSERT INTO `biz_order` (`id`, `productName`, `userId`, `createDate`) VALUES (?,?,?,?)",
		        new Object[]{
				order.getId(),
				order.getProductName(),
				order.getUserId(),
				order.getCreateDate()
		    });
		return order;
	}

}
