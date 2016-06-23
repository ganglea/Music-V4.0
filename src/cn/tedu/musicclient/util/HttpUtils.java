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
 * ���ڷ���http����Ĺ�����
 */
public class HttpUtils {
	
	/**
	 * ʹ��post�ķ�ʽ����http����  
	 * @param uri  ������Դ·��
	 * @param params  �������
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
		//���ò���
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
		//���ݲ���
		OutputStream os = conn.getOutputStream();
		os.write(param.toString().getBytes("utf-8"));
		os.flush();
		//��ȡ������
		return conn.getInputStream();
	}
	
	/**
	 * ����get����  ��ȡ������ 
	 * @param uri  ������������Դ·��
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
	 * ����get���� ��ȡ��Ӧ�ַ���
	 * @param uri
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static String getString(String uri) throws MalformedURLException, IOException{
		InputStream is = getInputStream(uri);
		//���������е�����ת���ַ���
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





