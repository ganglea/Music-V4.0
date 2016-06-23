package cn.tedu.musicclient.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.tedu.musicclient.adapter.MusicAdapter;
import cn.tedu.musicclient.app.MusicApplication;
import cn.tedu.musicclient.biz.MusicModel;
import cn.tedu.musicclient.biz.MusicModel.Callback;
import cn.tedu.musicclient.entity.Song;
import cn.tedu.musicclient.service.PlayMusicService.IMusicPlayer;
import cn.tedu.musicclient.ui.R;

/**
 * 呈现新歌榜的Fragment
 */
@SuppressLint("ValidFragment")
public class NewMusicListFragment extends Fragment{
	private ListView listView;
	private MusicAdapter adapter;
	private List<Song> songs;
	private MusicModel model;
	private IMusicPlayer player;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_list, null);
		//控件初始化
		setViews(view);
		setListeners();
		//TODO 调用model层的相应方法 执行业务
		model = new MusicModel();
		model.loadNewMusicList(0, 20, new Callback() {
			//当loadNewMusicList方法中把都加载完毕之后
			//将会在主线程中执行该方法
			public void onMusicListLoaded(List<Song> songs) {
				NewMusicListFragment.this.songs = songs;
				setAdapter();
			}
		} );
		return view;
	}

	/**
	 * 添加监听
	 */
	private void setListeners() {
		/**
		 * 给listView添加滚动事件监听
		 */
		listView.setOnScrollListener(new OnScrollListener() {
			boolean atBottom=false;
			/**
			 * 滚动状态改变时执行
			 */
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					Log.i("info", "空闲状态..");
					//到底了 加载后续数据
					if(atBottom){
						model.loadNewMusicList(songs.size(), 20, new Callback(){
							public void onMusicListLoaded(List<Song> songs) {
								if(songs!=null){
									NewMusicListFragment.this.songs.addAll(songs);
									adapter.notifyDataSetChanged();
								}else{
									Toast.makeText(getActivity(), "没有了!", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					Log.i("info", "摁住滚的状态..");
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					Log.i("info", "飞的状态..");
					break;
				}
			}
			public void onScroll(AbsListView view, 
					int firstVisibleItem, //屏幕中第一个可见item的下标
					int visibleItemCount, //可见item的数量
					int totalItemCount //item的总数量
					) {
				if(totalItemCount!=0 && firstVisibleItem+visibleItemCount==totalItemCount){
					Log.i("info", "到底了...."+totalItemCount);
					atBottom=true;
				}else{
					atBottom=false;
				}
			}
		});
		
		/**
		 * 单击Item
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//为了以后使用songs集合和position
				//我们把这些对象存入Application
				MusicApplication app=(MusicApplication) getActivity().getApplication();
				app.setSongList(songs);
				app.setPosition(position);
				
				
				final Song s = songs.get(position);
				String songId=s.getSong_id();
				//通过songId 获取相应音乐的基本信息
				model.loadSongInfoById(songId, new MusicModel.SongInfoCallback() {
					public void onSongInfoLoaded(Song song) {
						//把获取的song对象中的内容封装
						s.setSongInfo(song.getSongInfo());
						s.setUrls(song.getUrls());
						String path=s.getUrls().get(0).getShow_link();
						//准备播放 把列表与position都存入application
						player.playCurrentMusic(path);
					}
				});
			}
		});
	}

	/**
	 * 更新ListView的Adapter
	 */
	public void setAdapter(){
		//自定义Adapter
		adapter = new MusicAdapter(getActivity(), songs, listView);
		listView.setAdapter(adapter);
	}
	
	/**
	 * 控件初始化
	 */
	private void setViews(View view) {
		listView = (ListView) view.findViewById(R.id.lvMusics);
	}

	public MusicAdapter getAdapter() {
		return adapter;
	}

	public void setMusicPlayer(IMusicPlayer player){
		this.player = player;
	}
	
}


