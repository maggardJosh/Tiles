package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
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
	
	final Sprite				reflexButton;
	final Sprite				nonStopButton;
	final Sprite				raceButton;
	
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
			sceneAtlas.load();// load(activity.getTextureManager());
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		this.setBackgroundEnabled(false);
		
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		final float BUTTON_WIDTH = SharedResources.getInstance().modeRegion[0].getWidth();
		final float BUTTON_HEIGHT = SharedResources.getInstance().modeRegion[0].getHeight();
		
		final int START_Y = (int) ((CAMERA_HEIGHT + titleSprite.getHeight() - BUTTON_HEIGHT * 3) / 2) - 20;
		reflexButton = new Sprite((CAMERA_WIDTH - BUTTON_WIDTH) / 2, START_Y, SharedResources.getInstance().modeRegion[GameMode.REFLEX], activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						SetupScene.setGameMode(GameMode.REFLEX);
						mParentScene.clearChildScene();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		nonStopButton = new Sprite((CAMERA_WIDTH - BUTTON_WIDTH) / 2, reflexButton.getY() + reflexButton.getHeight(), SharedResources.getInstance().modeRegion[GameMode.NON_STOP], activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						SetupScene.setGameMode(GameMode.NON_STOP);
						mParentScene.clearChildScene();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		raceButton = new Sprite((CAMERA_WIDTH - BUTTON_WIDTH) / 2, (int) (nonStopButton.getY() + nonStopButton.getHeight()), SharedResources.getInstance().modeRegion[GameMode.RACE], activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						SetupScene.setGameMode(GameMode.RACE);
						mParentScene.clearChildScene();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		this.attachChild(titleSprite);
		this.attachChild(reflexButton);
		this.attachChild(nonStopButton);
		this.attachChild(raceButton);
		
	}
	
	@Override
	protected void registerTouchAreas()
	{
		registerTouchArea(reflexButton);
		registerTouchArea(nonStopButton);
		raceButton.setColor(.5f, .5f, .5f);
		//registerTouchArea(raceButton);
		
	}
	
}
