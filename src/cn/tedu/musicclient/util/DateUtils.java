package cn.tedu.musicclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期操作相关的工具类
 */
public class DateUtils {
	public static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
	/**
	 * 通过time  解析为mm:ss格式的字符串
	 * @param time
	 * @return
	 */
	public static String parseTime(long time){
		return sdf.format(new Date(time));
	}
}
