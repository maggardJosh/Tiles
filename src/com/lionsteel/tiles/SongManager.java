package com.lionsteel.tiles;

import org.andengine.audio.music.Music;
import org.andengine.engine.handler.IUpdateHandler;

import com.lionsteel.tiles.Constants.TilesConstants;

public class SongManager implements IUpdateHandler
{
	public static SongManager	instance;

	private Music				currentSong;
	private Music				nextSong;
	private boolean				isPlaying	= false;

	public static SongManager getInstance()
	{
		if (instance == null)
			instance = new SongManager();
		return instance;
	}

	public SongManager()
	{
		instance = this;
	}

	public void playSong(final Music nextSong)
	{
		this.nextSong = nextSong;
	}

	public void pause()
	{
		isPlaying = false;
		if (currentSong != null)
			currentSong.pause();
	}

	public void unpause()
	{
		isPlaying = true;
		if (currentSong != null)
			currentSong.play();
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		if (!isPlaying)
			return;
		if (currentSong == null)
		{
			if (nextSong != null)
				goNextSong();
			else
				return;
		}
		if (nextSong != null)
			fadeOutCurrentSong(pSecondsElapsed);
		else
			fadeInCurrentSong(pSecondsElapsed);
	}

	private void goNextSong()
	{
		if (currentSong != null)
		{
			currentSong.setVolume(0);
			currentSong.pause();
		}
		nextSong.setVolume(0);	
		nextSong.play();
		nextSong.setLooping(true);
		currentSong = nextSong;
		nextSong = null;
	}

	private void fadeOutCurrentSong(final float pSecondsElapsed)
	{
		if (currentSong.getVolume() > TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed)
			currentSong.setVolume(currentSong.getVolume() - TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed);
		else
			goNextSong();

	}

	private void fadeInCurrentSong(final float pSecondsElapsed)
	{
		if (currentSong.getVolume() < 1.0f - TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed)
			currentSong.setVolume(currentSong.getVolume() + TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed);
		else
			currentSong.setVolume(1.0f);
	}

	@Override
	public void reset()
	{

	}

	public static void clear()
	{
		instance = null;

	}

}
