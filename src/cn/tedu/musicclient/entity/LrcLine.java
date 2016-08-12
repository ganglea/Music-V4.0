package cn.tedu.musicclient.entity;

import java.text.SimpleDateFormat;

/**
 * ����һ�и�� [00:00.91]��죺������ȥ
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
	 * ��ȡline �� [00:00.91]��죺������ȥ
	 * �������һ�ȡtime �� content
	 * �����Ը�ֵ
	 * @param line
	 */
	public void parse(String line){
		//����ʱ��
		if(line.indexOf(".")<0){
			return;
		}
		this.time=line.substring(line.indexOf("[")+1, line.indexOf("."));
		//����content
		this.content=line.substring(line.lastIndexOf("]")+1);
	}

	@Override
	public String toString() {
		return this.time+","+this.content+"\n";
	}
	
}
