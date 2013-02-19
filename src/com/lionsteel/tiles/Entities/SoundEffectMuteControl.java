package com.lionsteel.tiles.Entities;

import org.andengine.audio.sound.SoundManager;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.MuteControl;

public class SoundEffectMuteControl extends MuteControl<SoundManager>
{

	public SoundEffectMuteControl()
	{
		super(SharedResources.getInstance().soundEffectImageRegion);
		this.audioManager = TilesMainActivity.getInstance().getSoundManager();
	}

}
