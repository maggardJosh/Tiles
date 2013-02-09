package com.lionsteel.reflexmulti.Scenes;

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

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.SetupScene.GameMode;
import com.lionsteel.reflexmulti.SharedResources;

public class MultiplayerModeSelectScene extends ReflexMenuScene
{
	ReflexActivity				activity;
	BuildableBitmapTextureAtlas	sceneAtlas;

	final ReflexMenuButton		reflexButton;
	final ReflexMenuButton		nonStopButton;
	final ReflexMenuButton		raceButton;

	public MultiplayerModeSelectScene()
	{
		super();
		activity = ReflexActivity.getInstance();

		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/MultiplayerModeSelectScene/");

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
		reflexButton = new ReflexMenuButton(SharedResources.getInstance().modeRegion[GameMode.REFLEX], new Runnable()
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

		nonStopButton = new ReflexMenuButton(SharedResources.getInstance().modeRegion[GameMode.NON_STOP], new Runnable()
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

		raceButton = new ReflexMenuButton(SharedResources.getInstance().modeRegion[GameMode.RACE], new Runnable()
		{
			@Override
			public void run()
			{
				//TODO: Race mode
				//SetupScene.setGameMode(GameMode.RACE);
				//mParentScene.clearChildScene();
			}
		});
		raceButton.center(nonStopButton.getBottom());
		addButton(raceButton);

		this.attachChild(titleSprite);

	}

}
