package cn.tedu.musicclient.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述一行歌词 [00:00.91]阮经天：许多年过去
 */
public class LrcLine {
	private static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
	private String time;
	private String content;
	private String data;

	public LrcLine() {
	}

	public LrcLine(String time, String content) {
		super();
		this.time = time;
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	/**
	 * 获取line ： [00:00.91]阮经天：许多年过去
	 * 解析并且获取time 和 content
	 * 给属性赋值
	 * @param line
	 */
	public void parse(String line){
		//解析时间
		if(line.indexOf(".")<0){
			return;
		}
		this.time=line.substring(line.indexOf("[")+1, line.indexOf("."));
		//解析content
		this.content=line.substring(line.lastIndexOf("]")+1);
	}

	@Override
	public String toString() {
		return this.time+","+this.content+"\n";
	}
	
}
