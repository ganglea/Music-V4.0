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
 * �����¸���Fragment
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
		//�ؼ���ʼ��
		setViews(view);
		setListeners();
		//TODO ����model�����Ӧ���� ִ��ҵ��
		model = new MusicModel();
		model.loadNewMusicList(0, 20, new Callback() {
			//��loadNewMusicList�����аѶ��������֮��
			//���������߳���ִ�и÷���
			public void onMusicListLoaded(List<Song> songs) {
				NewMusicListFragment.this.songs = songs;
				setAdapter();
			}
		} );
		return view;
	}

	/**
	 * ��Ӽ���
	 */
	private void setListeners() {
		/**
		 * ��listView��ӹ����¼�����
		 */
		listView.setOnScrollListener(new OnScrollListener() {
			boolean atBottom=false;
			/**
			 * ����״̬�ı�ʱִ��
			 */
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					Log.i("info", "����״̬..");
					//������ ���غ�������
					if(atBottom){
						model.loadNewMusicList(songs.size(), 20, new Callback(){
							public void onMusicListLoaded(List<Song> songs) {
								if(songs!=null){
									NewMusicListFragment.this.songs.addAll(songs);
									adapter.notifyDataSetChanged();
								}else{
									Toast.makeText(getActivity(), "û����!", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					Log.i("info", "��ס����״̬..");
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					Log.i("info", "�ɵ�״̬..");
					break;
				}
			}
			public void onScroll(AbsListView view, 
					int firstVisibleItem, //��Ļ�е�һ���ɼ�item���±�
					int visibleItemCount, //�ɼ�item������
					int totalItemCount //item��������
					) {
				if(totalItemCount!=0 && firstVisibleItem+visibleItemCount==totalItemCount){
					Log.i("info", "������...."+totalItemCount);
					atBottom=true;
				}else{
					atBottom=false;
				}
			}
		});
		
		/**
		 * ����Item
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Ϊ���Ժ�ʹ��songs���Ϻ�position
				//���ǰ���Щ�������Application
				MusicApplication app=(MusicApplication) getActivity().getApplication();
				app.setSongList(songs);
				app.setPosition(position);
				
				
				final Song s = songs.get(position);
				String songId=s.getSong_id();
				//ͨ��songId ��ȡ��Ӧ���ֵĻ�����Ϣ
				model.loadSongInfoById(songId, new MusicModel.SongInfoCallback() {
					public void onSongInfoLoaded(Song song) {
						//�ѻ�ȡ��song�����е����ݷ�װ
						s.setSongInfo(song.getSongInfo());
						s.setUrls(song.getUrls());
						String path=s.getUrls().get(0).getShow_link();
						//׼������ ���б���position������application
						player.playCurrentMusic(path);
					}
				});
			}
		});
	}

	/**
	 * ����ListView��Adapter
	 */
	public void setAdapter(){
		//�Զ���Adapter
		adapter = new MusicAdapter(getActivity(), songs, listView);
		listView.setAdapter(adapter);
	}
	
	/**
	 * �ؼ���ʼ��
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


