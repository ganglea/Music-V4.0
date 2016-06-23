package cn.tedu.musicclient.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import cn.tedu.musicclient.app.MusicApplication;
import cn.tedu.musicclient.ui.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;

/**
 * 图片相关操作的工具类
 */
public class BitmapUtils {

	/**
	 * 异步在工作线程中执行图片模糊化处理
	 * @param bitmap
	 * @param r
	 * @param callback
	 */
	public static void loadBluredBitmap(final Bitmap bitmap, final int r, final BitmapCallback callback){
		new AsyncTask<String, String, Bitmap>(){
			protected Bitmap doInBackground(String... params) {
				Bitmap b=createBlurBitmap(bitmap, r);
				return b;
			}
			protected void onPostExecute(Bitmap b) {
				callback.onBitmapLoaded(b);
			}
		}.execute();
	}
	
	/**
	 * 传递bitmap  传递模糊半径 返回一个被模糊的bitmap
	 * @param sentBitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap createBlurBitmap(Bitmap sentBitmap, int radius) {
		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		if (radius < 1) {
			return (null);
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);

		}
		yw = yi = 0;
		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;
		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

				}

			}
			stackpointer = radius;
			for (x = 0; x < w; x++) {
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);

				}
				p = pix[yw + vmin[x]];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi++;

			}
			yw += w;

		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;
				sir = stack[i + radius];
				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];
				rbs = r1 - Math.abs(i);
				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

				}
				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;

				}
				p = x + vmin[y];
				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi += w;

			}

		}
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	/**
	 * 异步发送请求 获取图片
	 * 
	 * @param url
	 *            http://xxxxxxx/xxxx/xxx.jpg
	 * @param callback
	 */
	public static void loadBitmap(final String url,
			final BitmapCallback callback) {
		Context context = MusicApplication.getApp();
		// 先判断文件缓存中是否有该图片
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		if("".equals(fileName)){ //没有路径
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_music_pic);
			callback.onBitmapLoaded(bitmap);
			return;
		}
		final File file = new File(context.getCacheDir(), fileName);
		Bitmap bitmap = loadBitmap(file.getAbsolutePath());
		if (bitmap != null) { // 文件缓存中已经找到
			callback.onBitmapLoaded(bitmap);
			return;
		}
		// 如果没有 再去服务端下载
		new AsyncTask<String, String, Bitmap>() {
			protected Bitmap doInBackground(String... params) {
				// 发送http请求
				try {
					InputStream is = HttpUtils.getInputStream(url);
					Bitmap b=BitmapFactory.decodeStream(is);
					save(b, file);
					return b;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			// 主线程中调用回调方法 返回bitmap
			protected void onPostExecute(Bitmap bitmap) {
				callback.onBitmapLoaded(bitmap);
			};
		}.execute();
	}

	/**
	 * 通过路径 加载出来一张图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap loadBitmap(String path) {
		File file = new File(path);
		if (!file.exists()) { // 不存在 返回null
			return null;
		}
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 把图片存入文件
	 * 
	 * @param bitmap
	 * @param file
	 * @throws IOException
	 */
	public static void save(Bitmap bitmap, File targetFile) throws IOException {
		// 父目录不存在 则创建父目录
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(targetFile);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
	}

	/**
	 * 通过输入流 解析图片
	 * 
	 * @param is
	 *            输入流
	 * @param width
	 *            目标图片的宽度
	 * @param height
	 *            目标图片的高度
	 * @return bitmap
	 * @throws IOException
	 */
	@SuppressLint("NewApi")
	public static Bitmap loadBitmap(InputStream is, int width, int height)
			throws IOException {
		// byte[] bitmapByteArray = new byte[0];
		// byte[] buffer = new byte[512];
		// int length=0;
		// while((length=is.read(buffer))!=-1){
		// bitmapByteArray=Arrays.copyOf(bitmapByteArray,
		// bitmapByteArray.length+length);
		// System.arraycopy(buffer, 0, bitmapByteArray,
		// bitmapByteArray.length-length, length);
		// }
		byte[] buffer = new byte[512];
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((length = is.read(buffer)) != -1) {
			bos.write(buffer, 0, length);
		}
		byte[] bitmapByteArray = bos.toByteArray();
		// Options封装了BitmapFactory解析图片时的配置参数
		Options opts = new Options();
		// 仅仅加载图片的边界属性
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bitmapByteArray, 0,
				bitmapByteArray.length, opts);
		// 获取原始宽度和高度 求出压缩比例
		int w = opts.outWidth / width;
		int h = opts.outHeight / height;
		int scale = w > h ? w : h;
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(bitmapByteArray, 0,
				bitmapByteArray.length, opts);
	}

	public interface BitmapCallback {
		void onBitmapLoaded(Bitmap bitmap);
	}

}
