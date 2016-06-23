package cn.tedu.musicclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import cn.tedu.musicclient.entity.Song;

/**
 * 解析XML的工具类
 */
public class MusicXmlParser {

	/**
	 * 解析音乐集合    xml：
	 * 使用pull解析方式  
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public static List<Song> parseMusicList(InputStream is) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is,"utf-8");
		int eventType=parser.getEventType();
		List<Song> songs = null;
		Song song = null;
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch (eventType) {
			case XmlPullParser.END_TAG: //结束标签
				String tagName=parser.getName(); //标签名称
				if("song".equals(tagName)){
					//吧song对象添加到集合中
					songs.add(song);
				}
				break;
			case XmlPullParser.START_TAG://开始标签
				tagName=parser.getName(); //标签名称
				if("song_list".equals(tagName)){ //
					songs = new ArrayList<Song>();
				}else if("song".equals(tagName)){
					song = new Song();
				}else if("pic_big".equals(tagName)){
					song.setPic_big(parser.nextText());
				}else if("pic_small".equals(tagName)){
					song.setPic_small(parser.nextText());
				}else if("lrclink".equals(tagName)){
					song.setLrclink(parser.nextText());
				}else if("style".equals(tagName)){
					song.setStyle(parser.nextText());
				}else if("song_id".equals(tagName)){
					song.setSong_id(parser.nextText());
				}else if("title".equals(tagName)){
					song.setTitle(parser.nextText());
				}else if("author".equals(tagName)){
					song.setAuthor(parser.nextText());
				}else if("album_title".equals(tagName)){
					song.setAlbum_title(parser.nextText());
				}else if("artist_name".equals(tagName)){
					song.setArtist_name(parser.nextText());
				}
				break;
			}
			//驱动事件
			eventType = parser.next();
		}
		
		return songs;
	}

}
