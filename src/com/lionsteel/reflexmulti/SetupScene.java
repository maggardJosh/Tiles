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

import com.lionsteel.reflexmulti.Entities.Tileset;
import com.lionsteel.reflexmulti.Entities.TilesetEntity;
import com.lionsteel.reflexmulti.Scenes.MultiplayerModeSelectScene;
import com.lionsteel.reflexmulti.Scenes.ReflexMenuScene;
import com.lionsteel.reflexmulti.Scenes.SkillSelectScene;
import com.lionsteel.reflexmulti.Scenes.TilesetSelectScene;

public class SetupScene extends ReflexMenuScene
{
	final ReflexActivity				activity;
	final BitmapTextureAtlas			sceneAtlas;
	
	Sprite								tilesSprite;
	final Sprite[]						difficultySprite	= new Sprite[4];
	final Sprite[]						gameModeSprite		= new Sprite[3];
	final Sprite						playSprite;
	
	final MultiplayerModeSelectScene	modeSelectScreen;
	final SkillSelectScene				skillSelectScene;
	final TilesetSelectScene			tilesetSelectScene;
	
	private static Tileset				currentTileset;
	
	private static SetupScene			instance;
	
	private static int					gameMode			= GameMode.REFLEX;
	private static int					difficulty			= Difficulty.NORMAL;
	
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
	
	public static Tileset getTileset()
	{
		return currentTileset;
	}
	
	public static void loadTileset(final String tileset)
	{
		if (currentTileset.getBasePath() == tileset)
		{
			ReflexActivity.getInstance().backToSetupScene();
			return;
		}
		ReflexActivity.getInstance().load(new Runnable()
		{
			@Override
			public void run()
			{
				currentTileset.clearTileset();
				currentTileset = new Tileset(tileset, false);
				SetupScene.getInstance().resetGraphics();
				
				ReflexActivity.getInstance().backToSetupScene();
				
			}
		});
	}
	
