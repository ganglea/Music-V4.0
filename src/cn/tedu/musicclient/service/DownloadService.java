package cn.tedu.musicclient.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.tedu.musicclient.ui.R;
import cn.tedu.musicclient.util.HttpUtils;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Environment;

public class DownloadService extends IntentService{

	private static final int NOTIFICATION_ID = 1000;

	public DownloadService() {
		super("download");
	}
	
	public DownloadService(String name) {
		super(name);
	}

	/**
	 * 该方法中的内容将会在工作线程中执行
	 * 当轮到执行该消息时，将会运行onHandleIntent方法中的代码
	 */
	protected void onHandleIntent(Intent intent) {
		//获取Acitvity传来的参数
		String path=intent.getStringExtra("path");
		int version=intent.getIntExtra("version", 0);
		String title=intent.getStringExtra("title");
		String total=intent.getStringExtra("total");
		//执行下载操作  发送http请求
		try {
			//获取保存目录  /mnt/sdcard/Music/_128/title.mp3
			File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "_"+version+"/"+title+".mp3");
			//判断父目录是否存在
			if(!targetFile.getParentFile().exists()){
				targetFile.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(targetFile);
			//执行下载
			//发送通知提示
			sendNotification("音乐开始下载...", "音乐下载", "音乐准备下载...");
			InputStream is = HttpUtils.getInputStream(path);
			//边读边保存到sd卡
			byte[] buffer=new byte[1024*200];
			int length=0;
			//保存当前已经读取了多少
			int current=0;
			//一共字节
			int totalSize=Integer.parseInt(total);
			while((length=is.read(buffer)) != -1){
				fos.write(buffer, 0, length);
				fos.flush();
				current+=length;
				//写入文件完毕后 发送通知提示进度
				double progress = Math.floor(current*100.0/totalSize);
				sendNotification("音乐开始下载...", "音乐下载", "下载进度:"+progress+"%");
			}
			fos.close();
			clearNotification();
			sendNotification("音乐下载完成", "音乐下载完成", "音乐下载完成");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清除通知
	 */
	public void clearNotification(){
		//1.   NotificationManager
		NotificationManager manager =(NotificationManager)
						getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICATION_ID);		
	}
	
	/**
	 * 发送通知
	 * @param ticker
	 * @param title
	 * @param text
	 */
	public void sendNotification(String ticker, String title, String text){
		//1.   NotificationManager
		NotificationManager manager =(NotificationManager)
				getSystemService(NOTIFICATION_SERVICE);
		//2.  Builder
		Notification.Builder builder = new Notification.Builder(this);
		builder.setTicker(ticker)
			.setContentTitle(title)
			.setContentText(text)
			.setSmallIcon(R.drawable.ic_launcher);
		Notification n=builder.build();
		//3.  notify()
		manager.notify(NOTIFICATION_ID, n);
	}
	
}






