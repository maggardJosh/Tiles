package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.app.ProgressDialog;

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
	final Sprite						tilesetLabelSprite;
	final Sprite						skillLabelSprite;
	final Sprite						modeLabelSprite;

	final int							TITLE_Y				= 50;
	final int							BUTTON_PADDING		= 20;

	private static Tileset				currentTileset;

	private static SetupScene			instance;

	private static int					gameMode			= GameMode.REFLEX;
	private static int					difficulty;
	private static boolean				isCreated			= false;

	private static Object				instanceLock;

	@Override
	public void logFlurryEvent()
	{
		if (getGameMode() < 3)
			FlurryAgent.logEvent(FlurryAgentEventStrings.MULTIPLAYER_SETUP);
		else
			FlurryAgent.logEvent(FlurryAgentEventStrings.PRACTICE_SETUP);

	};

	public static void clear()
	{
		instance = null;
	}

	public static boolean isNull()
	{
		return instance == null;
	}

	public static SetupScene getInstance()
	{
		if (instanceLock == null)
			instanceLock = new Object();
		synchronized (instanceLock)
		{
			if (instance == null)
				instance = new SetupScene();
			return instance;
		}
	}

	public boolean isCreated()
	{
		return isCreated;
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

		TilesMainActivity.getInstance().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				final ProgressDialog pd = TilesMainActivity.getInstance().progressDialog;
				pd.setMessage("Loading Tileset");
				pd.show();

				TilesMainActivity.getInstance().runOnUpdateThread(new Runnable()
				{

					@Override
					public void run()
					{
						currentTileset.clearTileset();
						currentTileset = new Tileset(tileset, false);
						SetupScene.getInstance().resetGraphics();

						TilesMainActivity.getInstance().backToSetupScene();
						TilesMainActivity.getInstance().savePreference(TilesSharedPreferenceStrings.lastTileset, tileset);

						instance.sortChildren();

						TilesMainActivity.getInstance().getEngine().registerUpdateHandler(new TimerHandler(.3f, new ITimerCallback()
						{

							@Override
							public void onTimePassed(TimerHandler pTimerHandler)
							{
								TilesMainActivity.getInstance().progressDialog.dismiss();
							}
						}));
					}
				});

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
			instance.sortChildren();
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
					instance.sortChildren();
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
				instance.sortChildren();
				super.onModifierFinished(pItem);
			}
		});
		SetupScene.difficulty = difficulty;
		TilesMainActivity.getInstance().saveInt(TilesSharedPreferenceStrings.lastDifficulty, difficulty);
	}

	private SetupScene()
	{
		super();
		instance = this;

		activity = TilesMainActivity.getInstance();
		this.setBackgroundEnabled(false);

		currentTileset = new Tileset(activity.sharedPrefs.getString(TilesSharedPreferenceStrings.lastTileset, Tileset.tilesetList[0]), false);

		modeSelectScreen = new MultiplayerModeSelectScene();
		skillSelectScene = new SkillSelectScene();
		tilesetSelectScene = TilesetSelectScene.getInstance();
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

		final TextureRegion playRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "play.png");
		final TextureRegion changeRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "changeButton.png");
		final TextureRegion tilesetLabelRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "tilesetLabel.png");
		final TextureRegion skillLabelRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "skillLabel.png");
		final TextureRegion modeLabelRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "modeLabel.png");

		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		titleSprite = new Sprite((CAMERA_WIDTH - titleRegion.getWidth()) / 2, TITLE_Y, titleRegion, activity.getVertexBufferObjectManager());
		practiceTitleSprite = new Sprite((CAMERA_WIDTH - practiceTitleRegion.getWidth()) / 2, TITLE_Y, practiceTitleRegion, activity.getVertexBufferObjectManager());

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
			gameModeSprite[x].center(titleSprite.getY() + titleSprite.getHeight() + BUTTON_PADDING);
		}

		final TilesetEntity tilesetEntity = currentTileset.getTilesetEntity();

		tilesButton = new TilesMenuButton(tilesetEntity.getButtonRegion(), new Runnable()
		{
			@Override
			public void run()
			{
				transitionChildScene(tilesetSelectScene);
			}
		});
		tilesButton.center(gameModeSprite[0].getBottom() + BUTTON_PADDING);
		tilesButton.attachChild(tilesetEntity.getButtonEntity());
		addButton(tilesButton);

		for (int x = 0; x < 4; x++)
		{
			difficultyButtons[x] = new TilesMenuButton(SharedResources.getInstance().difficultyRegion[x], new Runnable()
			{
				@Override
				public void run()
				{
					transitionChildScene(skillSelectScene);
				}
			});
			difficultyButtons[x].center(tilesButton.getBottom() + BUTTON_PADDING);
		}

		playButton = new TilesMenuButton(playRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.startGame();
			}
		});
		playButton.center(difficultyButtons[0].getBottom() + BUTTON_PADDING);
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

		tilesetLabelSprite = new Sprite(0,0,tilesetLabelRegion, activity.getVertexBufferObjectManager());
		skillLabelSprite = new Sprite(0,0,skillLabelRegion, activity.getVertexBufferObjectManager());
		modeLabelSprite = new Sprite(0,0,modeLabelRegion, activity.getVertexBufferObjectManager());
		
		positionLabels();
		setupChangeSprites(changeRegion);

		isCreated = true;

	}

	private void positionLabels()
	{
		final int LABEL_X_PADDING = 12;
		final int LABEL_PADDING = 12;

		tilesetLabelSprite.setPosition(tilesButton.getX() + LABEL_X_PADDING, tilesButton.getY() - LABEL_PADDING);
		tilesetLabelSprite.setZIndex(FOREGROUND_Z);
		attachChild(tilesetLabelSprite);

		skillLabelSprite.setPosition(difficultyButtons[0].getX() + LABEL_X_PADDING, difficultyButtons[0].getY() - LABEL_PADDING);
		skillLabelSprite.setZIndex(FOREGROUND_Z);
		attachChild(skillLabelSprite);

		modeLabelSprite.setPosition(gameModeSprite[0].getX() + LABEL_X_PADDING, gameModeSprite[0].getY() - LABEL_PADDING);
		modeLabelSprite.setZIndex(FOREGROUND_Z);
		attachChild(modeLabelSprite);

	}

	private void setupChangeSprites(final ITextureRegion changeRegion)
	{
		final Sprite[] changeSprites = new Sprite[3];
		for (int x = 0; x < 3; x++)
		{
			changeSprites[x] = new Sprite(0, 0, changeRegion, activity.getVertexBufferObjectManager());
			final TilesMenuButton button;
			switch (x)
			{
			case 0:
				button = difficultyButtons[0];
				break;
			case 1:
				button = tilesButton;
				break;
			case 2:
			default:
				button = gameModeSprite[0];
				break;
			}
			changeSprites[x].setPosition(button.getX() + button.getWidth() - changeSprites[0].getWidth(), button.getY());// + (button.getHeight() - changeSprites[0].getHeight()) / 2);
			changeSprites[x].setZIndex(FOREGROUND_Z);
			this.attachChild(changeSprites[x]);
		}

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
		tilesButton.setZIndex(FOREGROUND_Z - 1);
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

	@Override
	protected void exitScene()
	{
		// TODO Auto-generated method stub

	}

}
