package com.lionsteel.tiles.BaseClasses;

import org.andengine.audio.BaseAudioManager;
import org.andengine.audio.IAudioEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public abstract class MuteControl<T extends BaseAudioManager<? extends IAudioEntity>> extends TilesMenuButton implements TilesConstants
{
	protected TilesMainActivity	activity;
	private Sprite				cancelSprite;
	private boolean				isMuted;
	protected T					audioManager;
	protected String			preferenceString;

	protected abstract void logMute();

	protected abstract void logUnmute();

	public MuteControl(TextureRegion buttonRegion)
	{
		super(buttonRegion, null); 
		activity = TilesMainActivity.getInstance();
		cancelSprite = new Sprite(0, 0, SharedResources.getInstance().cancelImageRegion, activity.getVertexBufferObjectManager());
		this.attachChild(cancelSprite);
	}

	public boolean isMuted()
	{
		return isMuted;
	}

	@Override
	protected void runAction()
	{
		if (audioManager.getMasterVolume() > 0)
		{
			this.logMute();
			audioManager.setMasterVolume(0);
			isMuted = true;
			
		} else
		{
			this.logUnmute();
			audioManager.setMasterVolume(1.0f);
			isMuted = false;
		}
		final SharedPreferences s = activity.getPreferences(Activity.MODE_PRIVATE);
		Editor sEdit = s.edit();
		sEdit.putBoolean(preferenceString, isMuted);
		sEdit.commit();
		cancelSprite.setVisible(isMuted);
	}

	public void refreshButton()
	{
		SharedPreferences s = TilesMainActivity.getInstance().getPreferences(Activity.MODE_PRIVATE);
		isMuted = s.getBoolean(preferenceString, false);
		cancelSprite.setVisible(isMuted);
		if(isMuted)
			audioManager.setMasterVolume(0);
		else
			audioManager.setMasterVolume(1.0f);

	}

}
