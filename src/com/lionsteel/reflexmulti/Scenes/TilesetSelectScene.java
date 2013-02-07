package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.sprite.Sprite;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public class TilesetSelectScene extends ReflexMenuScene implements
		ReflexConstants
{
	final ReflexActivity		activity;
	
	final int					START_Y		= 160;
	final TilesetPreviewButton	buttons[]	= new TilesetPreviewButton[tileset.length];
	
	public TilesetSelectScene()
	{
		super();
		activity = ReflexActivity.getInstance();
		this.setBackgroundEnabled(false);
		
		for (int x = 0; x < buttons.length; x++)
		{
			buttons[x] = new TilesetPreviewButton(tileset[x]);
			final Sprite buttonSprite = buttons[x].getSprite();
			buttonSprite.setPosition((CAMERA_WIDTH - buttonSprite.getWidth()) / 2, START_Y + x * buttonSprite.getHeight());
			this.attachChild(buttonSprite);
		}

	}
	
	@Override
	protected void registerTouchAreas()
	{
		for (int x = 0; x < buttons.length; x++)
			this.registerTouchArea(buttons[x].getSprite());
		
	}
}
