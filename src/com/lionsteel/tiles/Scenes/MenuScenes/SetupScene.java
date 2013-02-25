package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.TilesSharedPreferenceStrings;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Entities.MusicMuteControl;
import com.lionsteel.tiles.Entities.SoundEffectMuteControl;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Entities.TilesetEntity;

public class SetupScene extends TilesMenuScene
{
	final TilesMainActivity				activity;
	final BuildableBitmapTextureAtlas	sceneAtlas;

	TilesMenuButton						tilesButton;
	final TilesMenuButton[]				difficultyButtons	= new TilesMenuButton[4];
	final TilesMenuButton[]				gameModeSprite		= new TilesMenuButton[6];
	final TilesMenuButton				playButton;

	final SoundEffectMuteControl		soundEffectMute;
	final MusicMuteControl				musicMute;

	final MultiplayerModeSelectScene	modeSelectScreen;
	final SkillSelectScene				skillSelectScene;
	final TilesetSelectScene			tilesetSelectScene;
	final PracticeModeSelectScene		practiceModeSelectScene;

	final Sprite						titleSprite;
	final Sprite						practiceTitleSprite;

	private static Tileset				currentTileset;

	private static SetupScene			instance;

	private static int					gameMode			= GameMode.REFLEX;
	private static int					difficulty;

