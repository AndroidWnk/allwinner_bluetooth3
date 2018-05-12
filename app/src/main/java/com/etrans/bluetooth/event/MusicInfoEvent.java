package com.etrans.bluetooth.event;


public class MusicInfoEvent {
	/***
	 * 歌曲名字
	 */
	public String name;
	/***
	 * 艺术家
	 */
	public String artist;

	/***
	 * 当前歌曲在总歌曲中的位置
	 */
	public int pos;
	/***
	 * 总的歌曲数目
	 */
	public int total;
	/***
	 * 歌曲时长  <br/>
	 * 单位 ms
	 */
	public int duration;
	
	public MusicInfoEvent(String name,String artist,int duration,int pos,int total){
		this.name = name;
		this.artist = artist;
		this.duration = duration;
		this.pos = pos;
		this.total = total;
	}
}
