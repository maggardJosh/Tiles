package com.lionsteel.tiles.Entities;

import org.andengine.audio.music.MusicManager;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.TilesSharedPreferenceStrings;
import com.lionsteel.tiles.BaseClasses.MuteControl;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;

public class MusicMuteControl extends MuteControl<MusicManager>
{
	public MusicMuteControl()
	{
		super(SharedResources.getInstance().musicNoteRegion);
		preferenceString = TilesSharedPreferenceStrings.isMusicMuted;
		audioManager = TilesMainActivity.getInstance().getMusicManager();
		refreshButton();
	}

	@Override
	protected void logMute()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.MUTE_MUSIC);
	}

	@Override
	protected void logUnmute()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.UNMUTE_MUSIC);
	}
}
