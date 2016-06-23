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
 * ���ڲ������ֵ�service
 * 
 * ÿ��1s ����һ���������ֽ��ȵĹ㲥
 */
public class PlayMusicService extends Service{
	private MediaPlayer player;
	private boolean isLoop=true;

	
	//ִ��1��onCreate
	@Override 
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
		player.setLooping(true);
		//�����������ֽ��ȵĹ����߳�
		new UpdateProgressThread().start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}
	
	/**
	 * �̳�Binder    Binderʵ����IBinder
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
			//  ���ŵ�ǰ����   
			try {
				player.reset();
				player.setDataSource(path);
				player.prepare();
				player.start();
				//�����Ѿ���ʼ����  �������ֿ�ʼ���ŵĹ㲥
				Intent intent = new Intent(Consts.ACTION_MUSIC_START_PLAY);
				sendBroadcast(intent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * �������ֽ��ȵ��߳�
	 */
	class UpdateProgressThread extends Thread{
		public void run() {
			while(isLoop){
				try {
					Thread.sleep(1000);
					//��ȡ��ǰ���ŵ�״̬
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
		//�ͷ���Դ
		isLoop=false;
		player.release();
	}
	
	/**
	 * �ṩ�ӿ�  ��Acitivity����
	 */
	public interface IMusicPlayer extends Serializable{
		/**
		 * ���Ż���ͣ
		 */
		void playOrPause();
		
		/**
		 * ���ŵ�ǰ����
		 */
		void playCurrentMusic(String path);
		
		/**
		 * ��ת��ĳһ��λ�� ��������
		 */
		void seekTo(int position);
	}
	
}





