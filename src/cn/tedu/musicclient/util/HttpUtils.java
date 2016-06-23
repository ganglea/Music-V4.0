package cn.tedu.musicclient.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.nfc.tech.IsoDep;

/**
 * 用于发送http请求的工具类
 */
public class HttpUtils {
	
	/**
	 * 使用post的方式发送http请求  
	 * @param uri  请求资源路径
	 * @param params  请求参数
	 * @return
	 * @throws MalformedURLException 
	 */
	public static InputStream postInputStream(String uri, Map<String, String> params) throws IOException{
		URL url = new URL(uri);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		//设置参数
		StringBuilder param=new StringBuilder();
		Set<String> keySet = params.keySet();
		Iterator<String> ite = keySet.iterator();
		while(ite.hasNext()){
			String key=ite.next();
			String val=params.get(key);
			param.append(key+"="+val+"&");
		}
		//param:    a=b&c=d&
		param.deleteCharAt(param.length()-1);
		//传递参数
		OutputStream os = conn.getOutputStream();
		os.write(param.toString().getBytes("utf-8"));
		os.flush();
		//获取输入流
		return conn.getInputStream();
	}
	
	/**
	 * 发送get请求  获取输入流 
	 * @param uri  完整的请求资源路径
	 * @return
	 * @throws MalformedURLException 
	 * @throws IOException 
	 */
	public static InputStream getInputStream(String uri) throws IOException{
		//1.  URL
		URL url = new URL(uri);
		//2. openConnection
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		//3. GET
		conn.setRequestMethod("GET");
		//4.  getInputStream
		InputStream is = conn.getInputStream();
		return is;
	}
	
	/**
	 * 发送get请求 获取响应字符串
	 * @param uri
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static String getString(String uri) throws MalformedURLException, IOException{
		InputStream is = getInputStream(uri);
		//把输入流中的内容转成字符串
		StringBuilder sb = new StringBuilder();
		String line=null;
		BufferedReader reader=new BufferedReader(new InputStreamReader(is));
		while((line=reader.readLine())!=null){
			sb.append(line);
		}
		String respText=sb.toString();
		return respText;
	}
}





