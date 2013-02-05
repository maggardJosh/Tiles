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
	
	final Sprite		oneTileButton;
	final Sprite		streamTileButton;
	
	public MultiplayerModeSelectScene()
	{
		super();
		activity = ReflexActivity.getInstance();
		
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/MultiplayerModeSelectScene/");
		
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", 0, 0);
		final TextureRegion oneTileButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "oneTileButton.png", (int) titleRegion.getWidth(), 0);
		final TextureRegion threeTileButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "threeTilesButton.png", (int) (oneTileButtonRegion.getTextureX()), (int) (oneTileButtonRegion.getHeight()));
		final TextureRegion streamButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "streamButton.png", (int)oneTileButtonRegion.getTextureX(), (int)(threeTileButtonRegion.getTextureY()+threeTileButtonRegion.getHeight()));
		
		
		this.setBackgroundEnabled(false);
		sceneAtlas.load();
		
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		oneTileButton = new Sprite(0, 210, oneTileButtonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						SetupScene.setGameMode(GameMode.ONE_TILE);
						mParentScene.clearChildScene();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final Sprite threeTileButton = new Sprite(0, oneTileButton.getY() + oneTileButton.getHeight(), threeTileButtonRegion, activity.getVertexBufferObjectManager());
		threeTileButton.setAlpha(.5f);
		streamTileButton = new Sprite(0, (int)(threeTileButton.getY() + threeTileButton.getHeight()), streamButtonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						SetupScene.setGameMode(GameMode.STREAM);
						mParentScene.clearChildScene();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		this.attachChild(titleSprite);
		this.attachChild(oneTileButton);
		this.attachChild(threeTileButton);
		this.attachChild(streamTileButton);
		
	}
	
	@Override
	protected void registerTouchAreas()
	{
		registerTouchArea(oneTileButton);
		registerTouchArea(streamTileButton);
		
	}
	
}
