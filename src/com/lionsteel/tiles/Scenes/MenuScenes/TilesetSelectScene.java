package com.lionsteel.tiles.Scenes.MenuScenes;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.TilesetPreviewButton;

public class TilesetSelectScene extends TilesMenuScene implements TilesConstants
{
	final TilesMainActivity		activity;

	final int					START_Y		= 160;
	final TilesetPreviewButton	buttons[]	= new TilesetPreviewButton[tileset.length];

	public TilesetSelectScene()
	{
		super();
		activity = TilesMainActivity.getInstance();
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
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.TILESET_MENU);
	}

	@Override
	public void initScene()
	{
		//Nothing to init
	}
}
