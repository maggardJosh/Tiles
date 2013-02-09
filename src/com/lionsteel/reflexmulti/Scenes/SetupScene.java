package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SharedResources;
import com.lionsteel.reflexmulti.Entities.Tileset;
import com.lionsteel.reflexmulti.Entities.TilesetEntity;

public class SetupScene extends ReflexMenuScene
{
	final ReflexActivity				activity;
	final BitmapTextureAtlas			sceneAtlas;

	//	Sprite								tilesSprite;
	ReflexMenuButton					tilesButton;
	final ReflexMenuButton[]			difficultyButtons	= new ReflexMenuButton[4];
	final ReflexMenuButton[]			gameModeSprite		= new ReflexMenuButton[3];
	final ReflexMenuButton				playButton;

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
		final int currentGameMode = SetupScene.gameMode;
		instance.gameModeSprite[SetupScene.gameMode].registerEntityModifier(new SequenceEntityModifier(new DelayModifier(SCENE_TRANSITION_SECONDS*2), new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 1.0f, 0))
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				instance.removeButton(instance.gameModeSprite[currentGameMode]);
				instance.addButton(instance.gameModeSprite[gameMode]);
				instance.clearTouchAreas();
				instance.registerTouchAreas();

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

		instance.difficultyButtons[currentDifficulty].registerEntityModifier(new SequenceEntityModifier(new DelayModifier(SCENE_TRANSITION_SECONDS * 2), new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 1.0f, 0)
		{
			protected void onModifierStarted(IEntity pItem)
			{
				currentTileset.getDifficultySprite(currentDifficulty).fadeOut();
				super.onModifierStarted(pItem);
			};
		})
		{

			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				instance.removeButton(instance.difficultyButtons[currentDifficulty]);
				instance.addButton(instance.difficultyButtons[difficulty]);
				instance.clearTouchAreas();
				instance.registerTouchAreas();

				currentTileset.getDifficultySprite(difficulty).fadeIn();
				instance.difficultyButtons[difficulty].registerEntityModifier(new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 0, 1.0f));
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

		tilesButton = new ReflexMenuButton(tilesetEntity.getButtonRegion(), new Runnable()
		{
			@Override
			public void run()
			{
				transitionChildScene(tilesetSelectScene);
			}
		});
		tilesButton.center(titleSprite.getY() + titleSprite.getHeight());
		tilesButton.attachChild(tilesetEntity.getButtonEntity());
		addButton(tilesButton);

		for (int x = 0; x < 4; x++)
		{
			difficultyButtons[x] = new ReflexMenuButton(difficultyRegion[x], new Runnable()
			{
				@Override
				public void run()
				{
					transitionChildScene(skillSelectScene);
				}
			});
			difficultyButtons[x].center(tilesButton.getBottom());
		}

		for (int x = 0; x < 3; x++)
		{
			gameModeSprite[x] = new ReflexMenuButton(SharedResources.getInstance().modeRegion[x], new Runnable()
			{
				@Override
				public void run()
				{
					transitionChildScene(modeSelectScreen);
				}
			});
			gameModeSprite[x].center(difficultyButtons[0].getBottom());
		}
		playButton = new ReflexMenuButton(playRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.startGame();
			}
		});
		playButton.center(CAMERA_HEIGHT - playRegion.getHeight());
		addButton(playButton);

		attachChild(titleSprite);
		for (int x = 0; x < 4; x++)
		{
			difficultyButtons[x].setAlpha(0);
		}
		difficultyButtons[getDifficulty()].setAlpha(1.0f);
		addButton(difficultyButtons[getDifficulty()]);

		for (int x = 0; x < 3; x++)
		{
			gameModeSprite[x].setAlpha(0);
		}
		addButton(gameModeSprite[getGameMode()]);
		gameModeSprite[getGameMode()].setAlpha(1.0f);

		for (int x = 0; x < 4; x++)
			difficultyButtons[x].attachChild(currentTileset.getDifficultySprite(x));
		currentTileset.getDifficultySprite(SetupScene.getDifficulty()).fadeIn();

	}

	public void resetGraphics()
	{
		skillSelectScene.resetGraphics();
		removeButton(tilesButton);
		final float oldY = tilesButton.getY();
		tilesButton = new ReflexMenuButton(currentTileset.getTilesetEntity().getButtonRegion(), new Runnable()
		{
			@Override
			public void run()
			{
				transitionChildScene(tilesetSelectScene);
			}
		});
		tilesButton.attachChild(currentTileset.getTilesetEntity().getButtonEntity());
		tilesButton.setZIndex(FOREGROUND_Z);
		tilesButton.center(oldY);
		addButton(tilesButton);

		for (int x = 0; x < 4; x++)
		{
			difficultyButtons[x].clearButtonChildren();
			difficultyButtons[x].attachChild(currentTileset.getDifficultySprite(x));

		}
		currentTileset.getDifficultySprite(SetupScene.getDifficulty()).fadeIn();
		this.sortChildren();

		this.clearTouchAreas();
		this.registerTouchAreas();
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
