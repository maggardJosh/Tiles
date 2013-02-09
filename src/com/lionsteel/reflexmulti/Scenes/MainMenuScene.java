package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SetupScene;

public class MainMenuScene extends ReflexMenuScene
{
	ReflexActivity		activity;
	BitmapTextureAtlas	sceneAtlas;

	private SetupScene	setupScene;

	final int			BUTTON_SPACING	= 150;

	final Sprite		titleSprite;

	int					quitPointerID	= -1;
	int					versusPointerID	= -1;

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
				//TODO: Practice Scene
			}
		});
		practiceButton.center(versusButton.getBottom());
		addButton(practiceButton);

		final ReflexMenuButton exitButton = new ReflexMenuButton(exitRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.finish();
			}
		});
		exitButton.center(practiceButton.getBottom());
		addButton(exitButton);

		this.attachChild(titleSprite);

		//Have to register own touch areas because we are the first scene
		registerTouchAreas();
		registerButtonTouchAreas();
	}

	@Override
	protected void registerTouchAreas()
	{

	}

}
