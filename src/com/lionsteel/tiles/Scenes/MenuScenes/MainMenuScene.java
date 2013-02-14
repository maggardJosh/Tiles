package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;

public class MainMenuScene extends TilesMenuScene
{
	TilesMainActivity		activity;
	BitmapTextureAtlas	sceneAtlas;

	private SetupScene	setupScene;

	final int			BUTTON_SPACING	= 150;

	final Sprite		titleSprite;

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.MAIN_MENU);
	}

	public MainMenuScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

		setupScene = new SetupScene();

		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 512);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/MainMenuScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", 0, 0);
		final TextureRegion versusRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "versusButton.png", (int) titleRegion.getWidth(), 0);
		final TextureRegion practiceRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "practiceButton.png", (int) versusRegion.getTextureX(), (int) versusRegion.getHeight());
		final TextureRegion exitRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "exitButton.png", (int) 0, (int) (titleRegion.getHeight()));

		sceneAtlas.load();

		this.setBackgroundEnabled(false);

		titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		final TilesMenuButton versusButton = new TilesMenuButton(versusRegion, new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.REFLEX, true);
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
				SetupScene.setGameMode(GameMode.FREE_PLAY, true);
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
	}

	@Override
	public void initScene()
	{
		// Nothing to init
		
	}

}
