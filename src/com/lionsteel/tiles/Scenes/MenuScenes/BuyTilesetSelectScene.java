package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.BuyTilesetPreviewButton;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.util.Inventory;

public class BuyTilesetSelectScene extends TilesMenuScene implements TilesConstants, IScrollDetectorListener, IOnSceneTouchListener
{
	final TilesMainActivity					activity;

	final SurfaceScrollDetector				scrollDetector;

	final float								SCROLL_SPEED	= .8f;
	final int								START_Y			= 160;
	float									MAX_Y;

	final BuyTilesetPreviewButton			buttons[]		= new BuyTilesetPreviewButton[Tileset.purchaseableTilesets.length];

	private static BuyTilesetSelectScene	instance;

	public static BuyTilesetSelectScene getInstance()
	{
		if (instance == null)
			instance = new BuyTilesetSelectScene();
		return instance;
	}

	public BuyTilesetSelectScene()
	{
		super();

		instance = this;

		scrollDetector = new SurfaceScrollDetector(this);
		setOnSceneTouchListener(this);
		setOnAreaTouchTraversalBackToFront();

		activity = TilesMainActivity.getInstance();
		this.setBackgroundEnabled(false);
		float nextYPos = 160;
		for (int x = 0; x < buttons.length; x++)
		{
			buttons[x] = new BuyTilesetPreviewButton(Tileset.purchaseableTilesets[x]);
			addButton(buttons[x].getButton());
			buttons[x].getButton().center(nextYPos);
			nextYPos = buttons[x].getButton().getBottom();

		}
		MAX_Y = nextYPos + 70;

	}

	public void clearButtons()
	{
		for (BuyTilesetPreviewButton button : buttons)
		{
			if (button == null)
				continue;
			removeButton(button.getButton());
			button.clear();
		}
	}

	public void redoButtons(Inventory inv)
	{
		clearButtons();
		float nextYPos = 160;
		for (int x = 0; x < buttons.length; x++)
		{
			//TODO: Remove Buttons when they are bought

			buttons[x] = new BuyTilesetPreviewButton(Tileset.purchaseableTilesets[x]);
			addButton(buttons[x].getButton());
			buttons[x].getButton().center(nextYPos);
			nextYPos = buttons[x].getButton().getBottom();

		}
		MAX_Y = nextYPos + 70;
	}

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.TILESET_MENU);
	}

	@Override
	public void initScene()
	{
		setY(0);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		scrollDetector.onSceneTouchEvent(pScene, pSceneTouchEvent);
		return true;
	}

	private void boundScene()
	{
		if (getY() > 0)
			setY(0);
		if (getY() - CAMERA_HEIGHT < -MAX_Y)
			setY(-MAX_Y + CAMERA_HEIGHT);
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		unsetAllButtons();
		this.setY(getY() + pDistanceY * SCROLL_SPEED);
		boundScene();
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		this.setY(getY() + pDistanceY * SCROLL_SPEED);
		boundScene();
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		this.setY(getY() + pDistanceY * SCROLL_SPEED);
		boundScene();
	}

	public static void clear()
	{
		instance = null;

	}

}
