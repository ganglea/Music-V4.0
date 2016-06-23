package cn.tedu.musicclient.app;

import java.util.List;

import cn.tedu.musicclient.entity.Song;

import android.app.Application;

/**
 * �嵥�ļ������ø�Application
 * ������Ӧ��ʱ �������� 
 */
public class MusicApplication extends Application {
	private List<Song> songList;
	private int position;
	
	private static MusicApplication app;
	
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
	}
	
	public static MusicApplication getApp(){
		return app;
	}

	public List<Song> getSongList() {
		return songList;
	}

	public void setSongList(List<Song> songList) {
		this.songList = songList;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public Song getCurrentSong(){
		return songList.get(position);
	}

}
