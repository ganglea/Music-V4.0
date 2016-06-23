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
	//底部栏的控件
	private ImageView ivCMPic;
	private TextView tvCMName;
	//声明当前播放界面中的控件
	private RelativeLayout rlPlayMusic;
	private TextView tvCMTitle;
	private TextView tvCMSinger;
	private ImageView ivCMAlbum;
	private TextView tvCMLrc;
	private SeekBar seekBar;
	private TextView tvCMProgress;
	private TextView tvCMTotaltime;
	private ImageView ivCMBackground;
	//声明搜索界面中的控件
	private RelativeLayout rlSearchView;
	private ListView lvSearchSongs;
	private EditText etSearch;
	private List<Song> searchSongList = new ArrayList<Song>();
	private ArrayAdapter<Song> searchSongAdapter;
	//声明Fragment的数据源
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
		//控件初始化
		setViews();
		//编写PlayMusicService  并且绑定该service
		bindMusicService();
		//初始化ViewPager
		setViewPager();
		//设置监听
		setListeners();
		//注册广播接收器 接收音乐相关的广播
		registMyReceivers();
	}

	/**
	 * 注册广播接收器
	 */
	private void registMyReceivers() {
		receiver = new MusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Consts.ACTION_MUSIC_START_PLAY);
		filter.addAction(Consts.ACTION_MUSIC_UPDATE_PROGRESS);
		this.registerReceiver(receiver, filter);
	}

	/**
	 * 编写PlayMusicService  并且绑定该service
	 */
	private void bindMusicService() {
		Intent intent = new Intent(this, PlayMusicService.class);
		conn = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
			}
			//Service绑定完成  返回IBinder接口实例
			public void onServiceConnected(ComponentName name, IBinder binder) {
				musicPlayer = (IMusicPlayer)binder;
				//把musicPlayer对象传递给fragments
				NewMusicListFragment f1=(NewMusicListFragment)fragments.get(0);
				f1.setMusicPlayer(musicPlayer);
				HotMusicListFragment f2=(HotMusicListFragment)fragments.get(1);
				f2.setMusicPlayer(musicPlayer);
			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	//设置监听
	private void setListeners() {
		/**
		 * 给rlPlayMusic添加ontouchListener
		 * return true让rlPlayMusic消费该事件
		 * 并且不再让向后传递。 
		 */
		rlPlayMusic.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		//给seekBar添加监听  拖拽
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			//当进度更新了之后执行
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					musicPlayer.seekTo(progress);
				}
			}
		});
		
		//通过点击tab 控制viewPager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.radio0){ //选择了新歌榜
					viewPager.setCurrentItem(0);
				}else{
					viewPager.setCurrentItem(1);
				}
			}
		});
		
		//通过viewPager 控制tab
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
	 * 初始化ViewPager
	 */
	private void setViewPager() {
		//对数据源进行初始化
		fragments = new ArrayList<Fragment>();
		fragments.add(new NewMusicListFragment());
		fragments.add(new HotMusicListFragment());
		
		//创建fragment的Adapter
		pagerAdapter = new MusicPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
	}

	/**
	 * 控件初始化
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
		
		//专辑图片
		ivCMAlbum = (ImageView) findViewById(R.id.ivCMAlbum);
		//当控件被度量之后 获取控件的宽度 
		//设置控件的高度
		ViewTreeObserver vto = ivCMAlbum.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				ivCMAlbum.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				int w=ivCMAlbum.getWidth();
				ivCMAlbum.getLayoutParams().height=w;
			}
		});
		
		
		
		//默认图片
		defaultAlbumPic = BitmapFactory.decodeResource(getResources(), R.drawable.default_music_pic);
		seekBar = (SeekBar) findViewById(R.id.seekbar);
		ivCMBackground = (ImageView) findViewById(R.id.ivCMBackground);
		//ivCMBackground需要被模糊处理  占用时间 
		//最好在工作线程中执行 当图片模糊处理完成之后
		//再给ivCMBackground设置
		Options opt = new Options();
		opt.inSampleSize=4;
		Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.default_music_background, opt);
		BitmapUtils.loadBluredBitmap(bitmap, 20, new BitmapCallback() {
			public void onBitmapLoaded(Bitmap bitmap) {
				defaultBackgroundPic = bitmap;
				//图片模糊处理完成后 回调
				ivCMBackground.setImageBitmap(defaultBackgroundPic);
			}
		});
	}

	/**
	 * 添加控件的监听
	 */
	public void doClick(View view){
		MusicApplication app=(MusicApplication) getApplication();
		List<Song> list = app.getSongList();
		MusicModel model = new MusicModel();
		switch (view.getId()) {
		case R.id.btnDownload: //点击了下载
			final Song song=app.getCurrentSong();
			//弹出alertDialog
			final String[] versions={"极速版", "普通版", "无损版"};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("选择版本")
				.setItems(versions, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent service = new Intent(MainActivity.this, DownloadService.class);
						String path="";
						int version=0;
						String title="";
						String total="0"; //文件的总字节数
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
						//验证一下这首歌是否已经下载过
						File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "_"+version+"/"+title+".mp3");
						if(targetFile.exists()){
							Toast.makeText(MainActivity.this, "音乐已经下载完成", Toast.LENGTH_SHORT).show();
						}else{
							startService(service);
						}
					}
				});
			AlertDialog dialog = builder.create();
			dialog.show();
			
			break;
		case R.id.btnSearchSong://点击了搜索界面中的搜索按钮
			String text = etSearch.getText().toString();
			//对text进行非空判断
			model.searchSongs(text, new Callback() {
				public void onMusicListLoaded(List<Song> songs) {
					//把搜索列表搜出的数据 存入成员变量
					searchSongList.clear();
					searchSongList.addAll(songs);
					//更新lvSearchSongs的列表数据
					if(searchSongAdapter!=null){
						searchSongAdapter.notifyDataSetChanged();
					}else{//初始化Adapter
						searchSongAdapter = new ArrayAdapter<Song>(MainActivity.this, android.R.layout.simple_list_item_1, searchSongList);
						lvSearchSongs.setAdapter(searchSongAdapter);
					}
				}
			});
			break;
		case R.id.btnSearchCancel:
			rlSearchView.setVisibility(View.INVISIBLE);
			break;
		case R.id.btnSearch: //点击的主界面中的搜索按钮
			//显示rllayout
			rlSearchView.setVisibility(View.VISIBLE);
			break;
		case R.id.ivPre:
			int position=app.getPosition();
			position = position == 0 ? 0 : position-1; 
			app.setPosition(position);
			final Song s=list.get(position);
			//获取歌曲的音乐路径 
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
			//先调用model对象的方法 加载下一首歌的基本信息 
			//加载完毕后再播放
			model.loadSongInfoById(s2.getSong_id(), new SongInfoCallback() {
				public void onSongInfoLoaded(Song song) {
					s2.setUrls(song.getUrls());
					s2.setSongInfo(song.getSongInfo());
					String p=s2.getUrls().get(0).getShow_link();
					musicPlayer.playCurrentMusic(p);
				}
			});
			break;
		case R.id.ivCMPic: //圆形图片 rlPlayMusic显示
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
			//把rlPlayMusic隐藏
			TranslateAnimation anim=new TranslateAnimation(0,0,0,rlPlayMusic.getHeight());
			anim.setDuration(400);
			rlPlayMusic.startAnimation(anim);
			rlPlayMusic.setVisibility(View.INVISIBLE);
		}else{
			//启动新的任务栈  跳转到启动界面
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
	}
	
	//为viewpager编写适配器
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
		//解除service的绑定
		this.unbindService(conn);
		//取消广播接收器的注册
		this.unregisterReceiver(receiver);
		//把NewMusicListFragment中的adapter中的线程停掉
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
	 * 接收音乐相关的信息
	 */
	class MusicInfoReceiver extends BroadcastReceiver{
		
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			MusicApplication app=(MusicApplication) getApplication();
			if(action.equals(Consts.ACTION_MUSIC_UPDATE_PROGRESS)){
				//更新音乐进度
				int current=intent.getIntExtra("current", 0);
				int total=intent.getIntExtra("total", 0);
				//更新seekBar
				seekBar.setMax(total);
				seekBar.setProgress(current);
				//更新两个TextView
				tvCMProgress.setText(DateUtils.parseTime(current));
				tvCMTotaltime.setText(DateUtils.parseTime(total));
				//更新歌词TextView
				Song song=app.getCurrentSong();
				String content=song.getCurrentLrc(current);
				if(content!=null){
					tvCMLrc.setText(content);
				}
			}else if(action.equals(Consts.ACTION_MUSIC_START_PLAY)){
				//音乐已经开始播放  需要把界面中的信息  更新
				List<Song> songs = app.getSongList();
				int position=app.getPosition();
				Song song=songs.get(position);
				//更新UI
				String url=song.getPic_small();
				BitmapUtils.loadBitmap(url, new BitmapUtils.BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						//图片加载完成后
						ivCMPic.setImageBitmap(bitmap);
						//让图片旋转
						RotateAnimation anim=new RotateAnimation(0,360,ivCMPic.getWidth()/2, ivCMPic.getHeight()/2);
						anim.setDuration(10000);
						anim.setRepeatCount(Animation.INFINITE);
						//时间插值器  匀速运动
						anim.setInterpolator(new LinearInterpolator());
						ivCMPic.startAnimation(anim);
					}
				});
				tvCMName.setText(song.getTitle());
				//更新rlPlayMusic界面中的控件
				tvCMTitle.setText(song.getTitle());
				tvCMSinger.setText(song.getArtist_name());
				//专辑图片
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
				//背景图片
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
						//图片下载完成后 
						BitmapUtils.loadBluredBitmap(bitmap, 20, new BitmapCallback() {
							public void onBitmapLoaded(Bitmap b) {
								ivCMBackground.setImageBitmap(b);
							}
						});
					}
				});
				//下载歌词 并且显示歌词
				downloadLrc();
			}
		}
		//下载歌词文件
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


