package com.lionsteel.reflexmulti.Scenes.MenuScenes;

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
import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SharedResources;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuButton;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuScene;
import com.lionsteel.reflexmulti.Constants.FlurryAgentEventStrings;
import com.lionsteel.reflexmulti.Constants.GameMode;

public class PracticeModeSelectScene extends ReflexMenuScene
{
	ReflexActivity				activity;
	BuildableBitmapTextureAtlas	sceneAtlas;

	final ReflexMenuButton		freePlayButton;
	final ReflexMenuButton		frenzyButton;
	final ReflexMenuButton		timeAttackButton;

	@Override
	public void logFlurryEvent()
	{	
		FlurryAgent.logEvent(FlurryAgentEventStrings.MULTIPLAYER_GAME_MODE_MENU);
	}
	
	public PracticeModeSelectScene()
	{
		super();
		activity = ReflexActivity.getInstance();

		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/PracticeModeSelectScene/");

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

		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		final float BUTTON_HEIGHT = SharedResources.getInstance().modeRegion[0].getHeight();

		final int START_Y = (int) ((CAMERA_HEIGHT + titleSprite.getHeight() - BUTTON_HEIGHT * 3) / 2) - 20;
		freePlayButton = new ReflexMenuButton(SharedResources.getInstance().modeRegion[GameMode.FREE_PLAY], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.FREE_PLAY);
				mParentScene.clearChildScene();

			}
		});
		freePlayButton.center(START_Y);
		addButton(freePlayButton);

		frenzyButton = new ReflexMenuButton(SharedResources.getInstance().modeRegion[GameMode.FRENZY], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.FRENZY);
				mParentScene.clearChildScene();
			}
		});
		frenzyButton.center(freePlayButton.getBottom());
		addButton(frenzyButton);

		timeAttackButton = new ReflexMenuButton(SharedResources.getInstance().modeRegion[GameMode.TIME_ATTACK], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.TIME_ATTACK);
				mParentScene.clearChildScene();
			}
		});
		timeAttackButton.center(frenzyButton.getBottom());
		addButton(timeAttackButton);

		this.attachChild(titleSprite);

	}

}
