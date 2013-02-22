package com.lionsteel.tiles.Entities;

import org.andengine.audio.sound.SoundManager;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.TilesSharedPreferenceStrings;
import com.lionsteel.tiles.BaseClasses.MuteControl;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;

public class SoundEffectMuteControl extends MuteControl<SoundManager>
{

	public SoundEffectMuteControl()
	{
		super(SharedResources.getInstance().soundEffectImageRegion);
		preferenceString = TilesSharedPreferenceStrings.isSoundMuted;
		this.audioManager = TilesMainActivity.getInstance().getSoundManager();
		refreshButton();
	}

	@Override
	protected void logMute()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.MUTE_SOUND);
	}

	@Override
	protected void logUnmute()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.UNMUTE_SOUND);
	}

}