	@Override
	public void logFlurryEvent()
	{
		if (getGameMode() < 3)
			FlurryAgent.logEvent(FlurryAgentEventStrings.MULTIPLAYER_SETUP);
		else
			FlurryAgent.logEvent(FlurryAgentEventStrings.PRACTICE_SETUP);

	};

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
		if (currentTileset.getBasePath().compareTo(tileset) == 0)
		{
			TilesMainActivity.getInstance().backToSetupScene();
			return;
		}
		TilesMainActivity.getInstance().load(new Runnable()
		{
			@Override
			public void run()
			{
				currentTileset.clearTileset();
				currentTileset = new Tileset(tileset, false);
				SetupScene.getInstance().resetGraphics();

				TilesMainActivity.getInstance().backToSetupScene();
				TilesMainActivity.getInstance().savePreference(TilesSharedPreferenceStrings.lastTileset, tileset);
			}
		});
	}

	public static void setGameMode(final int gameMode)
	{
		setGameMode(gameMode, false);
	}

	public static void setGameMode(final int gameMode, final boolean instant)
	{
		if (gameMode < 3)
		{
			instance.titleSprite.setVisible(true);
			instance.practiceTitleSprite.setVisible(false);
			TilesMainActivity.getInstance().saveInt(TilesSharedPreferenceStrings.lastVersusMode, gameMode);
		} else
		{
			instance.titleSprite.setVisible(false);
			instance.practiceTitleSprite.setVisible(true);
			TilesMainActivity.getInstance().saveInt(TilesSharedPreferenceStrings.lastPracticeMode, gameMode);
		}
		if (SetupScene.getGameMode() == gameMode)
			return;
		if (instant)
		{
			instance.removeButton(instance.gameModeSprite[SetupScene.gameMode]);
			instance.addButton(instance.gameModeSprite[gameMode]);
			instance.clearTouchAreas();
			instance.registerTouchAreas();
			instance.gameModeSprite[SetupScene.gameMode].setAlpha(0);
			instance.gameModeSprite[gameMode].setAlpha(1.0f);
		} else
		{
			final int currentGameMode = SetupScene.gameMode;
			instance.gameModeSprite[SetupScene.gameMode].registerEntityModifier(new SequenceEntityModifier(new DelayModifier(SCENE_TRANSITION_SECONDS * 2), new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 1.0f, 0))
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
		}
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
		TilesMainActivity.getInstance().saveInt(TilesSharedPreferenceStrings.lastDifficulty, difficulty);
	}

	public SetupScene()
	{
		super();
		instance = this;

		activity = TilesMainActivity.getInstance();
		this.setBackgroundEnabled(false);

		currentTileset = new Tileset(activity.sharedPrefs.getString(TilesSharedPreferenceStrings.lastTileset, Tileset.tilesetList[0]), false);

		modeSelectScreen = new MultiplayerModeSelectScene();
		skillSelectScene = new SkillSelectScene();
		tilesetSelectScene = new TilesetSelectScene();
		practiceModeSelectScene = new PracticeModeSelectScene();

		musicMute = new MusicMuteControl();
		soundEffectMute = new SoundEffectMuteControl();

		musicMute.setPosition(CAMERA_WIDTH - musicMute.getWidth() - BACK_ARROW_PADDING, BACK_ARROW_PADDING);
		soundEffectMute.setPosition(CAMERA_WIDTH - soundEffectMute.getWidth() - BACK_ARROW_PADDING, musicMute.getBottom());

		addButton(musicMute);
		addButton(soundEffectMute);

		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SetupScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png");
		final TextureRegion practiceTitleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "practiceTitle.png");
		final TextureRegion[] difficultyRegion = new TextureRegion[4];

		difficultyRegion[Difficulty.EASY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "easy.png");
		difficultyRegion[Difficulty.NORMAL] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "normal.png");
		difficultyRegion[Difficulty.HARD] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "hard.png");
		difficultyRegion[Difficulty.INSANE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "insane.png");

		final TextureRegion playRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "play.png");

		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		practiceTitleSprite = new Sprite(0, 0, practiceTitleRegion, activity.getVertexBufferObjectManager());

		final TilesetEntity tilesetEntity = currentTileset.getTilesetEntity();

		tilesButton = new TilesMenuButton(tilesetEntity.getButtonRegion(), new Runnable()
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
			difficultyButtons[x] = new TilesMenuButton(difficultyRegion[x], new Runnable()
			{
				@Override
				public void run()
				{
					transitionChildScene(skillSelectScene);
				}
			});
			difficultyButtons[x].center(tilesButton.getBottom());
		}

		for (int x = 0; x < 6; x++)
		{
			if (x < 3)
				gameModeSprite[x] = new TilesMenuButton(SharedResources.getInstance().modeRegion[x], new Runnable()
				{
					@Override
					public void run()
					{
						transitionChildScene(modeSelectScreen);
					}
				});
			else
				gameModeSprite[x] = new TilesMenuButton(SharedResources.getInstance().modeRegion[x], new Runnable()
				{
					@Override
					public void run()
					{
						transitionChildScene(practiceModeSelectScene);
					}
				});
			gameModeSprite[x].center(difficultyButtons[0].getBottom());
		}

		playButton = new TilesMenuButton(playRegion, new Runnable()
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
		attachChild(practiceTitleSprite);

		if (getGameMode() < 3)
			titleSprite.setVisible(false);
		else
			titleSprite.setVisible(true);

		for (int x = 0; x < 4; x++)
		{
			difficultyButtons[x].setAlpha(0);
		}
		difficulty = activity.sharedPrefs.getInt(TilesSharedPreferenceStrings.lastDifficulty, Difficulty.EASY);
		difficultyButtons[getDifficulty()].setAlpha(1.0f);
		addButton(difficultyButtons[getDifficulty()]);

		for (int x = 0; x < 6; x++)
		{
			gameModeSprite[x].setAlpha(0);
		}
		addButton(gameModeSprite[getGameMode()]);
		gameModeSprite[getGameMode()].setAlpha(1.0f);

		for (int x = 0; x < 4; x++)
			difficultyButtons[x].attachChild(currentTileset.getDifficultySprite(x));
		currentTileset.getDifficultySprite(SetupScene.getDifficulty()).fadeIn();

		setupLabels();

	}

	private void setupLabels()
	{

		final Text tilesetLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Tileset", activity.getVertexBufferObjectManager());
		final Text tilesetLabelShadow = new Text(0, 0, SharedResources.getInstance().mFont, "Tileset", activity.getVertexBufferObjectManager());
		tilesetLabelShadow.setColor(Color.BLACK);
		tilesetLabelShadow.setRotationCenter(0, 0);
		tilesetLabelShadow.setRotation(-90);
		tilesetLabel.setRotationCenter(0, 0);
		tilesetLabel.setRotation(-90);
		tilesetLabel.setPosition(tilesButton.getX() - tilesetLabel.getHeight() - 2, tilesButton.getY() + (tilesButton.getHeight() + tilesetLabel.getWidth()) / 2);
		tilesetLabelShadow.setPosition(tilesetLabel.getX() + 2, tilesetLabel.getY() - 2);
		attachChild(tilesetLabelShadow);
		attachChild(tilesetLabel);

		final Text difficultyLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Skill", activity.getVertexBufferObjectManager());
		final Text difficultyLabelShadow = new Text(0, 0, SharedResources.getInstance().mFont, "Skill", activity.getVertexBufferObjectManager());
		difficultyLabelShadow.setColor(Color.BLACK);
		difficultyLabelShadow.setRotationCenter(0, 0);
		difficultyLabelShadow.setRotation(-90);
		difficultyLabel.setRotationCenter(0, 0);
		difficultyLabel.setRotation(-90);
		difficultyLabel.setPosition(difficultyButtons[0].getX() - difficultyLabel.getHeight() - 2, difficultyButtons[0].getY() + (difficultyButtons[0].getHeight() + difficultyLabel.getWidth()) / 2);
		difficultyLabelShadow.setPosition(difficultyLabel.getX() + 2, difficultyLabel.getY() - 2);
		attachChild(difficultyLabelShadow);
		attachChild(difficultyLabel);

		final Text modeLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Mode", activity.getVertexBufferObjectManager());
		final Text modeLabelShadow = new Text(0, 0, SharedResources.getInstance().mFont, "Mode", activity.getVertexBufferObjectManager());
		modeLabelShadow.setColor(Color.BLACK);
		modeLabelShadow.setRotationCenter(0, 0);
		modeLabelShadow.setRotation(-90);
		modeLabel.setRotationCenter(0, 0);
		modeLabel.setRotation(-90);
		modeLabel.setPosition(gameModeSprite[0].getX() - modeLabel.getHeight() - 2, gameModeSprite[0].getY() + (gameModeSprite[0].getHeight() + modeLabel.getWidth()) / 2);
		modeLabelShadow.setPosition(modeLabel.getX() + 2, modeLabel.getY() - 2);
		attachChild(modeLabelShadow);
		attachChild(modeLabel);

	}

	public void resetGraphics()
	{
		skillSelectScene.resetGraphics();
		removeButton(tilesButton);
		final float oldY = tilesButton.getY();
		tilesButton = new TilesMenuButton(currentTileset.getTilesetEntity().getButtonRegion(), new Runnable()
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

	@Override
	public void initScene()
	{
		musicMute.refreshButton();
		soundEffectMute.refreshButton();
	}

}
