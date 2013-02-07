package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.SetupScene.GameMode;

public class MultiplayerModeSelectScene extends ReflexMenuScene
{
	ReflexActivity		activity;
	BitmapTextureAtlas	sceneAtlas;
	
	final Sprite		reflexButton;
	final Sprite		nonStopButton;
	final Sprite		raceButton;
	
	public MultiplayerModeSelectScene()
	{
		super();
		activity = ReflexActivity.getInstance();
		
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/MultiplayerModeSelectScene/");
		
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", 0, 0);
		final TextureRegion reflexButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "reflexButton.png", (int) titleRegion.getWidth(), 0);
		final TextureRegion nonStopButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "nonStopButton.png", (int) (reflexButtonRegion.getTextureX()), (int) (reflexButtonRegion.getHeight()));
		final TextureRegion raceButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "raceButton.png", (int) reflexButtonRegion.getTextureX(), (int) (nonStopButtonRegion.getTextureY() + nonStopButtonRegion.getHeight()));
		
		this.setBackgroundEnabled(false);
		sceneAtlas.load();
		
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		reflexButton = new Sprite(0, 210, reflexButtonRegion, activity.getVertexBufferObjectManager())
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
		nonStopButton = new Sprite(0, reflexButton.getY() + reflexButton.getHeight(), nonStopButtonRegion, activity.getVertexBufferObjectManager())
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
		
		raceButton = new Sprite(0, (int) (nonStopButton.getY() + nonStopButton.getHeight()), raceButtonRegion, activity.getVertexBufferObjectManager())
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
		raceButton.setColor(.5f,.5f,.5f);
		//registerTouchArea(raceButton);
		
	}
	
}
