package cn.tedu.musicclient.entity;

/**
 * 封装一个SongUrl
 */
public class SongUrl {
	private int file_duration;
	private int file_bitrate;
	private String show_link;
	private String song_file_id;
	private String file_size;
	private String file_extension;
	private String file_link;

	public SongUrl() {
		// TODO Auto-generated constructor stub
	}

	public SongUrl(int file_duration, int file_bitrate, String show_link,
			String song_file_id, String file_size, String file_extension,
			String file_link) {
		super();
		this.file_duration = file_duration;
		this.file_bitrate = file_bitrate;
		this.show_link = show_link;
		this.song_file_id = song_file_id;
		this.file_size = file_size;
		this.file_extension = file_extension;
		this.file_link = file_link;
	}

	public int getFile_duration() {
		return file_duration;
	}

	public void setFile_duration(int file_duration) {
		this.file_duration = file_duration;
	}

	public int getFile_bitrate() {
		return file_bitrate;
	}

	public void setFile_bitrate(int file_bitrate) {
		this.file_bitrate = file_bitrate;
	}

	public String getShow_link() {
		return show_link;
	}

	public void setShow_link(String show_link) {
		this.show_link = show_link;
	}

	public String getSong_file_id() {
		return song_file_id;
	}

	public void setSong_file_id(String song_file_id) {
		this.song_file_id = song_file_id;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getFile_extension() {
		return file_extension;
	}

	public void setFile_extension(String file_extension) {
		this.file_extension = file_extension;
	}

	public String getFile_link() {
		return file_link;
	}

	public void setFile_link(String file_link) {
		this.file_link = file_link;
	}

}
