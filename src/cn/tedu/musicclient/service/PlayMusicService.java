package cn.tedu.musicclient.service;

import java.io.IOException;
import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import cn.tedu.musicclient.util.Consts;

/**
 * 用于播放音乐的service
 * 
 * 每隔1s 发送一个更新音乐进度的广播
 */
public class PlayMusicService extends Service{
	private MediaPlayer player;
	private boolean isLoop=true;

	
	//执行1次onCreate
	@Override 
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
		player.setLooping(true);
		//启动更新音乐进度的工作线程
		new UpdateProgressThread().start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}
	
	/**
	 * 继承Binder    Binder实现了IBinder
	 */
	class MusicBinder extends Binder implements IMusicPlayer{
		
		@Override
		public void playOrPause() {
			if(player.isPlaying()){
				player.pause();
			}else{
				player.start();
			}
		}
		
		@Override
		public void seekTo(int position) {
			player.seekTo(position);
		}
		
		@Override
		public void playCurrentMusic(String path) {
			//  播放当前音乐   
			try {
				player.reset();
				player.setDataSource(path);
				player.prepare();
				player.start();
				//音乐已经开始播放  发送音乐开始播放的广播
				Intent intent = new Intent(Consts.ACTION_MUSIC_START_PLAY);
				sendBroadcast(intent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 更新音乐进度的线程
	 */
	class UpdateProgressThread extends Thread{
		public void run() {
			while(isLoop){
				try {
					Thread.sleep(1000);
					//获取当前播放的状态
					if(player.isPlaying()){
						int current=player.getCurrentPosition();
						int total=player.getDuration();
						Intent intent=new Intent(Consts.ACTION_MUSIC_UPDATE_PROGRESS);
						intent.putExtra("current", current);
						intent.putExtra("total", total);
						sendBroadcast(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//释放资源
		isLoop=false;
		player.release();
	}
	
	/**
	 * 提供接口  供Acitivity调用
	 */
	public interface IMusicPlayer extends Serializable{
		/**
		 * 播放或暂停
		 */
		void playOrPause();
		
		/**
		 * 播放当前歌曲
		 */
		void playCurrentMusic(String path);
		
		/**
		 * 跳转到某一个位置 继续播放
		 */
		void seekTo(int position);
	}
	
}





