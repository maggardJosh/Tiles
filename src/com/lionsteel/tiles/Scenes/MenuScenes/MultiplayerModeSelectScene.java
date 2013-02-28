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

public class MultiplayerModeSelectScene extends TilesMenuScene
{
	TilesMainActivity				activity;
	BuildableBitmapTextureAtlas	sceneAtlas;

	final TilesMenuButton		reflexButton;
	final TilesMenuButton		nonStopButton;
	final TilesMenuButton		raceButton;

	final int TITLE_Y = 60;
	
	@Override
	public void logFlurryEvent()
	{	
		FlurryAgent.logEvent(FlurryAgentEventStrings.MULTIPLAYER_GAME_MODE_MENU);
	}
	
	public MultiplayerModeSelectScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
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

		final Sprite titleSprite = new Sprite((CAMERA_WIDTH-titleRegion.getWidth())/2, TITLE_Y, titleRegion, activity.getVertexBufferObjectManager());
		final float BUTTON_HEIGHT = SharedResources.getInstance().modeRegion[0].getHeight();

		final int START_Y = (int) ((CAMERA_HEIGHT + TITLE_Y+ titleSprite.getHeight() - BUTTON_HEIGHT * 3) / 2) - 20;
		reflexButton = new TilesMenuButton(SharedResources.getInstance().modeRegion[GameMode.REFLEX], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.REFLEX);
				mParentScene.clearChildScene();

			}
		});
		reflexButton.center(START_Y);
		addButton(reflexButton);

		nonStopButton = new TilesMenuButton(SharedResources.getInstance().modeRegion[GameMode.NON_STOP], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.NON_STOP);
				mParentScene.clearChildScene();
			}
		});
		nonStopButton.center(reflexButton.getBottom());
		addButton(nonStopButton);

		raceButton = new TilesMenuButton(SharedResources.getInstance().modeRegion[GameMode.RACE], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setGameMode(GameMode.RACE);
				mParentScene.clearChildScene();
			}
		});
		raceButton.center(nonStopButton.getBottom());
		addButton(raceButton);

		this.attachChild(titleSprite);

	}

	@Override
	public void initScene()
	{
		//Nothing to init
	}

}
