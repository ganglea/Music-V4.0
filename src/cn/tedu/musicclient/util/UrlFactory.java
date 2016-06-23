package cn.tedu.musicclient.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Url地址的工厂
 */
public class UrlFactory {
	/**
	 * 获取查询新歌榜的url地址
	 * @return
	 */
	public static String getNewMusicListUrl(int offset, int size){
		return "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offset+"&size="+size;
	}

	/**
	 * 获取查询热歌榜的url地址
	 * @return
	 */
	public static String getHotMusicListUrl(int offset, int size){
		return "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=2&offset="+offset+"&size="+size;
	}

	/**
	 * 通过songId 获取该歌曲的基本信息
	 * @param songId
	 * @return
	 */
	public static String getSongInfoUrl(String songId){
		return "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="+songId+"&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
	}
	
	/**
	 * 返回搜索音乐接口路径
	 * @param keyword 关键字
	 * @return
	 */
	public static String getSearchSongUrl(String keyword){
		try {
			keyword = URLEncoder.encode(keyword,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.search.common&format=json&query="+keyword+"&page_no=1&page_size=30";
	}
	
}
