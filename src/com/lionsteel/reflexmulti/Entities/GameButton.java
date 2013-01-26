package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.Scenes.GameScene;

public class GameButton implements ReflexConstants
{
	final ReflexActivity		activity;
	final BitmapTextureAtlas	buttonAtlas;
	final TextureRegion			buttonRegion;
	public final Sprite			buttonSprite;
	private final int			playerOwner;
	
	private final int			buttonNumber;
	private final GameScene		parent;
	
	public GameButton(final int buttonNumber, final GameScene parent,
			final int player)
	{
		activity = ReflexActivity.getInstance();
		this.buttonNumber = buttonNumber;
		playerOwner = player;
		this.parent = parent;
		buttonAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512);
		buttonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buttonAtlas, activity, "button" + buttonNumber + ".png", 0, 0);
		buttonAtlas.load();
		buttonSprite = new Sprite(0, 0, buttonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) onTouched();
				return false;
			}
		};
	}
	
	public int getButtonNumber()
	{
		return buttonNumber;
	}
	
	public int getPlayer()
	{
		return playerOwner;
	}
	
	private void onTouched()
	{
		parent.buttonPressed(this);
	}
	
}
