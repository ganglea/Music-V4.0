package cn.tedu.musicclient.entity;

/**
 * 封装音乐的基本信息
 */
public class SongInfo {
	private String album_1000_1000;
	private String album_500_500;
	private String artist_500_500;
	private String album_title;
	private String title;
	private String lrclink;
	private String artist_480_800;
	private String artist_id;
	private String album_id;
	private String artist_640_1136;
	private String publishtime;
	private String author;
	private String song_id;

	public SongInfo() {
		// TODO Auto-generated constructor stub
	}

	public SongInfo(String album_1000_1000, String album_500_500,
			String artist_500_500, String album_title, String title,
			String lrclink, String artist_480_800, String artist_id,
			String album_id, String artist_640_1136, String publishtime,
			String author, String song_id) {
		super();
		this.album_1000_1000 = album_1000_1000;
		this.album_500_500 = album_500_500;
		this.artist_500_500 = artist_500_500;
		this.album_title = album_title;
		this.title = title;
		this.lrclink = lrclink;
		this.artist_480_800 = artist_480_800;
		this.artist_id = artist_id;
		this.album_id = album_id;
		this.artist_640_1136 = artist_640_1136;
		this.publishtime = publishtime;
		this.author = author;
		this.song_id = song_id;
	}

	public String getAlbum_1000_1000() {
		return album_1000_1000;
	}

	public void setAlbum_1000_1000(String album_1000_1000) {
		this.album_1000_1000 = album_1000_1000;
	}

	public String getAlbum_500_500() {
		return album_500_500;
	}

	public void setAlbum_500_500(String album_500_500) {
		this.album_500_500 = album_500_500;
	}

	public String getArtist_500_500() {
		return artist_500_500;
	}

	public void setArtist_500_500(String artist_500_500) {
		this.artist_500_500 = artist_500_500;
	}

	public String getAlbum_title() {
		return album_title;
	}

	public void setAlbum_title(String album_title) {
		this.album_title = album_title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLrclink() {
		return lrclink;
	}

	public void setLrclink(String lrclink) {
		this.lrclink = lrclink;
	}

	public String getArtist_480_800() {
		return artist_480_800;
	}

	public void setArtist_480_800(String artist_480_800) {
		this.artist_480_800 = artist_480_800;
	}

	public String getArtist_id() {
		return artist_id;
	}

	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}

	public String getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(String album_id) {
		this.album_id = album_id;
	}

	public String getArtist_640_1136() {
		return artist_640_1136;
	}

	public void setArtist_640_1136(String artist_640_1136) {
		this.artist_640_1136 = artist_640_1136;
	}

	public String getPublishtime() {
		return publishtime;
	}

	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSong_id() {
		return song_id;
	}

	public void setSong_id(String song_id) {
		this.song_id = song_id;
	}

}
