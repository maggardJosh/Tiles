package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.sprite.Sprite;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public class TilesetSelectScene extends ReflexMenuScene implements ReflexConstants
{
	final ReflexActivity		activity;

	final int					START_Y		= 160;
	final TilesetPreviewButton	buttons[]	= new TilesetPreviewButton[tileset.length];

	public TilesetSelectScene()
	{
		super();
		activity = ReflexActivity.getInstance();
		this.setBackgroundEnabled(false);
		float nextYPos = 160;
		for (int x = 0; x < buttons.length; x++)
		{
			buttons[x] = new TilesetPreviewButton(tileset[x]);
			addButton(buttons[x].getButton());
			buttons[x].getButton().center(nextYPos);
			nextYPos = buttons[x].getButton().getBottom();
			
		}

	}

	@Override
	protected void registerTouchAreas()
	{
	
	}
}
