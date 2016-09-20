/**
 * description:
 * create by penkee
 * date:2013-8-4
 */


import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
			orderService.insert("洪泽湖大闸蟹"+i+"个", "43", new Date());
		}
	}
}
