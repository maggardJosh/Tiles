package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Entities.TouchControls.ReadyTouchControl;

public class PauseScene extends TilesMenuScene
{
	private boolean				playerOneReady	= false;
	private boolean				playerTwoReady	= false;

	private ReadyTouchControl	playerOneTouch;
	private ReadyTouchControl	playerTwoTouch;

	private boolean				isTwoPlayerMode	= true;

	final Sprite				playerTwoResumeSprite;
	private TilesMainActivity	activity;

	private TilesMenuButton		exitButton;

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.PAUSE);
	}

	public PauseScene()
	{
		activity = TilesMainActivity.getInstance();
		BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256);
		this.setBackgroundEnabled(false);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/PauseScene/");
		final TextureRegion pausedRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "paused.png");
		final TextureRegion resumeRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "holdToResume.png");

		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		final int resumePadding = 20;

		final Sprite pausedSprite = new Sprite((CAMERA_WIDTH - pausedRegion.getWidth()) / 2, (CAMERA_HEIGHT - pausedRegion.getHeight()) / 2, pausedRegion, activity.getVertexBufferObjectManager());
		final Sprite playerOneResumeSprite = new Sprite((CAMERA_WIDTH - resumeRegion.getWidth()) / 2, (CAMERA_HEIGHT - resumeRegion.getHeight() - resumePadding), resumeRegion, activity.getVertexBufferObjectManager());
		playerTwoResumeSprite = new Sprite((CAMERA_WIDTH - resumeRegion.getWidth()) / 2, resumePadding, resumeRegion, activity.getVertexBufferObjectManager());
		playerTwoResumeSprite.setRotationCenter(resumeRegion.getWidth() / 2, resumeRegion.getHeight() / 2);
		playerTwoResumeSprite.setRotation(180);

		final Rectangle background = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		background.setColor(0, 0, 0, .9f);
		
		exitButton = new TilesMenuButton(SharedResources.getInstance().exitGameButtonRegion, new Runnable(){
			@Override
			public void run()
			{
				activity.onBackPressed();
			}
		});
		
		exitButton.setPosition(3, (CAMERA_HEIGHT-exitButton.getHeight())/2);

		this.attachChild(background);
		this.attachChild(pausedSprite);
		this.attachChild(playerOneResumeSprite);
		this.attachChild(playerTwoResumeSprite);

		prepareTouchControls();

		this.attachChild(playerOneTouch);
		this.attachChild(playerTwoTouch);

		addButton(exitButton);
		
	}

	public void setTwoPlayerMode(final boolean isTwoPlayerMode)
	{
		if (this.isTwoPlayerMode == isTwoPlayerMode)
			return;
		this.isTwoPlayerMode = isTwoPlayerMode;
		if (!this.isTwoPlayerMode)
		{
			this.detachChild(playerTwoTouch);
			this.detachChild(playerTwoResumeSprite);
			playerTwoReady = true;
		} else
		{
			this.attachChild(playerTwoTouch);
			this.attachChild(playerTwoResumeSprite);
			playerTwoReady = false;
		}

	}

	private void prepareTouchControls()
	{
		playerOneTouch = new ReadyTouchControl(new Runnable()
		{
			@Override
			public void run()
			{
				playerOneReady = true;
				if (playerTwoReady)
					mParentScene.clearChildScene();
			}
		}, new Runnable()
		{

			@Override
			public void run()
			{
				playerOneReady = false;
			}
		});

		playerTwoTouch = new ReadyTouchControl(new Runnable()
		{
			@Override
			public void run()
			{
				playerTwoReady = true;
				if (playerOneReady)
					mParentScene.clearChildScene();
			}
		}, new Runnable()
		{

			@Override
			public void run()
			{
				playerTwoReady = false;
			}
		});

		final int PAUSE_TOUCH_PADDING = 120;

		playerOneTouch.setPosition((CAMERA_WIDTH - playerOneTouch.touchImage.getWidth()) / 2, CAMERA_HEIGHT - playerOneTouch.touchImage.getHeight() - PAUSE_TOUCH_PADDING);
		playerTwoTouch.setPosition((CAMERA_WIDTH - playerTwoTouch.touchImage.getWidth()) / 2, PAUSE_TOUCH_PADDING);
		playerTwoTouch.setRotation(180);
	}

	@Override
	public void registerTouchAreas()
	{
		registerTouchArea(playerOneTouch.touchImage);
		if (isTwoPlayerMode)
			registerTouchArea(playerTwoTouch.touchImage);
		super.registerTouchAreas();
	}

	@Override
	public void initScene()
	{
		playerOneTouch.initButton();
		playerTwoTouch.initButton();
		
	}
}