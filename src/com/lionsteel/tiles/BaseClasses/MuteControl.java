package com.lionsteel.tiles.BaseClasses;

import org.andengine.audio.BaseAudioManager;
import org.andengine.audio.IAudioEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;

public abstract class MuteControl<T extends BaseAudioManager<? extends IAudioEntity>> extends TilesMenuButton
{
	private Sprite	cancelSprite;
	private boolean	isMuted;
	protected T		audioManager;

	public MuteControl(TextureRegion buttonRegion)
	{
		super(buttonRegion, null);
		cancelSprite = new Sprite(0, 0, SharedResources.getInstance().cancelImageRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		this.attachChild(cancelSprite);
		cancelSprite.setVisible(isMuted);
	}

	@Override
	protected void runAction()
	{
		if (audioManager.getMasterVolume() > 0)
		{
			audioManager.setMasterVolume(0);
			isMuted = true;
		} else
		{
			audioManager.setMasterVolume(1.0f);
			isMuted = false;
		}
		cancelSprite.setVisible(isMuted);
	}

}
