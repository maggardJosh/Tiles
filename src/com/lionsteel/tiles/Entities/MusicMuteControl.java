package com.lionsteel.tiles.Entities;

import org.andengine.audio.music.MusicManager;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.MuteControl;

public class MusicMuteControl extends MuteControl<MusicManager>
{
	public MusicMuteControl()
	{
		super(SharedResources.getInstance().musicNoteRegion);
		audioManager = TilesMainActivity.getInstance().getMusicManager();
	}
}
