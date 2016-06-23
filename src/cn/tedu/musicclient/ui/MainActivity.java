package cn.tedu.musicclient.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.tedu.musicclient.app.MusicApplication;
import cn.tedu.musicclient.biz.MusicModel;
import cn.tedu.musicclient.biz.MusicModel.Callback;
import cn.tedu.musicclient.biz.MusicModel.LrcCallback;
import cn.tedu.musicclient.biz.MusicModel.SongInfoCallback;
import cn.tedu.musicclient.entity.LrcLine;
import cn.tedu.musicclient.entity.Song;
import cn.tedu.musicclient.fragment.HotMusicListFragment;
import cn.tedu.musicclient.fragment.NewMusicListFragment;
import cn.tedu.musicclient.service.DownloadService;
import cn.tedu.musicclient.service.PlayMusicService;
import cn.tedu.musicclient.service.PlayMusicService.IMusicPlayer;
import cn.tedu.musicclient.util.BitmapUtils;
import cn.tedu.musicclient.util.BitmapUtils.BitmapCallback;
import cn.tedu.musicclient.util.Consts;
import cn.tedu.musicclient.util.DateUtils;

public class MainActivity extends FragmentActivity {
	private ViewPager viewPager;
	private RadioGroup radioGroup;
	private RadioButton rbNew;
	private RadioButton rbHot;
	//�ײ����Ŀؼ�
	private ImageView ivCMPic;
	private TextView tvCMName;
	//������ǰ���Ž����еĿؼ�
	private RelativeLayout rlPlayMusic;
	private TextView tvCMTitle;
	private TextView tvCMSinger;
	private ImageView ivCMAlbum;
	private TextView tvCMLrc;
	private SeekBar seekBar;
	private TextView tvCMProgress;
	private TextView tvCMTotaltime;
	private ImageView ivCMBackground;
	//�������������еĿؼ�
	private RelativeLayout rlSearchView;
	private ListView lvSearchSongs;
	private EditText etSearch;
	private List<Song> searchSongList = new ArrayList<Song>();
	private ArrayAdapter<Song> searchSongAdapter;
	//����Fragment������Դ
	private List<Fragment> fragments;
	private MusicPagerAdapter pagerAdapter;
	private ServiceConnection conn;
	protected IMusicPlayer musicPlayer;
	private MusicInfoReceiver receiver;
	protected Bitmap defaultBackgroundPic;
	private Bitmap defaultAlbumPic;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//�ؼ���ʼ��
		setViews();
		//��дPlayMusicService  ���Ұ󶨸�service
		bindMusicService();
		//��ʼ��ViewPager
		setViewPager();
		//���ü���
		setListeners();
		//ע��㲥������ ����������صĹ㲥
		registMyReceivers();
	}

	/**
	 * ע��㲥������
	 */
	private void registMyReceivers() {
		receiver = new MusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Consts.ACTION_MUSIC_START_PLAY);
		filter.addAction(Consts.ACTION_MUSIC_UPDATE_PROGRESS);
		this.registerReceiver(receiver, filter);
	}

	/**
	 * ��дPlayMusicService  ���Ұ󶨸�service
	 */
	private void bindMusicService() {
		Intent intent = new Intent(this, PlayMusicService.class);
		conn = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
			}
			//Service�����  ����IBinder�ӿ�ʵ��
			public void onServiceConnected(ComponentName name, IBinder binder) {
				musicPlayer = (IMusicPlayer)binder;
				//��musicPlayer���󴫵ݸ�fragments
				NewMusicListFragment f1=(NewMusicListFragment)fragments.get(0);
				f1.setMusicPlayer(musicPlayer);
				HotMusicListFragment f2=(HotMusicListFragment)fragments.get(1);
				f2.setMusicPlayer(musicPlayer);
			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	//���ü���
	private void setListeners() {
		/**
		 * ��rlPlayMusic���ontouchListener
		 * return true��rlPlayMusic���Ѹ��¼�
		 * ���Ҳ�������󴫵ݡ� 
		 */
		rlPlayMusic.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		//��seekBar��Ӽ���  ��ק
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			//�����ȸ�����֮��ִ��
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					musicPlayer.seekTo(progress);
				}
			}
		});
		
		//ͨ�����tab ����viewPager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.radio0){ //ѡ�����¸��
					viewPager.setCurrentItem(0);
				}else{
					viewPager.setCurrentItem(1);
				}
			}
		});
		
		//ͨ��viewPager ����tab
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				if(position == 0){
					rbNew.setChecked(true);
				}else{
					rbHot.setChecked(true);
				}
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * ��ʼ��ViewPager
	 */
	private void setViewPager() {
		//������Դ���г�ʼ��
		fragments = new ArrayList<Fragment>();
		fragments.add(new NewMusicListFragment());
		fragments.add(new HotMusicListFragment());
		
		//����fragment��Adapter
		pagerAdapter = new MusicPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
	}

	/**
	 * �ؼ���ʼ��
	 */
	private void setViews() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		rbNew = (RadioButton) findViewById(R.id.radio0);
		rbHot = (RadioButton) findViewById(R.id.radio1);
		ivCMPic = (ImageView) findViewById(R.id.ivCMPic);
		tvCMName = (TextView) findViewById(R.id.tvCMName);
		
		rlPlayMusic = (RelativeLayout) findViewById(R.id.rlPlayMusic);
		tvCMTitle = (TextView) findViewById(R.id.tvCMTitle);
		tvCMProgress = (TextView) findViewById(R.id.tvCMProgress);
		tvCMTotaltime = (TextView) findViewById(R.id.tvCMTotaltime);
		tvCMSinger = (TextView) findViewById(R.id.tvCMSinger);
		tvCMLrc= (TextView) findViewById(R.id.tvCMLrc);
		
		rlSearchView = (RelativeLayout) findViewById(R.id.rlSearchView);
		lvSearchSongs = (ListView) findViewById(R.id.lvSearchSongs);
		etSearch = (EditText) findViewById(R.id.etSearch);
		
		//ר��ͼƬ
		ivCMAlbum = (ImageView) findViewById(R.id.ivCMAlbum);
		//���ؼ�������֮�� ��ȡ�ؼ��Ŀ�� 
		//���ÿؼ��ĸ߶�
		ViewTreeObserver vto = ivCMAlbum.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				ivCMAlbum.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				int w=ivCMAlbum.getWidth();
				ivCMAlbum.getLayoutParams().height=w;
			}
		});
		
		
		
		//Ĭ��ͼƬ
		defaultAlbumPic = BitmapFactory.decodeResource(getResources(), R.drawable.default_music_pic);
		seekBar = (SeekBar) findViewById(R.id.seekbar);
		ivCMBackground = (ImageView) findViewById(R.id.ivCMBackground);
		//ivCMBackground��Ҫ��ģ������  ռ��ʱ�� 
		//����ڹ����߳���ִ�� ��ͼƬģ���������֮��
		//�ٸ�ivCMBackground����
		Options opt = new Options();
		opt.inSampleSize=4;
		Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.default_music_background, opt);
		BitmapUtils.loadBluredBitmap(bitmap, 20, new BitmapCallback() {
			public void onBitmapLoaded(Bitmap bitmap) {
				defaultBackgroundPic = bitmap;
				//ͼƬģ��������ɺ� �ص�
				ivCMBackground.setImageBitmap(defaultBackgroundPic);
			}
		});
	}

	/**
	 * ��ӿؼ��ļ���
	 */
	public void doClick(View view){
		MusicApplication app=(MusicApplication) getApplication();
		List<Song> list = app.getSongList();
		MusicModel model = new MusicModel();
		switch (view.getId()) {
		case R.id.btnDownload: //���������
			final Song song=app.getCurrentSong();
			//����alertDialog
			final String[] versions={"���ٰ�", "��ͨ��", "�����"};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("ѡ��汾")
				.setItems(versions, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent service = new Intent(MainActivity.this, DownloadService.class);
						String path="";
						int version=0;
						String title="";
						String total="0"; //�ļ������ֽ���
						switch (which) {
						case 0:
							path=song.getUrls().get(0).getFile_link();
							version=song.getUrls().get(0).getFile_bitrate();
							total=song.getUrls().get(0).getFile_size();
							title=song.getSongInfo().getTitle();
							break;
						case 1:
							path=song.getUrls().get(1).getFile_link();
							version=song.getUrls().get(1).getFile_bitrate();
							total=song.getUrls().get(1).getFile_size();
							title=song.getSongInfo().getTitle();
							break;
						case 2:
							path=song.getUrls().get(2).getFile_link();
							version=song.getUrls().get(2).getFile_bitrate();
							total=song.getUrls().get(2).getFile_size();
							title=song.getSongInfo().getTitle();
							break;
						}
						service.putExtra("path", path);
						service.putExtra("version", version);
						service.putExtra("title", title);
						service.putExtra("total", total);
						//��֤һ�����׸��Ƿ��Ѿ����ع�
						File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "_"+version+"/"+title+".mp3");
						if(targetFile.exists()){
							Toast.makeText(MainActivity.this, "�����Ѿ��������", Toast.LENGTH_SHORT).show();
						}else{
							startService(service);
						}
					}
				});
			AlertDialog dialog = builder.create();
			dialog.show();
			
			break;
		case R.id.btnSearchSong://��������������е�������ť
			String text = etSearch.getText().toString();
			//��text���зǿ��ж�
			model.searchSongs(text, new Callback() {
				public void onMusicListLoaded(List<Song> songs) {
					//�������б��ѳ������� �����Ա����
					searchSongList.clear();
					searchSongList.addAll(songs);
					//����lvSearchSongs���б�����
					if(searchSongAdapter!=null){
						searchSongAdapter.notifyDataSetChanged();
					}else{//��ʼ��Adapter
						searchSongAdapter = new ArrayAdapter<Song>(MainActivity.this, android.R.layout.simple_list_item_1, searchSongList);
						lvSearchSongs.setAdapter(searchSongAdapter);
					}
				}
			});
			break;
		case R.id.btnSearchCancel:
			rlSearchView.setVisibility(View.INVISIBLE);
			break;
		case R.id.btnSearch: //������������е�������ť
			//��ʾrllayout
			rlSearchView.setVisibility(View.VISIBLE);
			break;
		case R.id.ivPre:
			int position=app.getPosition();
			position = position == 0 ? 0 : position-1; 
			app.setPosition(position);
			final Song s=list.get(position);
			//��ȡ����������·�� 
			model.loadSongInfoById(s.getSong_id(), new SongInfoCallback() {
				public void onSongInfoLoaded(Song song) {
					s.setUrls(song.getUrls());
					s.setSongInfo(song.getSongInfo());
					String path=s.getUrls().get(0).getShow_link();
					musicPlayer.playCurrentMusic(path);
				}
			});
			break;
		case R.id.ivPlay:
			musicPlayer.playOrPause();
			break;
		case R.id.ivNext:
			position=app.getPosition();
			position = position == list.size()-1 ? position : position+1;
			app.setPosition(position);
			final Song s2=list.get(position);
			//�ȵ���model����ķ��� ������һ�׸�Ļ�����Ϣ 
			//������Ϻ��ٲ���
			model.loadSongInfoById(s2.getSong_id(), new SongInfoCallback() {
				public void onSongInfoLoaded(Song song) {
					s2.setUrls(song.getUrls());
					s2.setSongInfo(song.getSongInfo());
					String p=s2.getUrls().get(0).getShow_link();
					musicPlayer.playCurrentMusic(p);
				}
			});
			break;
		case R.id.ivCMPic: //Բ��ͼƬ rlPlayMusic��ʾ
			rlPlayMusic.setVisibility(View.VISIBLE);
			TranslateAnimation anim=new TranslateAnimation(0, 0, rlPlayMusic.getHeight(), 0);
			anim.setDuration(400);
			rlPlayMusic.startAnimation(anim);
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(rlPlayMusic.getVisibility()==View.VISIBLE){
			//��rlPlayMusic����
			TranslateAnimation anim=new TranslateAnimation(0,0,0,rlPlayMusic.getHeight());
			anim.setDuration(400);
			rlPlayMusic.startAnimation(anim);
			rlPlayMusic.setVisibility(View.INVISIBLE);
		}else{
			//�����µ�����ջ  ��ת����������
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
	}
	
	//Ϊviewpager��д������
	class MusicPagerAdapter extends FragmentPagerAdapter{

		public MusicPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		public Fragment getItem(int position) {
			return fragments.get(position);
		}
		public int getCount() {
			return fragments.size();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//���service�İ�
		this.unbindService(conn);
		//ȡ���㲥��������ע��
		this.unregisterReceiver(receiver);
		//��NewMusicListFragment�е�adapter�е��߳�ͣ��
		NewMusicListFragment f=(NewMusicListFragment) fragments.get(0);
		f.getAdapter().stopThread();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * ����������ص���Ϣ
	 */
	class MusicInfoReceiver extends BroadcastReceiver{
		
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			MusicApplication app=(MusicApplication) getApplication();
			if(action.equals(Consts.ACTION_MUSIC_UPDATE_PROGRESS)){
				//�������ֽ���
				int current=intent.getIntExtra("current", 0);
				int total=intent.getIntExtra("total", 0);
				//����seekBar
				seekBar.setMax(total);
				seekBar.setProgress(current);
				//��������TextView
				tvCMProgress.setText(DateUtils.parseTime(current));
				tvCMTotaltime.setText(DateUtils.parseTime(total));
				//���¸��TextView
				Song song=app.getCurrentSong();
				String content=song.getCurrentLrc(current);
				if(content!=null){
					tvCMLrc.setText(content);
				}
			}else if(action.equals(Consts.ACTION_MUSIC_START_PLAY)){
				//�����Ѿ���ʼ����  ��Ҫ�ѽ����е���Ϣ  ����
				List<Song> songs = app.getSongList();
				int position=app.getPosition();
				Song song=songs.get(position);
				//����UI
				String url=song.getPic_small();
				BitmapUtils.loadBitmap(url, new BitmapUtils.BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						//ͼƬ������ɺ�
						ivCMPic.setImageBitmap(bitmap);
						//��ͼƬ��ת
						RotateAnimation anim=new RotateAnimation(0,360,ivCMPic.getWidth()/2, ivCMPic.getHeight()/2);
						anim.setDuration(10000);
						anim.setRepeatCount(Animation.INFINITE);
						//ʱ���ֵ��  �����˶�
						anim.setInterpolator(new LinearInterpolator());
						ivCMPic.startAnimation(anim);
					}
				});
				tvCMName.setText(song.getTitle());
				//����rlPlayMusic�����еĿؼ�
				tvCMTitle.setText(song.getTitle());
				tvCMSinger.setText(song.getArtist_name());
				//ר��ͼƬ
				ivCMAlbum.setImageBitmap(defaultAlbumPic);
				String albumPath = song.getSongInfo().getAlbum_500_500();
				if("".equals(albumPath)){
					albumPath=song.getPic_big();
				}
				BitmapUtils.loadBitmap(albumPath, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						ivCMAlbum.setImageBitmap(bitmap);
					}
				});
				//����ͼƬ
				ivCMBackground.setImageBitmap(defaultBackgroundPic);
				String backgroundPath=song.getSongInfo().getArtist_480_800();
				if("".equals(backgroundPath)){
					backgroundPath=song.getSongInfo().getArtist_640_1136();
				}
				if("".equals(backgroundPath)){
					backgroundPath=song.getSongInfo().getAlbum_500_500();
				}
				if("".equals(backgroundPath)){
					backgroundPath=song.getPic_big();
				}
				Log.i("info", "backgroundPath:"+backgroundPath);
				BitmapUtils.loadBitmap(backgroundPath, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						//ͼƬ������ɺ� 
						BitmapUtils.loadBluredBitmap(bitmap, 20, new BitmapCallback() {
							public void onBitmapLoaded(Bitmap b) {
								ivCMBackground.setImageBitmap(b);
							}
						});
					}
				});
				//���ظ�� ������ʾ���
				downloadLrc();
			}
		}
		//���ظ���ļ�
		private void downloadLrc() {
			MusicModel model = new MusicModel();
			MusicApplication app=(MusicApplication) getApplication();
			final Song song=app.getCurrentSong();
			String url=song.getSongInfo().getLrclink();
			model.loadLrcByUrl(url, new LrcCallback() {
				public void onLrcLoaded(List<LrcLine> lines) {
					song.setLrc(lines);
				}
			});
		}
	}
	
}


