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
	ReflexActivity			activity;
	BitmapTextureAtlas		sceneAtlas;

	private SetupScene		setupScene;

	final int				BUTTON_SPACING	= 150;

	final Sprite			titleSprite;
	final Sprite			versusButton;
	final Sprite			practiceButton;
	//final Sprite		exitButton;
	final ReflexMenuButton	exitButton;

	int						quitPointerID	= -1;
	int						versusPointerID	= -1;

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
		versusButton = new Sprite((CAMERA_WIDTH - versusRegion.getWidth()) / 2, 230, versusRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
				case TouchEvent.ACTION_DOWN:
					versusPointerID = pSceneTouchEvent.getPointerID();
					break;
				case TouchEvent.ACTION_UP:
					if (versusPointerID == pSceneTouchEvent.getPointerID())
						transitionChildScene(setupScene);
					break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		practiceButton = new Sprite((CAMERA_WIDTH - practiceRegion.getWidth()) / 2, 230 + BUTTON_SPACING, practiceRegion, activity.getVertexBufferObjectManager());
		practiceButton.setAlpha(.5f);
		exitButton = new ReflexMenuButton(exitRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.finish();
			}
		});
		exitButton.center(practiceButton.getY()+practiceButton.getHeight());


		this.attachChild(titleSprite);
		this.attachChild(versusButton);
		this.attachChild(practiceButton);
		this.attachChild(exitButton);

		//Must register own touch areas as first screen
		registerTouchAreas();
	}

	@Override
	protected void registerTouchAreas()
	{
		this.registerTouchArea(versusButton);
		//this.registerTouchArea(exitButton);
		exitButton.registerOwnTouchArea(this);
	}

}
