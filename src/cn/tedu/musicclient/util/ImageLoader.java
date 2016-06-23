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
 * 异步加载图片的工具类
 */
public class ImageLoader {
	private Context context;
	// 声明图片缓存的hashmap
	Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	// 声明任务集合
	private List<ImageLoadTask> tasks = new ArrayList<ImageLoadTask>();
	// 声明轮循任务集合的工作线程
	private Thread workThread;
	private boolean isLoop = true;
	private AbsListView listView;
	
	//声明handler
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_LOAD_IMAGE_SUCCESS:
				//图片加载完成
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
		// 初始化workThread
		workThread = new Thread() {
			public void run() {
				// 不断轮循任务集合 如果有任务 则执行 如果没任务 则等待
				while (isLoop) {
					if (!tasks.isEmpty()) { // 有任务
						ImageLoadTask task = tasks.remove(0);
						// 下载图片
						Bitmap bitmap = loadBitmap(task.path);
						// 图片已经下载成功
						// Log.i("info", "图片下载成功:"+task.path);
						// 显示在ImageView中 需要发消息给handler
						task.bitmap = bitmap;
						Message msg = new Message();
						msg.what = HANDLER_LOAD_IMAGE_SUCCESS;
						msg.obj = task;
						handler.sendMessage(msg);
					} else {// 没任务
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
		// 启动工作线程
		workThread.start();
	}

	/**
	 * 通过完整路径  下载图片
	 * @param path
	 * @return
	 */
	public Bitmap loadBitmap(String path){
		try {
			InputStream is = HttpUtils.getInputStream(path);
			//吧输入流 解析成bitmap
			Bitmap bitmap=BitmapUtils.loadBitmap(is, 50, 50);
			//存入内存缓存区域中 HashMap<String, Bitmap> 
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			//存入缓存目录中   
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
	 * 在某个imageView中显示图片 (异步)
	 * 
	 * @param imageView
	 * @param path
	 */
	public void displayImage(ImageView imageView, String path) {
		// 设置ImageView
		imageView.setTag(path);
		// 先去缓存中寻找 是否已经下载过
		SoftReference<Bitmap> ref = cache.get(path);
		if (ref != null) { // 以前存过
			Bitmap bitmap = ref.get();
			if (bitmap != null) {// 以前存的图片还在
				//Log.i("info", "从内存中找到了图片...");
				imageView.setImageBitmap(bitmap);
				return;
			}
		}

		// 内存缓存中没有 则去文件缓存中获取
		String name = path.substring(path.lastIndexOf("/") + 1);
		File file = new File(context.getCacheDir(), name);
		Bitmap bitmap = BitmapUtils.loadBitmap(file.getAbsolutePath());
		if (bitmap != null) { // 文件中有该文件
			// 先存入内存
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			//Log.i("info", "从文件中找到了图片...");
			imageView.setImageBitmap(bitmap);
			return;
		}

		// 不能启动太多的线程
		// 下载图片的工作必须在工作线程中执行
		// 向任务集合中添加任务对象
		ImageLoadTask task = new ImageLoadTask();
		task.path = path;
		tasks.add(task);
		// 把工作线程唤醒 继续干活
		synchronized (workThread) {
			workThread.notify();
		}
	}

	/**
	 * 图片加载任务对象
	 */
	class ImageLoadTask {
		String path;
		Bitmap bitmap;
	}

	/**
	 * 停止工作线程
	 */
	public void stopThread(){
		isLoop = false;
		synchronized (workThread) {
			workThread.notify();
		}
	}
	
}
