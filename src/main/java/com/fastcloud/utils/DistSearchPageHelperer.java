package com.fastcloud.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fastcloud.domain.DistPage;

/**
 * @date 2008-12-12
 * @author lfh
 * 锟侥筹拷jdbcTemplate
 * modified by penkee 2014-5-9
 */
public class DistSearchPageHelperer {
	private static Logger logger=Logger.getLogger(DistSearchPageHelperer.class);
	
	public  Page<Map<String,Object>> executeQueryByPage(String sqlStr, String orderby,Pageable page,Object[] para,List<JdbcTemplate> jdbcList){
        int allRowCount = 0; 
        int[] distDataSize=new int[jdbcList.size()];//每个节点的记录数
        int pageSize=page.getPageSize();
        int pageNum=page.getPageNumber();
        
        Page<Map<String,Object>> resPage=null;
            String sqlcount = "select count(*) from ("+sqlStr+") a";
            //排序
            long sTime=System.currentTimeMillis();
            int i=0;
			for(JdbcTemplate jdbcTemplate:jdbcList){
               int rc= (Integer)jdbcTemplate.queryForObject(sqlcount,para, Integer.class);
               allRowCount+=rc;
               distDataSize[i++]=rc;
            }
            
            int pageCount = (int)Math.ceil((double)allRowCount/pageSize);//一共页数
            if(pageCount<=0){
            	return new PageImpl<Map<String,Object>>(null, page, allRowCount);
            }
            
            List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
            //有排序
            if(!StringFunc.isNull(orderby)){
            	String sql=sqlStr+" order by "+orderby+" limit "+ pageNum * pageSize;    
                //排序
                sTime=System.currentTimeMillis();
                for(JdbcTemplate jdbcTemplate:jdbcList){
                	 List<Map<String, Object>> currList=jdbcTemplate.queryForList(sql,para);
                	 
                	 if(currList!=null&&currList.size()>0){
                		 list.addAll(currList);
                	 }
                }
                //排序，选择前(page-1)*size+1到page*size个
                //解析order by字符串  XXX或者  XXX,XXX或者XXX desc,XXX
                String[] ors= orderby.split(",");
                sort(list,ors);
                logger.info("执行排序的耗时："+(System.currentTimeMillis()-sTime)+"ms，"+sql);
                
                if(list.size()>=(pageNum-1)*pageSize){
                	if(list.size()>=pageNum*pageSize){
                		resPage= new PageImpl<Map<String,Object>>(list.subList((pageNum-1)*pageSize, pageNum*pageSize), page, allRowCount);
                	}else{
                		resPage= new PageImpl<Map<String,Object>>(list.subList((pageNum-1)*pageSize,list.size()), page, allRowCount);
                	}
                }else{
                	resPage= new PageImpl<Map<String,Object>>(null, page, allRowCount);
                }
            }
            else{
                sTime=System.currentTimeMillis();
                //非排序，平均分，不够延续到下一个取
                DistPage[] distPage=getDistPageNoSort(pageNum,pageSize,distDataSize);
                for(DistPage dp:distPage){
                	if(dp!=null&&dp.isNeed){
                		JdbcTemplate jdbcTemplate=jdbcList.get(dp.distDBID);
                		//计算起点
                		String sql=sqlStr+" limit "+dp.startIndex+","+dp.size;
                        List<Map<String, Object>> currList=jdbcTemplate.queryForList(sql,para);
                   	 
    	               	if(currList!=null&&currList.size()>0){
    	               		list.addAll(currList);
    	               	}
                	}
                }
                resPage= new PageImpl<Map<String,Object>>(list, page, allRowCount);
                logger.info("执行不排序耗时："+(System.currentTimeMillis()-sTime)+"ms");
            }
                   
        return resPage;
    }
    /**
     * @param list
     * @param ors 
     * added by cruze(penkee@163.com) at 2014-9-24
     */
	public void sort(List<Map<String, Object>> list, String[] ors) {
		// TODO Auto-generated method stub
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				Map<String, Object> a = list.get(i);
				Map<String, Object> b = list.get(j);
				if (isChange(a, b, ors, 0)) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
	}
    /**
     * 指示是否换，递归
     * @param a
     * @param b
     * @param ors
     * @return 
     * added by cruze(penkee@163.com) at 2014-9-24
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
	boolean isChange(Map<String, Object> a, Map<String, Object> b,
			String[] ors, int i) {
		if (i > ors.length - 1)
			return false;
		String o1 = ors[i];
		boolean isDesc = false;
		if (o1.toLowerCase().contains(" desc")) {
			isDesc = true;
		}

		String key = o1.replace(" desc", "").trim();

		Object aV = a.get(key);
		Object bV = b.get(key);

		int res=0;
		try {
			Method method=aV.getClass().getMethod("compareTo", aV.getClass());
			res = (Integer)method.invoke(aV, bV);
		} catch (Exception e) {
			return false;
		}
		if (res < 0) {
			return isDesc;
		} else if (res == 0) {
			return isChange(a, b, ors, ++i);
		} else {
			return !isDesc;
		}
	}
	
	public DistPage[] getDistPageNoSort(int pageNum,int pageSize,int[] distDataSize){
		//需要计算的，每个db的开始位置和取的数量
		DistPage[] dp=new DistPage[distDataSize.length];
		
		//如果直到取pageNum时，每个DB能独立给出页，那么不用计算
		int minCanPageNum=Integer.MAX_VALUE;
 		for (int dbsize : distDataSize) {
			int tmp=dbsize/pageSize;
  			if(tmp<minCanPageNum){
				minCanPageNum=tmp;
			}
		}
		//即每个DB能提供minCanPageNum个页
 		if(minCanPageNum*distDataSize.length>=pageNum){
			int dbi=(pageNum-1)%distDataSize.length;//选中第几个DB
			int pcount=(pageNum-1)/distDataSize.length;//跳过的页
			
			dp[dbi]=new DistPage();
			dp[dbi].distDBID=dbi;
			dp[dbi].startIndex=pcount*pageSize;
			dp[dbi].size=pageSize;
			dp[dbi].isNeed=true;
			
			logger.info("结果查询第"+pageNum+"页：db-"+dbi+"查询第"+dp[dbi].startIndex+"-"+(dp[dbi].size+dp[dbi].startIndex-1));
			return dp;
		}else{
			if(minCanPageNum>0){
				//初始化dp
 				for (int j = 0; j < dp.length; j++) {
					dp[j]=new DistPage();
					dp[j].distDBID=j;
					dp[j].startIndex=pageSize*(minCanPageNum-1);
 					dp[j].size=pageSize;
					dp[j].remain=distDataSize[j]-pageSize*minCanPageNum;
					dp[j].isNeed=false;
				}
			}else{
				//初始化dp
				for (int j = 0; j < dp.length; j++) {
					dp[j]=new DistPage();
					dp[j].distDBID=j;
					dp[j].startIndex=0;
					dp[j].size=0;
					dp[j].remain=distDataSize[j];
					dp[j].isNeed=false;
				}
			}
		}
		//开始计算部分DB带有提供不足，其他DB补的情况下
		for (int i = minCanPageNum*distDataSize.length+1; i <=pageNum; i++) {
			for (int j = 0; j < dp.length; j++) {
				if(dp[j]!=null){
					dp[j].isNeed=false;
				}
			}
			//当前的头
			int head=(i-1)%distDataSize.length;
			
			//如果当前取整页不足，则下一个去补，直到一个循环
			if(dp[head].remain<pageSize){
				dp[head].startIndex+=dp[head].size;
				dp[head].size=dp[head].remain;
				dp[head].remain=0;
				dp[head].isNeed=true;
				//库存没就不需要此节点提供数据
				if(dp[head].size<=0){
					dp[head].isNeed=false;
				}
				
				int findNext=head;
				int needGet=pageSize-dp[head].size;
				do{
					findNext=(findNext+1)%distDataSize.length;
					if(dp[findNext].remain<=0)continue;//无法提供数据
					
					if(dp[findNext].remain<needGet){
						dp[findNext].startIndex+=dp[findNext].size;
						dp[findNext].size=dp[findNext].remain;
						dp[findNext].remain=0;
						dp[findNext].isNeed=true;
						needGet-=dp[findNext].size;
					}else{
						dp[findNext].startIndex+=dp[findNext].size;
						dp[findNext].size=needGet;
						dp[findNext].remain-=needGet;
						dp[findNext].isNeed=true;
						break;
					}
				}while(head!=findNext);
			}else{
				dp[head].startIndex+=dp[head].size;//加上一个的size得出当前start
				dp[head].size=pageSize;
				dp[head].remain-=pageSize;
				dp[head].isNeed=true;
			}
			
			for (DistPage distPage : dp) {
				if(distPage!=null&&distPage.isNeed)
					logger.info("结果查询第"+pageNum+"页：需要经过查询第  "+i+"页的db-"+distPage.distDBID+"查询第"+distPage.startIndex+"-"+(distPage.size+distPage.startIndex-1));
			}
		}
		
		for (DistPage distPage : dp) {
			if(distPage!=null&&distPage.isNeed)
				logger.info("结果查询第"+pageNum+"页： db-"+distPage.distDBID+"查询第"+distPage.startIndex+"-"+(distPage.size+distPage.startIndex-1));
		}
		return dp;
	}
}

