package cn.tedu.musicclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import cn.tedu.musicclient.entity.LrcLine;

/**
 * 操作歌词的工具类
 */
public class LrcUtils {
	
	/**
	 * 把歌词信息保存在文件中
	 * @param lines
	 * @param file
	 * @throws IOException 
	 */
	public static void save(List<LrcLine> lines, File file) throws IOException{
		if(file.exists()){ //已经存过了
			return;
		}
		//把lines中的每一行数据 都写入文件
		PrintWriter out =new PrintWriter(file);
		for(int i=0; i<lines.size(); i++){
			String content=lines.get(i).getData();
			out.println(content);
		}
		out.flush();
		out.close();
	}
	
	/**
	 * 读取某个文件获取输入流 
	 * @param path
	 */
	public static InputStream loadLrcByFilePath(String path) throws IOException{
		File file = new File(path);
		if(!file.exists()){ //根本就没有歌词文件
			return null; 
		}
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}
		
}





