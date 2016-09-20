/**
 * 
 */
package com.fastcloud.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.fastcloud.utils.SysProperties;

/**
 * @brief 这是一个Java API注解的例子（必填）
 * @details （必填）
 * @author 彭堃
 * @date 2016年9月8日上午11:45:40
 */
public class BaseService extends ApplicationObjectSupport{
	/**
	 * 
	 * @brief 获取序列
	 * @param seq
	 * @param machineId 机器id
	 * @return
	 * @author 彭堃
	 * @date 2016年9月8日上午11:46:29
	 */
	public String getSeq(AtomicInteger seq,String machineId){
		String strSeq=String.valueOf(Math.abs(seq.addAndGet(1))%100);//0-99

		if(strSeq.length()<2){
			strSeq="00".substring(0,2-strSeq.length())+strSeq;
		}
		String time=String.valueOf(System.currentTimeMillis());
		
		return machineId+strSeq+time.substring(0,time.length()-2)+new Random().nextInt(10)+time.substring(time.length()-2);
	}
	/**
	 * @brief 获取jdbc工具类
	 * @details （必填）
	 * @param id
	 * @return
	 * @author 彭堃
	 * @date 2016年9月8日下午1:41:12
	 */
	public JdbcTemplate getJdbcTemplate(String id) {
	    Assert.notNull(id);
	    
	    int unitID=Integer.parseInt(id.substring(11))%1000+1;//1-1000之间
	    int dbCount=Integer.parseInt(SysProperties.getValue("dbCount"));
	    
	    JdbcTemplate jdbcTemplate=null;
	    for (int i = 0; i < dbCount; i++) {
	    	String dbi=SysProperties.getValue("db"+i);
	    	String[] limit=dbi.split("-");
	    	
	    	if(unitID>=Integer.parseInt(limit[0])&&unitID<=Integer.parseInt(limit[1])){
	    		jdbcTemplate = (JdbcTemplate) super.getApplicationContext().getBean("jdbcTemplate"+i);
	    		break;
	    	}
		}
	    Assert.notNull(jdbcTemplate);
		return jdbcTemplate;
	}
	
	/**
	 * @brief 获取所有jdbc工具类
	 * @details （必填）
	 * @param id
	 * @return
	 * @author 彭堃
	 * @date 2016年9月8日下午1:41:12
	 */
	public List<JdbcTemplate> getJdbcTemplateAll() {
	    int dbCount=Integer.parseInt(SysProperties.getValue("dbCount"));
	    
	    List<JdbcTemplate> jdbcTemplateList= new ArrayList<JdbcTemplate>(dbCount);
	    for (int i = 0; i < dbCount; i++) {
	    	JdbcTemplate jdbcTemplate=(JdbcTemplate) super.getApplicationContext().getBean("jdbcTemplate"+i);
	    	jdbcTemplateList.add(jdbcTemplate);
	    	
	    	Assert.notNull(jdbcTemplate);
		}
		return jdbcTemplateList;
	}
}
