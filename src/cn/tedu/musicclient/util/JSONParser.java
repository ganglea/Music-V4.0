package cn.tedu.musicclient.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.tedu.musicclient.entity.Song;
import cn.tedu.musicclient.entity.SongInfo;
import cn.tedu.musicclient.entity.SongUrl;

public class JSONParser {

	/**
	 * 通过解析json字符串 返回Song对象
	 * @param jsonString 
	 * { 
	 *   songurl:  [ {},{},{} ]
	 *   errorcode:
	 *   songinfo: {  }
	 * }
	 * @return
	 * @throws JSONException 
	 */
	public static Song parseMusicInfo(String jsonString) throws JSONException {
		JSONObject obj = new JSONObject(jsonString);
		JSONArray urlArray = obj.getJSONObject("songurl").getJSONArray("url");
		Song song = new Song();
		//封装集合 songUrls
		for(int i=0; i<urlArray.length(); i++){
			JSONObject urlObj=urlArray.getJSONObject(i);
			SongUrl url=new SongUrl(
					urlObj.getInt("file_duration"), 
					urlObj.getInt("file_bitrate"), 
					urlObj.getString("show_link"), 
					urlObj.getString("song_file_id"), 
					urlObj.getString("file_size"), 
					urlObj.getString("file_extension"), 
					urlObj.getString("file_link"));
			song.getUrls().add(url);
		}
		//封装对象  songInfo
		JSONObject infoObj=obj.getJSONObject("songinfo");
		SongInfo info=new SongInfo(
				infoObj.getString("album_1000_1000"), 
				infoObj.getString("album_500_500"), 
				infoObj.getString("artist_500_500"), 
				infoObj.getString("album_title"), 
				infoObj.getString("title"), 
				infoObj.getString("lrclink"), 
				infoObj.getString("artist_480_800"), 
				infoObj.getString("artist_id"), 
				infoObj.getString("album_id"), 
				infoObj.getString("artist_640_1136"), 
				infoObj.getString("publishtime"), 
				infoObj.getString("author"), 
				infoObj.getString("song_id"));
		song.setSongInfo(info);
		return song;
	}

	/**
	 * 解析搜索出的json信息
	 * @param jsonString
	 * {pages:{rn_num:"", total:"x"}, song_list:[{},{},{},{}]}
	 * @return
	 * @throws JSONException 
	 */
	public static List<Song> parseSearchSongs(String jsonString) throws JSONException {
		List<Song> songs = new ArrayList<Song>();
		JSONObject jsonObj=new JSONObject(jsonString);
		String num=jsonObj.getJSONObject("pages").getString("rn_num");
		if(!"0".equals(num)){ //有数据 则解析
			JSONArray ary = jsonObj.getJSONArray("song_list");
			for(int i=0; i<ary.length(); i++){
				JSONObject obj=ary.getJSONObject(i);
				Song song = new Song(
						"", 
						"", 
						"", 
						"", 
						obj.getString("song_id"), 
						obj.getString("title"), 
						obj.getString("author"), 
						obj.getString("album_title"), 
						obj.getString("author"));
				songs.add(song);
			}
		}
		return songs;
	}

}
