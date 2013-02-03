package com.lionsteel.reflexmulti;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.Scenes.MultiplayerModeSelectScene.GameMode;
import com.lionsteel.reflexmulti.Scenes.ReflexMenuScene;

public class SetupScene extends ReflexMenuScene
{
	final ReflexActivity		activity;
	final BitmapTextureAtlas	sceneAtlas;
	final Sprite				playSprite;
	
	private int					gameMode	= GameMode.ONE_TILE;
	
	public SetupScene()
	{
		activity = ReflexActivity.getInstance();
		this.setBackgroundEnabled(false);
		
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 2048);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SetupScene/");
		
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", (int) backgroundRegion.getWidth(), 0);
		final TextureRegion tilesRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "tiles.png", (int) titleRegion.getTextureX(), (int) titleRegion.getHeight());
		final TextureRegion difficultyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "difficulty.png", (int) tilesRegion.getTextureX(), (int) (tilesRegion.getTextureY() + tilesRegion.getHeight()));
		final TextureRegion playRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "play.png", (int) tilesRegion.getTextureX(), (int) (difficultyRegion.getTextureY() + difficultyRegion.getHeight()));
		
		sceneAtlas.load();
		
		final Sprite backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		final Sprite tilesSprite = new Sprite(0, 160, tilesRegion, activity.getVertexBufferObjectManager());
		final Sprite difficultySprite = new Sprite(0, 415, difficultyRegion, activity.getVertexBufferObjectManager());
		playSprite = new Sprite((CAMERA_WIDTH - playRegion.getWidth()) / 2, CAMERA_HEIGHT - playRegion.getHeight() - 10, playRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						activity.startGame(gameMode);
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		attachChild(backgroundSprite);
		attachChild(titleSprite);
		attachChild(tilesSprite);
		attachChild(difficultySprite);
		attachChild(playSprite);
		
	}
	
	@Override
	protected void registerTouchAreas()
	{
		registerTouchArea(playSprite);
		
	}
	
	@Override
	protected void deregisterTouchAreas()
	{
		unregisterTouchArea(playSprite);
		
	}
	
	public void setMode(final int mode)
	{
		gameMode = mode;
	}
}
