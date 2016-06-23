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
 * ������ص�ҵ���
 */
public class MusicModel {
	private Callback callback;
	
	/**
	 * ͨ��url  ��ȡ ������б�
	 * @param url
	 * @param callback
	 */
	public void loadLrcByUrl(final String url, final LrcCallback callback){
		//���������û�� ����������
		new AsyncTask<String, String, List<LrcLine>>(){
			private File file;
			protected List<LrcLine> doInBackground(String... params) {
				//url:  http://xxxx/xxx/xxx/xxx.lrc
				//Ŀ��·����  /data/data/xxxxxxx/cache/xxx.lrc
				MusicApplication app = MusicApplication.getApp();
				String filename=url.substring(url.lastIndexOf("/")+1);
				file = new File(app.getCacheDir(), filename);
				//�ȴӻ����л�ȡ
				try {
					InputStream is = LrcUtils.loadLrcByFilePath(file.getAbsolutePath());
					if(is!=null){
						//���ļ��л�ȡ��������
						List<LrcLine> list = loadLrcLines(is);
						Log.i("info", "��ȡ�ļ���ȡ���");
						return list;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//�첽�������� 
				try {
					InputStream is = HttpUtils.getInputStream(url);
					List<LrcLine> lines = loadLrcLines(is);
					Log.i("info", "���������ȡ���");
					return lines;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			/**
			 * ͨ�����������س�����ʼ���
			 * @param is
			 */
			private List<LrcLine> loadLrcLines(InputStream is)throws IOException{
				BufferedReader reader=new BufferedReader(new InputStreamReader(is));
				String line="";
				List<LrcLine> lines = new ArrayList<LrcLine>();
				while((line=reader.readLine())!=null){
					//line:   [00:00.91]��죺������ȥ
					LrcLine l = new LrcLine();
					l.parse(line);
					l.setData(line);
					lines.add(l);
				}
				//��ʼ������  �����еĸ�ʶ�д�뻺���ļ�
				LrcUtils.save(lines, file);
				return lines;
			}
			
			
			protected void onPostExecute(java.util.List<LrcLine> result) {
				callback.onLrcLoaded(result);
			}
		}.execute();
	}
	

	/**
	 * �첽�������� ͨ��songid��ȡ����������Ϣ
	 * @param id
	 */
	public void loadSongInfoById(final String songId, final SongInfoCallback callback){
		AsyncTask<String, String, Song> task=new AsyncTask<String, String, Song>(){
			protected Song doInBackground(String... params) {
				//�������� ��json��װ��song������
				String uri=UrlFactory.getSongInfoUrl(songId);
				try {
					String jsonString=HttpUtils.getString(uri);
					//����json�ַ���
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
			//���߳���ִ��
			protected void onPostExecute(Song song) {
				callback.onSongInfoLoaded(song);
			}
		};
		task.execute();
	}
	
	/**
	 * �����¸��    ��Ҫ�첽��������
	 * �ڹ����߳�����������Ϻ�  ����XML
	 * ��ȡList<Song>
	 * �����߳�ִ�к���ҵ��
	 */
	public void loadNewMusicList(int offset, int size, Callback callback){
		this.callback = callback;
		//ͨ��url����  ��ȡ�����ַ
		String uri=UrlFactory.getNewMusicListUrl(offset, size);
		SearchSongsTask task = new SearchSongsTask();
		task.execute(uri);
	}

	/**
	 * �����ȸ��    ��Ҫ�첽��������
	 * �ڹ����߳�����������Ϻ�  ����XML
	 * ��ȡList<Song>
	 * �����߳�ִ�к���ҵ��
	 */
	public void loadHotMusicList(int offset, int size, Callback callback){
		this.callback = callback;
		//ͨ��url����  ��ȡ�����ַ
		String uri=UrlFactory.getHotMusicListUrl(offset, size);
		SearchSongsTask task = new SearchSongsTask();
		task.execute(uri);
	}
	
	
	/**
	 * �첽���������б���첽����
	 * @param:  ���������ַ 
	 */
	class SearchSongsTask extends AsyncTask<String, String, List<Song>>{
		/**
		 * �ڹ����߳�ִ��  
		 * �����˷������� ��ȡxml�ĵ� ���� 
		 * ����List<Song>
		 */
		protected List<Song> doInBackground(String... params) {
			String uri=params[0];
			//����http����  ��ȡXML�ĵ�
			try {
				InputStream is = HttpUtils.getInputStream(uri);
				//String xml=HttpUtils.getString(uri);
				//���Դ�is�ж�ȡXML�ĵ�
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
		 * �����߳���ִ��
		 * ��doInBackground����ִ����Ϻ� 
		 */
		protected void onPostExecute(List<Song> songs) {
			//����UI (��Ӧ����Model����ֱ�Ӹ���)
			//ʹ�ûص�����  ����callback�ӿڵķ���
			//��Fragment����UI
			callback.onMusicListLoaded(songs);
		}
		
	}
	

	/**
	 * ��������
	 * @param text �ؼ���
	 * @param callback  �ص�����
	 */
	public void searchSongs(final String text, final Callback callback) {
		new AsyncTask<String, String, List<Song>>(){
			protected List<Song> doInBackground(String... params) {
				//�ڹ����߳��з���http����
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
		 * �������б������Ϻ�  ��Ҫִ��
		 */
		void onMusicListLoaded(List<Song> songs);
	}
	
	public interface LrcCallback{
		void onLrcLoaded(List<LrcLine> lines);
	}
	
	public interface SongInfoCallback{
		/**
		 * ��ͨ��songid���س�songInfo�� ִ��
		 * @param song
		 */
		void onSongInfoLoaded(Song song);
	}

}





