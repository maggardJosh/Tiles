package com.lionsteel.tiles;

import org.andengine.audio.music.Music;
import org.andengine.engine.handler.IUpdateHandler;

import android.media.MediaRouter.VolumeCallback;

import com.lionsteel.tiles.Constants.TilesConstants;

public class SongManager implements IUpdateHandler
{
	public static SongManager	instance;

	private Music				currentSong;
	private Music				nextSong;
	private boolean				isFadingOut				= false;
	private boolean				isPlaying				= false;
	private float				currentSongVolume		= 0;
	private float				songVolumeMultiplier	= 1.0f;

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

	public void fadeOut()
	{
		isFadingOut = true;
	}

	public void playSong(final Music nextSong)
	{
		isFadingOut = false;
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
		if (isFadingOut)
		{
			fadeOutCurrentSong(pSecondsElapsed);
			currentSong.setVolume(currentSongVolume * songVolumeMultiplier);
			return;
		}
		if (nextSong != null)
			fadeOutCurrentSong(pSecondsElapsed);
		else
			fadeInCurrentSong(pSecondsElapsed);

		currentSong.setVolume(currentSongVolume * songVolumeMultiplier);
	}

	private void goNextSong()
	{
		if (isFadingOut)
			return;
		if (currentSong != null)
		{
			currentSong.setVolume(0);
			currentSong.pause();
		}
		this.currentSongVolume = 0;
		nextSong.setVolume(currentSongVolume);
		nextSong.play();
		nextSong.setLooping(true);
		currentSong = nextSong;
		nextSong = null;
	}

	public void setVolumeMultiplier(final float newMultiplier)
	{
		this.songVolumeMultiplier = newMultiplier;
	}

	private void fadeOutCurrentSong(final float pSecondsElapsed)
	{
		if (currentSongVolume > TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed)
			currentSongVolume -= TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed;
		else
			goNextSong();

	}

	private void fadeInCurrentSong(final float pSecondsElapsed)
	{
		if (currentSongVolume < 1.0f - TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed)
			currentSongVolume += TilesConstants.SONG_TRANSITION_SPEED * pSecondsElapsed;
		else
			currentSongVolume = 1.0f;
	}

	@Override
	public void reset()
	{

	}

	public static void clear()
	{
		instance = null;

	}

	public void setCurrentVolume(float currentVolume)
	{
		this.currentSongVolume = currentVolume;

	}

}
