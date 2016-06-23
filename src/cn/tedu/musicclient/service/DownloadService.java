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
	 * �÷����е����ݽ����ڹ����߳���ִ��
	 * ���ֵ�ִ�и���Ϣʱ����������onHandleIntent�����еĴ���
	 */
	protected void onHandleIntent(Intent intent) {
		//��ȡAcitvity�����Ĳ���
		String path=intent.getStringExtra("path");
		int version=intent.getIntExtra("version", 0);
		String title=intent.getStringExtra("title");
		String total=intent.getStringExtra("total");
		//ִ�����ز���  ����http����
		try {
			//��ȡ����Ŀ¼  /mnt/sdcard/Music/_128/title.mp3
			File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "_"+version+"/"+title+".mp3");
			//�жϸ�Ŀ¼�Ƿ����
			if(!targetFile.getParentFile().exists()){
				targetFile.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(targetFile);
			//ִ������
			//����֪ͨ��ʾ
			sendNotification("���ֿ�ʼ����...", "��������", "����׼������...");
			InputStream is = HttpUtils.getInputStream(path);
			//�߶��߱��浽sd��
			byte[] buffer=new byte[1024*200];
			int length=0;
			//���浱ǰ�Ѿ���ȡ�˶���
			int current=0;
			//һ���ֽ�
			int totalSize=Integer.parseInt(total);
			while((length=is.read(buffer)) != -1){
				fos.write(buffer, 0, length);
				fos.flush();
				current+=length;
				//д���ļ���Ϻ� ����֪ͨ��ʾ����
				double progress = Math.floor(current*100.0/totalSize);
				sendNotification("���ֿ�ʼ����...", "��������", "���ؽ���:"+progress+"%");
			}
			fos.close();
			clearNotification();
			sendNotification("�����������", "�����������", "�����������");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���֪ͨ
	 */
	public void clearNotification(){
		//1.   NotificationManager
		NotificationManager manager =(NotificationManager)
						getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICATION_ID);		
	}
	
	/**
	 * ����֪ͨ
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






