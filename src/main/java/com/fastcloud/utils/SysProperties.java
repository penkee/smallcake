/**
 *SysProperties.java
	Description:
 */
package com.fastcloud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author Pengkun (penkee@163.com)
 *	2014-8-11
 */
public class SysProperties {
	public static Properties pro = null;
    public static String configFile = "system.properties";
    static {
    	reload();
    }
    /**
     * 根据Key获取设置的值
     * added by penkee(penkee@163.com) at 2014-3-11
     */
    public static String getValue(String key){
      return pro.getProperty(key);
    }
    public static void reload(){
    	pro = new Properties();
        ClassLoader loader = SysProperties.class.getClassLoader();
        InputStream inStream = loader.getResourceAsStream(configFile);
        try {
      	InputStreamReader  reader=new InputStreamReader(inStream,"UTF-8");
          pro.load(reader);
          inStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}
