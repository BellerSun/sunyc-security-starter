package cn.sunyc.security.utils;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

/**
 * 用来获取资源文件夹内properties的key对应值的工具类。
 */
public class PropertiesUtil {
    private static Hashtable<String, Properties> ppMap = new Hashtable<>();
    @SuppressWarnings("unchecked")
    private static String dir;

    static {
        URL resource = PropertiesUtil.class.getClassLoader().getResource("");
        dir = resource == null ? "" : resource.getPath();
    }

    /**
     * 获取资源文件夹内的properties文件中的一个属性的值
     * @param fileName resources下的文件名
     * @param propertiesKey 要获取的key
     * @return key对应的值
     */
    public static String getValue(String fileName, String propertiesKey) {
        Properties pp = ppMap.get(fileName);
        if (pp == null) {
            String path = dir + fileName + ".properties";
            try {
                FileInputStream inputStream = new FileInputStream(path);
                pp = new Properties();
                pp.load(inputStream);
                ppMap.put(fileName, pp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pp == null ? null : pp.getProperty(propertiesKey);
    }

    /**
     * 清除缓存
     */
    public static void dispose() {
        ppMap.clear();
    }

}
