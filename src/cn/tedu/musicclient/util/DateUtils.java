package cn.tedu.musicclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ���ڲ�����صĹ�����
 */
public class DateUtils {
	public static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
	/**
	 * ͨ��time  ����Ϊmm:ss��ʽ���ַ���
	 * @param time
	 * @return
	 */
	public static String parseTime(long time){
		return sdf.format(new Date(time));
	}
}
