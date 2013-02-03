package com.lionsteel.reflexmulti;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.SequenceModifier;

import com.lionsteel.reflexmulti.Scenes.MultiplayerModeSelectScene;
import com.lionsteel.reflexmulti.Scenes.ReflexMenuScene;

public class SetupScene extends ReflexMenuScene
{
	final ReflexActivity				activity;
	final BitmapTextureAtlas			sceneAtlas;
	
	final Sprite						tilesSprite;
	final Sprite						difficultySprite;
	final Sprite[]						gameModeSprite	= new Sprite[3];
	final Sprite						playSprite;
	
	final MultiplayerModeSelectScene	modeSelectScreen;
	
	private static SetupScene			instance;
	
	private static int					gameMode		= GameMode.ONE_TILE;
	private static int					difficulty		= Difficulty.NORMAL;
	
	public static SetupScene getInstance()
	{
		if (instance == null)
			instance = new SetupScene();
		return instance;
	}
	
	public static int getGameMode()
	{
		return gameMode;
	}
	
	public static int getDifficulty()
	{
		return difficulty;
	}
	
	public static void setGameMode(final int gameMode)
	{
		if(SetupScene.getGameMode()==gameMode)
			return;
		instance.gameModeSprite[SetupScene.gameMode].registerEntityModifier(new SequenceEntityModifier(new DelayModifier(SCENE_TRANSITION_SECONDS), new AlphaModifier(SCENE_TRANSITION_SECONDS, 1.0f, 0)){
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				instance.gameModeSprite[gameMode].registerEntityModifier(new AlphaModifier(SCENE_TRANSITION_SECONDS, 0, 1.0f));
				super.onModifierFinished(pItem);
			}
		});
		SetupScene.gameMode = gameMode;
	}
	
	public static void setDifficulty(final int difficulty)
	{
		SetupScene.difficulty = difficulty;
	}
	
	public SetupScene()
	{
		instance = this;
		activity = ReflexActivity.getInstance();
		this.setBackgroundEnabled(false);
		
		modeSelectScreen = new MultiplayerModeSelectScene();
		
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 2048);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SetupScene/");
		
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", (int) backgroundRegion.getWidth(), 0);
		final TextureRegion tilesRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "tiles.png", (int) titleRegion.getTextureX(), (int) titleRegion.getHeight());
		final TextureRegion difficultyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "difficulty.png", (int) tilesRegion.getTextureX(), (int) (tilesRegion.getTextureY() + tilesRegion.getHeight()));
		final TextureRegion playRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "play.png", (int) tilesRegion.getTextureX(), (int) (difficultyRegion.getTextureY() + difficultyRegion.getHeight()));
		
		final TextureRegion[] modeRegion = new TextureRegion[3];
		modeRegion[GameMode.ONE_TILE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "oneTile.png", (int) tilesRegion.getTextureX(), (int) (playRegion.getTextureY() + playRegion.getHeight()));
		modeRegion[GameMode.THREE_TILE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "threeTiles.png", (int) tilesRegion.getTextureX(), (int) (modeRegion[GameMode.ONE_TILE].getTextureY() + modeRegion[GameMode.ONE_TILE].getHeight()));
		modeRegion[GameMode.STREAM] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "stream.png", (int) tilesRegion.getTextureX(), (int) (modeRegion[GameMode.THREE_TILE].getTextureY() + modeRegion[GameMode.THREE_TILE].getHeight()));
		
		sceneAtlas.load();
		
		final Sprite backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		tilesSprite = new Sprite((CAMERA_WIDTH - tilesRegion.getWidth()) / 2, titleSprite.getY() + titleSprite.getHeight(), tilesRegion, activity.getVertexBufferObjectManager());
		difficultySprite = new Sprite((CAMERA_WIDTH - difficultyRegion.getWidth()) / 2, tilesSprite.getY() + tilesSprite.getHeight(), difficultyRegion, activity.getVertexBufferObjectManager());
		for (int x = 0; x < 3; x++)
			gameModeSprite[x] = new Sprite((CAMERA_WIDTH - modeRegion[x].getWidth()) / 2, difficultySprite.getY() + difficultySprite.getHeight(), modeRegion[x], activity.getVertexBufferObjectManager())
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					switch (pSceneTouchEvent.getAction())
					{
						case TouchEvent.ACTION_UP:
							transitionChildScene(modeSelectScreen);
							break;
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
		playSprite = new Sprite((CAMERA_WIDTH - playRegion.getWidth()) / 2, CAMERA_HEIGHT - playRegion.getHeight(), playRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						activity.startGame();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		attachChild(backgroundSprite);
		attachChild(titleSprite);
		attachChild(tilesSprite);
		attachChild(difficultySprite);
		
		for (int x = 0; x < 3; x++)
		{
			attachChild(gameModeSprite[x]);
			gameModeSprite[x].setAlpha(0);
		}
		gameModeSprite[getGameMode()].setAlpha(1.0f);
		
		attachChild(playSprite);
		
	}
	
	@Override
	protected void registerTouchAreas()
	{
		registerTouchArea(playSprite);
		for (int x = 0; x < 3; x++)
			registerTouchArea(gameModeSprite[x]);
		
	}
	
	@Override
	protected void deregisterTouchAreas()
	{
		unregisterTouchArea(playSprite);
		for (int x = 0; x < 3; x++)
			unregisterTouchArea(gameModeSprite[x]);
	}
	
	public class GameMode
	{
		public static final int	ONE_TILE	= 0;
		public static final int	THREE_TILE	= ONE_TILE + 1;
		public static final int	STREAM		= THREE_TILE + 1;
	}
	
	public class Difficulty
	{
		public static final int	EASY	= 0;
		public static final int	NORMAL	= EASY + 1;
		public static final int	HARD	= NORMAL + 1;
	}
}
