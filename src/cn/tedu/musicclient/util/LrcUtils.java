package cn.tedu.musicclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import cn.tedu.musicclient.entity.LrcLine;

/**
 * ������ʵĹ�����
 */
public class LrcUtils {
	
	/**
	 * �Ѹ����Ϣ�������ļ���
	 * @param lines
	 * @param file
	 * @throws IOException 
	 */
	public static void save(List<LrcLine> lines, File file) throws IOException{
		if(file.exists()){ //�Ѿ������
			return;
		}
		//��lines�е�ÿһ������ ��д���ļ�
		PrintWriter out =new PrintWriter(file);
		for(int i=0; i<lines.size(); i++){
			String content=lines.get(i).getData();
			out.println(content);
		}
		out.flush();
		out.close();
	}
	
	/**
	 * ��ȡĳ���ļ���ȡ������ 
	 * @param path
	 */
	public static InputStream loadLrcByFilePath(String path) throws IOException{
		File file = new File(path);
		if(!file.exists()){ //������û�и���ļ�
			return null; 
		}
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}
		
}





