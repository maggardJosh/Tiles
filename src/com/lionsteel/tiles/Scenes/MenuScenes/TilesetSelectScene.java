package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Entities.TilesetPreviewButton;
import com.lionsteel.tiles.util.Inventory;

public class TilesetSelectScene extends TilesMenuScene implements TilesConstants, IScrollDetectorListener, IOnSceneTouchListener
{
	final TilesMainActivity				activity;

	final BuyTilesetSelectScene			buyTilesetSelectScene;

	final SurfaceScrollDetector			scrollDetector;

	final float							SCROLL_SPEED	= .8f;
	final int							START_Y			= 160;
	float								MAX_Y;

	final TilesetPreviewButton			buttons[]		= new TilesetPreviewButton[Tileset.tilesetList.length];
	final TilesMenuButton				buyTilesetsButton;

	private static TilesetSelectScene	instance;

	public static TilesetSelectScene getInstance()
	{
		if (instance == null)
			instance = new TilesetSelectScene();
		return instance;
	}

	public TilesetSelectScene()
	{
		super();

		instance = this;

		buyTilesetSelectScene = BuyTilesetSelectScene.getInstance();

		scrollDetector = new SurfaceScrollDetector(this);
		setOnSceneTouchListener(this);

		activity = TilesMainActivity.getInstance();
		this.setBackgroundEnabled(false);
		float nextYPos = 160;
		for (int x = 0; x < buttons.length; x++)
		{

			buttons[x] = new TilesetPreviewButton(Tileset.tilesetList[x]);
			if (!Tileset.isPurchasable(Tileset.tilesetList[x]))
			{
				addButton(buttons[x].getButton());
				buttons[x].getButton().center(nextYPos);
				nextYPos = buttons[x].getButton().getBottom();
			}

		}

		buyTilesetsButton = new TilesMenuButton(SharedResources.getInstance().buyTilesetButtonRegion, new Runnable()
		{

			@Override
			public void run()
			{
				if (TilesMainActivity.getInstance().getIABHelper() != null && 
						TilesMainActivity.getInstance().canQuery() && 
						TilesMainActivity.getInstance().getArePurchasesLoaded())
					transitionChildScene(BuyTilesetSelectScene.getInstance());
				else
				{
					if (TilesMainActivity.getInstance().getIABHelper() == null)
						TilesMainActivity.getInstance().setupIABHelper();
					else
					{
						TilesMainActivity.getInstance().queryPurchases();
					}
				}
			}
		});
		buyTilesetsButton.center(nextYPos);
		nextYPos = buyTilesetsButton.getBottom();
		addButton(buyTilesetsButton);

		MAX_Y = nextYPos + 70;

	}

	public void clearButtons()
	{
		for (TilesetPreviewButton button : buttons)
		{
			if (button == null)
				continue;
			removeButton(button.getButton());
			clearTouchAreas();
		}
		removeButton(buyTilesetsButton);
	}

	public void redoButtons()
	{
		clearButtons();

		BuyTilesetSelectScene.getInstance().redoButtons();

		float nextYPos = 160;
		for (int x = 0; x < buttons.length; x++)
		{

			if (Tileset.isPurchasable(Tileset.tilesetList[x]))
			{
				if (Tileset.isPurchased(Tileset.tilesetList[x]))
				{
					addButton(buttons[x].getButton());
					buttons[x].getButton().center(nextYPos);
					nextYPos = buttons[x].getButton().getBottom();
				}
			} else
			{
				addButton(buttons[x].getButton());
				buttons[x].getButton().center(nextYPos);
				nextYPos = buttons[x].getButton().getBottom();
			}

		}
		buyTilesetsButton.center(nextYPos);
		nextYPos = buyTilesetsButton.getBottom();
		addButton(buyTilesetsButton);

		registerTouchAreas();

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
		scrollDetector.reset();
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
		if (getY() - CAMERA_HEIGHT < -MAX_Y)
			setY(-MAX_Y + CAMERA_HEIGHT);
		if (getY() > 0)
			setY(0);
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
