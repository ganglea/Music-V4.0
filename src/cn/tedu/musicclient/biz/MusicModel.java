package cn.tedu.musicclient.biz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import cn.tedu.musicclient.app.MusicApplication;
import cn.tedu.musicclient.entity.LrcLine;
import cn.tedu.musicclient.entity.Song;
import cn.tedu.musicclient.util.HttpUtils;
import cn.tedu.musicclient.util.JSONParser;
import cn.tedu.musicclient.util.LrcUtils;
import cn.tedu.musicclient.util.MusicXmlParser;
import cn.tedu.musicclient.util.UrlFactory;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 音乐相关的业务层
 */
public class MusicModel {
	private Callback callback;
	
	/**
	 * 通过url  获取 歌词行列表
	 * @param url
	 * @param callback
	 */
	public void loadLrcByUrl(final String url, final LrcCallback callback){
		//如果缓存中没有 则发请求下载
		new AsyncTask<String, String, List<LrcLine>>(){
			private File file;
			protected List<LrcLine> doInBackground(String... params) {
				//url:  http://xxxx/xxx/xxx/xxx.lrc
				//目标路径：  /data/data/xxxxxxx/cache/xxx.lrc
				MusicApplication app = MusicApplication.getApp();
				String filename=url.substring(url.lastIndexOf("/")+1);
				file = new File(app.getCacheDir(), filename);
				//先从缓存中获取
				try {
					InputStream is = LrcUtils.loadLrcByFilePath(file.getAbsolutePath());
					if(is!=null){
						//从文件中获取到了数据
						List<LrcLine> list = loadLrcLines(is);
						Log.i("info", "读取文件获取歌词");
						return list;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//异步发送请求 
				try {
					InputStream is = HttpUtils.getInputStream(url);
					List<LrcLine> lines = loadLrcLines(is);
					Log.i("info", "发送请求获取歌词");
					return lines;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			/**
			 * 通过输入流加载出来歌词集合
			 * @param is
			 */
			private List<LrcLine> loadLrcLines(InputStream is)throws IOException{
				BufferedReader reader=new BufferedReader(new InputStreamReader(is));
				String line="";
				List<LrcLine> lines = new ArrayList<LrcLine>();
				while((line=reader.readLine())!=null){
					//line:   [00:00.91]阮经天：许多年过去
					LrcLine l = new LrcLine();
					l.parse(line);
					l.setData(line);
					lines.add(l);
				}
				//歌词加载完成  把所有的歌词都写入缓存文件
				LrcUtils.save(lines, file);
				return lines;
			}
			
			
			protected void onPostExecute(java.util.List<LrcLine> result) {
				callback.onLrcLoaded(result);
			}
		}.execute();
	}
	

	/**
	 * 异步发送请求 通过songid获取歌曲基本信息
	 * @param id
	 */
	public void loadSongInfoById(final String songId, final SongInfoCallback callback){
		AsyncTask<String, String, Song> task=new AsyncTask<String, String, Song>(){
			protected Song doInBackground(String... params) {
				//发送请求 把json封装到song对象中
				String uri=UrlFactory.getSongInfoUrl(songId);
				try {
					String jsonString=HttpUtils.getString(uri);
					//解析json字符串
					Song song = JSONParser.parseMusicInfo(jsonString);
					return song;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			//主线程中执行
			protected void onPostExecute(Song song) {
				callback.onSongInfoLoaded(song);
			}
		};
		task.execute();
	}
	
	/**
	 * 加载新歌榜单    需要异步发送请求
	 * 在工作线程中请求发送完毕后  解析XML
	 * 获取List<Song>
	 * 在主线程执行后续业务
	 */
	public void loadNewMusicList(int offset, int size, Callback callback){
		this.callback = callback;
		//通过url工厂  获取请求地址
		String uri=UrlFactory.getNewMusicListUrl(offset, size);
		SearchSongsTask task = new SearchSongsTask();
		task.execute(uri);
	}

	/**
	 * 加载热歌榜单    需要异步发送请求
	 * 在工作线程中请求发送完毕后  解析XML
	 * 获取List<Song>
	 * 在主线程执行后续业务
	 */
	public void loadHotMusicList(int offset, int size, Callback callback){
		this.callback = callback;
		//通过url工厂  获取请求地址
		String uri=UrlFactory.getHotMusicListUrl(offset, size);
		SearchSongsTask task = new SearchSongsTask();
		task.execute(uri);
	}
	
	
	/**
	 * 异步访问音乐列表的异步任务
	 * @param:  传递请求地址 
	 */
	class SearchSongsTask extends AsyncTask<String, String, List<Song>>{
		/**
		 * 在工作线程执行  
		 * 向服务端发送请求 获取xml文档 解析 
		 * 返回List<Song>
		 */
		protected List<Song> doInBackground(String... params) {
			String uri=params[0];
			//发送http请求  获取XML文档
			try {
				InputStream is = HttpUtils.getInputStream(uri);
				//String xml=HttpUtils.getString(uri);
				//可以从is中读取XML文档
				List<Song> songs = MusicXmlParser.parseMusicList(is);
				return songs;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * 在主线程中执行
		 * 当doInBackground方法执行完毕后 
		 */
		protected void onPostExecute(List<Song> songs) {
			//更新UI (不应该在Model层中直接更新)
			//使用回调机制  调用callback接口的方法
			//让Fragment更新UI
			callback.onMusicListLoaded(songs);
		}
		
	}
	

	/**
	 * 搜索歌曲
	 * @param text 关键字
	 * @param callback  回调方法
	 */
	public void searchSongs(final String text, final Callback callback) {
		new AsyncTask<String, String, List<Song>>(){
			protected List<Song> doInBackground(String... params) {
				//在工作线程中发送http请求
				String uri=UrlFactory.getSearchSongUrl(text);
				try {
					String jsonString=HttpUtils.getString(uri);
					//json:
					List<Song> songs = JSONParser.parseSearchSongs(jsonString);
					return songs;
				} catch (IOException e) {
					e.printStackTrace();
				} catch(JSONException e){
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(java.util.List<Song> result) {
				callback.onMusicListLoaded(result);
			}
		}.execute();
		
	}
	
	public interface Callback {
		/**
		 * 当音乐列表加载完毕后  需要执行
		 */
		void onMusicListLoaded(List<Song> songs);
	}
	
	public interface LrcCallback{
		void onLrcLoaded(List<LrcLine> lines);
	}
	
	public interface SongInfoCallback{
		/**
		 * 当通过songid加载出songInfo后 执行
		 * @param song
		 */
		void onSongInfoLoaded(Song song);
	}

}





