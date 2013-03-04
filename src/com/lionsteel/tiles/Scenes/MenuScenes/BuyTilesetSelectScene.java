package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

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

	final float								SCROLL_SPEED			= .8f;
	final int								START_Y					= 160;
	float									MAX_Y;
	final int								TITLE_Y					= 40;
	final int								TITLE_BOTTOM_PADDING	= 10;

	final Sprite							titleSprite;

	final BuyTilesetPreviewButton			buttons[]				= new BuyTilesetPreviewButton[Tileset.purchaseableTilesets.length];

	private static BuyTilesetSelectScene	instance;

	public static BuyTilesetSelectScene getInstance()
	{
		if (instance == null)
			instance = new BuyTilesetSelectScene();
		return instance;
	}

	private BuyTilesetSelectScene()
	{
		super();

		instance = this;

		scrollDetector = new SurfaceScrollDetector(this);
		setOnSceneTouchListener(this);

		activity = TilesMainActivity.getInstance();
		this.setBackgroundEnabled(false);

		final BuildableBitmapTextureAtlas sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/BuyTilesetsScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png");

		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		titleSprite = new Sprite((CAMERA_WIDTH - titleRegion.getWidth()) / 2, TITLE_Y, titleRegion, activity.getVertexBufferObjectManager());
		this.attachChild(titleSprite);

		float nextYPos = TITLE_Y + titleSprite.getHeight() + TITLE_BOTTOM_PADDING;
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
		}
	}

	public void resetScrollDetector()
	{
		this.scrollDetector.reset();
	}

	public void redoButtons()
	{
		clearButtons();
		clearTouchAreas();
		float nextYPos = TITLE_Y + titleSprite.getHeight() + TITLE_BOTTOM_PADDING;
		for (int x = 0; x < buttons.length; x++)
		{
			if (!Tileset.isPurchased(Tileset.purchaseableTilesets[x]))
			{
				addButton(buttons[x].getButton());
				buttons[x].getButton().center(nextYPos);
				nextYPos = buttons[x].getButton().getBottom();
			}
		}
		MAX_Y = nextYPos + 70;
		registerTouchAreas();
	}

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.TILESET_MENU);
	}

	@Override
	public void initScene()
	{
		resetScrollDetector();
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
