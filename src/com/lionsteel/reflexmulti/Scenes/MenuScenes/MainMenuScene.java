package com.lionsteel.reflexmulti.Scenes.MenuScenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.flurry.android.FlurryAgent;
import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuScene;

public class MainMenuScene extends ReflexMenuScene
{
	ReflexActivity		activity;
	BitmapTextureAtlas	sceneAtlas;

	private SetupScene	setupScene;

	final int			BUTTON_SPACING	= 150;

	final Sprite		titleSprite;

	public MainMenuScene()
	{
		super();
		activity = ReflexActivity.getInstance();

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
		final ReflexMenuButton versusButton = new ReflexMenuButton(versusRegion, new Runnable()
		{
			@Override
			public void run()
			{
				FlurryAgent.logEvent("Multiplayer_Setup");
				transitionChildScene(setupScene);

			}
		});
		versusButton.center(230);
		addButton(versusButton);

		final ReflexMenuButton practiceButton = new ReflexMenuButton(practiceRegion, new Runnable()
		{
			@Override
			public void run()
			{
				FlurryAgent.logEvent("Practice_Menu");
				//TODO: Practice Scene
			}
		});
		practiceButton.center(versusButton.getBottom());
		addButton(practiceButton);
		
		final MainMenuScene instance = this;
		final ReflexMenuButton exitButton = new ReflexMenuButton(exitRegion, new Runnable()
		{
			@Override
			public void run()
			{
				FlurryAgent.logEvent("Quit_Button");
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
	public void clearChildScene()
	{
		FlurryAgent.logEvent("Main_Menu");
		super.clearChildScene();
	}


}
