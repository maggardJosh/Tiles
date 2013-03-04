package com.lionsteel.tiles.Scenes.MenuScenes;

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
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.TilesSharedPreferenceStrings;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;

public class MainMenuScene extends TilesMenuScene
{
	TilesMainActivity			activity;
	BuildableBitmapTextureAtlas	sceneAtlas;

	private SetupScene			setupScene;

	final int					BUTTON_SPACING	= 150;

	final Sprite				titleSprite;

	final int					TITLE_PADDING	= 40;

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.MAIN_MENU);
	}

	public MainMenuScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

		

		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 512);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/MainMenuScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png");
		final TextureRegion versusRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "versusButton.png");
		final TextureRegion practiceRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "practiceButton.png");
		final TextureRegion exitRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "exitButton.png");

		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		this.setBackgroundEnabled(false);

		titleSprite = new Sprite((CAMERA_WIDTH - titleRegion.getWidth()) / 2, TITLE_PADDING, titleRegion, activity.getVertexBufferObjectManager());
		final TilesMenuButton versusButton = new TilesMenuButton(versusRegion, new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(activity.sharedPrefs.getInt(TilesSharedPreferenceStrings.lastVersusMode, GameMode.REFLEX), true);
				transitionChildScene(setupScene);

			}
		});
		versusButton.center(230);
		addButton(versusButton);

		final TilesMenuButton practiceButton = new TilesMenuButton(practiceRegion, new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(activity.sharedPrefs.getInt(TilesSharedPreferenceStrings.lastPracticeMode, GameMode.FREE_PLAY), true);
				transitionChildScene(setupScene);
			}
		});
		practiceButton.center(versusButton.getBottom());
		addButton(practiceButton);

		final MainMenuScene instance = this;
		final TilesMenuButton exitButton = new TilesMenuButton(exitRegion, new Runnable()
		{
			@Override
			public void run()
			{
				FlurryAgent.logEvent(FlurryAgentEventStrings.QUIT_BUTTON);
				activity.showQuitPrompt(instance);
			}
		});
		exitButton.center(practiceButton.getBottom());
		addButton(exitButton);

		this.attachChild(titleSprite);

		//Have to register own touch areas because we are the first scene
		registerTouchAreas();
		setupScene = SetupScene.getInstance();
	}

	@Override
	public void initScene()
	{
		// Nothing to init

	}

}
