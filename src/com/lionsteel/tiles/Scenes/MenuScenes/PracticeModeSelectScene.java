package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.sprite.Sprite;
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
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;

public class PracticeModeSelectScene extends TilesMenuScene
{
	TilesMainActivity			activity;
	BuildableBitmapTextureAtlas	sceneAtlas;

	final TilesMenuButton		freePlayButton;
	final TilesMenuButton		frenzyButton;
	final TilesMenuButton		timeAttackButton;

	final int					TITLE_Y					= 60;
	final int					TITLE_BOTTOM_PADDING	= 40;
	final int					BUTTON_SPACING			= 35;

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.PRACTICE_GAME_MODE_MENU);
	}

	public PracticeModeSelectScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/ModeSelectScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png");

		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		this.setBackgroundEnabled(false);

		final Sprite titleSprite = new Sprite((CAMERA_WIDTH - titleRegion.getWidth()) / 2, TITLE_Y, titleRegion, activity.getVertexBufferObjectManager());

		freePlayButton = new TilesMenuButton(SharedResources.getInstance().modeRegion[GameMode.FREE_PLAY], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.FREE_PLAY);
				mParentScene.clearChildScene();

			}
		});
		freePlayButton.center(titleSprite.getY() + titleSprite.getHeight() + TITLE_BOTTOM_PADDING);
		addButton(freePlayButton);

		frenzyButton = new TilesMenuButton(SharedResources.getInstance().modeRegion[GameMode.FRENZY], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.FRENZY);
				mParentScene.clearChildScene();
			}
		});
		frenzyButton.center(freePlayButton.getBottom() + BUTTON_SPACING);
		addButton(frenzyButton);

		timeAttackButton = new TilesMenuButton(SharedResources.getInstance().modeRegion[GameMode.TIME_ATTACK], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.TIME_ATTACK);
				mParentScene.clearChildScene();
			}
		});
		timeAttackButton.center(frenzyButton.getBottom() + BUTTON_SPACING);
		addButton(timeAttackButton);

		this.attachChild(titleSprite);

	}

	@Override
	public void initScene()
	{
		//Nothing to init
	}

	@Override
	protected void exitScene()
	{
		// TODO Auto-generated method stub

	}

}
