package cn.tedu.musicclient.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tedu.musicclient.ui.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * �첽����ͼƬ�Ĺ�����
 */
public class ImageLoader {
	private Context context;
	// ����ͼƬ�����hashmap
	Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	// �������񼯺�
	private List<ImageLoadTask> tasks = new ArrayList<ImageLoadTask>();
	// ������ѭ���񼯺ϵĹ����߳�
	private Thread workThread;
	private boolean isLoop = true;
	private AbsListView listView;
	
	//����handler
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_LOAD_IMAGE_SUCCESS:
				//ͼƬ�������
				ImageLoadTask task=(ImageLoadTask) msg.obj;
				Bitmap bitmap =task.bitmap;
				ImageView imageView = (ImageView) listView.findViewWithTag(task.path);
				if(imageView!=null){
					if(bitmap!=null){
						imageView.setImageBitmap(bitmap);
					}else{
						imageView.setImageResource(R.drawable.ic_launcher);
					}
				}
				break;
			}
		}
	};
	
	public static final int HANDLER_LOAD_IMAGE_SUCCESS=1;

	public ImageLoader(Context context, AbsListView listView) {
		this.listView = listView;
		this.context = context;
		// ��ʼ��workThread
		workThread = new Thread() {
			public void run() {
				// ������ѭ���񼯺� ��������� ��ִ�� ���û���� ��ȴ�
				while (isLoop) {
					if (!tasks.isEmpty()) { // ������
						ImageLoadTask task = tasks.remove(0);
						// ����ͼƬ
						Bitmap bitmap = loadBitmap(task.path);
						// ͼƬ�Ѿ����سɹ�
						// Log.i("info", "ͼƬ���سɹ�:"+task.path);
						// ��ʾ��ImageView�� ��Ҫ����Ϣ��handler
						task.bitmap = bitmap;
						Message msg = new Message();
						msg.what = HANDLER_LOAD_IMAGE_SUCCESS;
						msg.obj = task;
						handler.sendMessage(msg);
					} else {// û����
						try {
							synchronized (workThread) {
								workThread.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		// ���������߳�
		workThread.start();
	}

	/**
	 * ͨ������·��  ����ͼƬ
	 * @param path
	 * @return
	 */
	public Bitmap loadBitmap(String path){
		try {
			InputStream is = HttpUtils.getInputStream(path);
			//�������� ������bitmap
			Bitmap bitmap=BitmapUtils.loadBitmap(is, 50, 50);
			//�����ڴ滺�������� HashMap<String, Bitmap> 
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			//���뻺��Ŀ¼��   
			String name=path.substring(path.lastIndexOf("/")+1);
			//name:  42342342423.jpg
			File targetFile = new File( context.getCacheDir(), name);
			BitmapUtils.save(bitmap, targetFile);
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * ��ĳ��imageView����ʾͼƬ (�첽)
	 * 
	 * @param imageView
	 * @param path
	 */
	public void displayImage(ImageView imageView, String path) {
		// ����ImageView
		imageView.setTag(path);
		// ��ȥ������Ѱ�� �Ƿ��Ѿ����ع�
		SoftReference<Bitmap> ref = cache.get(path);
		if (ref != null) { // ��ǰ���
			Bitmap bitmap = ref.get();
			if (bitmap != null) {// ��ǰ���ͼƬ����
				//Log.i("info", "���ڴ����ҵ���ͼƬ...");
				imageView.setImageBitmap(bitmap);
				return;
			}
		}

		// �ڴ滺����û�� ��ȥ�ļ������л�ȡ
		String name = path.substring(path.lastIndexOf("/") + 1);
		File file = new File(context.getCacheDir(), name);
		Bitmap bitmap = BitmapUtils.loadBitmap(file.getAbsolutePath());
		if (bitmap != null) { // �ļ����и��ļ�
			// �ȴ����ڴ�
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			//Log.i("info", "���ļ����ҵ���ͼƬ...");
			imageView.setImageBitmap(bitmap);
			return;
		}

		// ��������̫����߳�
		// ����ͼƬ�Ĺ��������ڹ����߳���ִ��
		// �����񼯺�������������
		ImageLoadTask task = new ImageLoadTask();
		task.path = path;
		tasks.add(task);
		// �ѹ����̻߳��� �����ɻ�
		synchronized (workThread) {
			workThread.notify();
		}
	}

	/**
	 * ͼƬ�����������
	 */
	class ImageLoadTask {
		String path;
		Bitmap bitmap;
	}

	/**
	 * ֹͣ�����߳�
	 */
	public void stopThread(){
		isLoop = false;
		synchronized (workThread) {
			workThread.notify();
		}
	}
	
}
