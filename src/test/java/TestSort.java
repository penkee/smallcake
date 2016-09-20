import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.fastcloud.utils.DistSearchPageHelperer;

/**
 * 
 */

/**
 * @brief 这是一个Java API注解的例子（必填）
 * @details （必填）
 * @author 彭堃
 * @date 2016年9月20日下午6:22:17
 */
public class TestSort {
	@Test
	public void queryPageSort() throws ParseException{
		DistSearchPageHelperer ds=new DistSearchPageHelperer();
		
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Map<String, Object> item1=new ConcurrentHashMap<String, Object>();
		item1.put("createDate", DateUtils.parseDate("2016-05-01", "yyyy-MM-dd"));
		item1.put("id", "11111");
		
		Map<String, Object> item2=new ConcurrentHashMap<String, Object>();
		item2.put("createDate", DateUtils.parseDate("2016-06-01", "yyyy-MM-dd"));
		item2.put("id", "22222");
		
		list.add(item1);
		list.add(item2);
		
		ds.sort(list, new String[]{"createDate desc","id"});
		
		for (Map<String, Object> map : list) {
			System.out.println(map);
		}
	}
}
