/**
 * description:
 * create by penkee
 * date:2013-8-4
 */


import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.fastcloud.domain.OrderBean;
import com.fastcloud.service.OrderServiceImpl;

/**
 * @author Administrator
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class TestUserService {
	@Autowired
	OrderServiceImpl orderService;
	
	@Test
	public void insert(){
		for (int i = 0; i < 50; i++) {
			//orderService.insert("洪泽湖大闸蟹"+i+"个", "43", new Date());
		}
	}
	
	@Test
	public void find(){
		OrderBean order=orderService.findOne("12114743556862922");
		Assert.isTrue(order.getProductName().equals("洪泽湖大闸蟹20个"));
	}
	
	@Test
	public void queryPage(){
		int i=1,size=10;
		Pageable page = new PageRequest(i, size);
		Page<Map<String, Object>> pageRes=orderService.queryByUserId("43", page);
		do{
			if(pageRes!=null){
				for (Map<String, Object> map : pageRes.getContent()) {
					System.out.println(map);
				}
				if(pageRes.getContent().size()==size){
					pageRes=orderService.queryByUserId("43", new PageRequest(++i, size));
				}else{
					pageRes=null;
				}
			}
		}while(pageRes!=null&&pageRes.getContent().size()>0);
	}
	
	@Test
	public void queryPageSort(){
		int i=1,size=10;
		Pageable page = new PageRequest(i, size);
		Page<Map<String, Object>> pageRes=orderService.queryByUserIdSort("43", page);
		do{
			if(pageRes!=null){
				for (Map<String, Object> map : pageRes.getContent()) {
					System.out.println(map);
				}
				System.out.println();
				if(pageRes.getContent().size()==size){
					pageRes=orderService.queryByUserIdSort("43", new PageRequest(++i, size));
				}else{
					pageRes=null;
				}
			}
		}while(pageRes!=null&&pageRes.getContent().size()>0);
	}
}