	public static void setGameMode(final int gameMode)
	{
		if (SetupScene.getGameMode() == gameMode)
			return;
		instance.gameModeSprite[SetupScene.gameMode].registerEntityModifier(new SequenceEntityModifier(new DelayModifier(SCENE_TRANSITION_SECONDS), new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 1.0f, 0))
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				instance.gameModeSprite[gameMode].registerEntityModifier(new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 0, 1.0f));
				super.onModifierFinished(pItem);
			}
		});
		SetupScene.gameMode = gameMode;
	}
	
	public static void setDifficulty(final int difficulty)
	{
		
		if (SetupScene.getDifficulty() == difficulty)
			return;
		final int currentDifficulty = SetupScene.difficulty;
		
		instance.difficultySprite[currentDifficulty].registerEntityModifier(new SequenceEntityModifier(new DelayModifier(SCENE_TRANSITION_SECONDS), new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 1.0f, 0)
		{
			protected void onModifierStarted(IEntity pItem)
			{
				currentTileset.getDifficultySprite(currentDifficulty).fadeOut();
			};
		})
		{
			
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				currentTileset.getDifficultySprite(difficulty).fadeIn();
				instance.difficultySprite[difficulty].registerEntityModifier(new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 0, 1.0f));
				super.onModifierFinished(pItem);
			}
		});
		SetupScene.difficulty = difficulty;
	}
	
	public SetupScene()
	{
		super();
		instance = this;
		
		activity = ReflexActivity.getInstance();
		this.setBackgroundEnabled(false);
		
		currentTileset = new Tileset("three", false);
		
		modeSelectScreen = new MultiplayerModeSelectScene();
		skillSelectScene = new SkillSelectScene();
		tilesetSelectScene = new TilesetSelectScene();
		
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SetupScene/");
		
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", 0, 0);
		final TextureRegion[] difficultyRegion = new TextureRegion[4];
		
		difficultyRegion[Difficulty.EASY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "easy.png", (int) titleRegion.getTextureX(), (int) (titleRegion.getTextureY() + titleRegion.getHeight()));
		difficultyRegion[Difficulty.NORMAL] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "normal.png", (int) titleRegion.getTextureX(), (int) (difficultyRegion[0].getTextureY() + difficultyRegion[0].getHeight()));
		difficultyRegion[Difficulty.HARD] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "hard.png", (int) titleRegion.getTextureX(), (int) (difficultyRegion[1].getTextureY() + difficultyRegion[1].getHeight()));
		difficultyRegion[Difficulty.INSANE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "insane.png", (int) titleRegion.getTextureX(), (int) (difficultyRegion[2].getTextureY() + difficultyRegion[2].getHeight()));
		
		final TextureRegion playRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "play.png", (int) titleRegion.getWidth(), 0);
		
		sceneAtlas.load();
		
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		
		final TilesetEntity tilesetEntity = currentTileset.getTilesetEntity();
		tilesetEntity.setAction(new Runnable()
		{
			
			@Override
			public void run()
			{
				transitionChildScene(tilesetSelectScene);
				
			}
		});
		tilesSprite = tilesetEntity.getButtonSprite();
		tilesSprite.setPosition((CAMERA_WIDTH - tilesSprite.getWidth()) / 2, titleSprite.getY() + titleSprite.getHeight());
		
		for (int x = 0; x < 4; x++)
			difficultySprite[x] = new Sprite((CAMERA_WIDTH - difficultyRegion[x].getWidth()) / 2, tilesSprite.getY() + tilesSprite.getHeight(), difficultyRegion[x], activity.getVertexBufferObjectManager())
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					switch (pSceneTouchEvent.getAction())
					{
						case TouchEvent.ACTION_UP:
							transitionChildScene(skillSelectScene);
							break;
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
		for (int x = 0; x < 3; x++)
			gameModeSprite[x] = new Sprite((CAMERA_WIDTH - SharedResources.getInstance().modeRegion[x].getWidth()) / 2, difficultySprite[0].getY() + difficultySprite[0].getHeight(), SharedResources.getInstance().modeRegion[x], activity.getVertexBufferObjectManager())
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
		
		attachChild(titleSprite);
		attachChild(tilesSprite);
		for (int x = 0; x < 4; x++)
		{
			attachChild(difficultySprite[x]);
			difficultySprite[x].setAlpha(0);
		}
		difficultySprite[getDifficulty()].setAlpha(1.0f);
		
		for (int x = 0; x < 3; x++)
		{
			attachChild(gameModeSprite[x]);
			gameModeSprite[x].setAlpha(0);
		}
		gameModeSprite[getGameMode()].setAlpha(1.0f);
		
		attachChild(playSprite);
		
		for (int x = 0; x < 4; x++)
			difficultySprite[x].attachChild(currentTileset.getDifficultySprite(x));
		currentTileset.getDifficultySprite(SetupScene.getDifficulty()).fadeIn();
		
	}
	
	public void resetGraphics()
	{
		skillSelectScene.resetGraphics();
		tilesSprite.detachSelf();
		final float oldY = tilesSprite.getY();
		final TilesetEntity tilesetEntity = currentTileset.getTilesetEntity();
		tilesetEntity.setAction(new Runnable()
		{
			
			@Override
			public void run()
			{
				transitionChildScene(tilesetSelectScene);
				
			}
		});
		tilesSprite = tilesetEntity.getButtonSprite();
		tilesSprite.setPosition((CAMERA_WIDTH - tilesSprite.getWidth()) / 2, oldY);
		tilesSprite.setZIndex(FOREGROUND_Z);
		this.attachChild(tilesSprite);
		
		for (int x = 0; x < 4; x++)
		{
			difficultySprite[x].detachChildren();
			difficultySprite[x].attachChild(currentTileset.getDifficultySprite(x));
			
		}
		currentTileset.getDifficultySprite(SetupScene.getDifficulty()).fadeIn();
		this.sortChildren();
	}
	
	@Override
	protected void registerTouchAreas()
	{
		registerTouchArea(playSprite);
		registerTouchArea(tilesSprite);
		for (int x = 0; x < 3; x++)
			registerTouchArea(gameModeSprite[x]);
		for (int x = 0; x < 4; x++)
			registerTouchArea(difficultySprite[x]);
		
	}
	
	public class GameMode
	{
		public static final int	REFLEX		= 0;
		public static final int	NON_STOP	= REFLEX + 1;
		public static final int	RACE		= NON_STOP + 1;
	}
	
	public class Difficulty
	{
		public static final int	EASY	= 0;
		public static final int	NORMAL	= EASY + 1;
		public static final int	HARD	= NORMAL + 1;
		public static final int	INSANE	= HARD + 1;
	}
}
