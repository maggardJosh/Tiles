package com.lionsteel.tiles.BaseClasses;

import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
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
import com.lionsteel.tiles.SongManager;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.GameButton;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Entities.WrongSelectionIndicator;
import com.lionsteel.tiles.Scenes.GameScenes.GameCountdown;
import com.lionsteel.tiles.Scenes.GameScenes.GameOverScreen;
import com.lionsteel.tiles.Scenes.GameScenes.PauseScene;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public abstract class GameScene extends Scene implements TilesConstants
{
	protected TilesMainActivity			activity;
	private boolean						playerOneDisabled		= false;
	private boolean						playerTwoDisabled		= false;

	protected Tileset					currentTileset;

	final BuildableBitmapTextureAtlas	sceneAtlas;
	protected final Sprite				playerOneIntro;
	protected final Sprite				playerTwoIntro;
	protected final Sprite[]			playerTutorials			= new Sprite[2];
	private boolean[]					playerInTutorial		= new boolean[2];
	protected final Sprite				barSprite;

	private final TouchControl[]		introTouchControls		= new TouchControl[2];
	final TilesMenuButton[]				tutorialButton			= new TilesMenuButton[2];

	protected boolean					playerOneReady			= false;
	protected boolean					playerTwoReady			= false;

	protected GameCountdown				gameCountdown;
	private WrongSelectionIndicator[]	errorIndicators			= new WrongSelectionIndicator[2];
	protected GameOverScreen			gameOverScreen;
	protected PauseScene				pauseScene;

	private final int[]					tilesCollected			= new int[2];
	private final int[]					maxStreak				= new int[2];
	private final int[]					currentStreak			= new int[2];

	protected TilesMenuButton			pauseButton;
	final TilesMenuButton[]				tutorialExitButton		= new TilesMenuButton[2];

	protected int						gameState				= GameState.INTRO;
	protected float						secondsOnCurrentState	= 0;

	public static boolean				isGameEventStarted		= false;

	protected Random					rand					= new Random();

	protected float						barSpeedMulti			= 1.0f;

	public abstract void buttonPressed(GameButton button);

	protected abstract void resetGame();

	public int getTilesCollected(final int player)
	{
		return tilesCollected[player];
	}

	public GameScene()
	{

		activity = TilesMainActivity.getInstance();
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);

		currentTileset = SetupScene.getTileset();
		currentTileset.setParent(this);

		currentTileset.setupScene();

		gameCountdown = new GameCountdown(this);

		gameOverScreen = new GameOverScreen();
		gameOverScreen.setLabels("Tiles", "Streak");
		pauseScene = new PauseScene();

		pauseButton = new TilesMenuButton(SharedResources.getInstance().pauseButtonRegion, new Runnable()
		{

			@Override
			public void run()
			{
				SharedResources.getInstance().pauseSound.setRate(1.0f);
				SharedResources.getInstance().pauseSound.play();
				transitionChildScene(pauseScene);
			}
		});
		this.setOnAreaTouchTraversalFrontToBack();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		final TextureRegion barRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "bar.png");
		final TextureRegion playerOneIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerOneIntro.png");
		final TextureRegion playerTwoIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerTwoIntro.png");
		final TextureRegion tutorialRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, GameMode.getName(SetupScene.getGameMode()) + "Tutorial.png");
		final TextureRegion tutorialButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "tutorialButton.png");
		final TextureRegion tutorialExitButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "tutorialExitButton.png");
		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		barSprite = new Sprite((CAMERA_WIDTH - barRegion.getWidth() * 1.5f), (CAMERA_HEIGHT - barRegion.getHeight()) / 2, barRegion, activity.getVertexBufferObjectManager());
		barSprite.setZIndex(FOREGROUND_Z);

		playerOneIntro = new Sprite((CAMERA_WIDTH - playerOneIntroRegion.getWidth()) / 2, CAMERA_HEIGHT - playerTwoIntroRegion.getHeight(), playerOneIntroRegion, activity.getVertexBufferObjectManager());
		playerOneIntro.setZIndex(FOREGROUND_Z);

		playerTwoIntro = new Sprite((CAMERA_WIDTH - playerTwoIntroRegion.getWidth()) / 2, 0, playerTwoIntroRegion, activity.getVertexBufferObjectManager());
		playerTwoIntro.setRotation(180);
		playerTwoIntro.setZIndex(FOREGROUND_Z);

		final int TUTORIAL_BUTTON_PADDING = 30;

		for (int x = 0; x < 2; x++)
		{
			final int playerIndex = x;
			tutorialButton[x] = new TilesMenuButton(tutorialButtonRegion, new Runnable()
			{

				@Override
				public void run()
				{
					moveTutorialIn(playerIndex);
				}
			});
			tutorialButton[x].registerOwnTouchArea(this);
			final int BUTTON_Y_SPACING = 20;
			if (x == PLAYER_ONE)
			{
				playerOneIntro.attachChild(tutorialButton[x]);
				tutorialButton[x].setPosition(playerOneIntro.getWidth() - tutorialButton[x].getWidth() - TUTORIAL_BUTTON_PADDING, (playerOneIntro.getHeight() - tutorialButton[x].getHeight()) / 2);
				final Text helpText = new Text(0, 0, SharedResources.getInstance().mFont, "Help", activity.getVertexBufferObjectManager());
				helpText.setPosition(tutorialButton[x].getX()+(tutorialButton[x].getWidth()-helpText.getWidth())/2, tutorialButton[x].getY() - BUTTON_Y_SPACING);
				playerOneIntro.attachChild(helpText);
			} else
			{
				playerTwoIntro.attachChild(tutorialButton[x]);
				tutorialButton[x].setPosition(playerTwoIntro.getWidth() - tutorialButton[x].getWidth() - TUTORIAL_BUTTON_PADDING, (playerTwoIntro.getHeight() - tutorialButton[x].getHeight()) / 2);
				final Text helpText = new Text(0, 0, SharedResources.getInstance().mFont, "Help", activity.getVertexBufferObjectManager());
				helpText.setPosition(tutorialButton[x].getX()+(tutorialButton[x].getWidth()-helpText.getWidth())/2, tutorialButton[x].getY() - BUTTON_Y_SPACING);
				playerTwoIntro.attachChild(helpText);
			}
		}
		
		final int TUTORIAL_EXIT_Y_PADDING = 20;
		final int TUTORIAL_EXIT_X_PADDING = 80;
		for (int x = 0; x < 2; x++)
		{
			playerTutorials[x] = new Sprite((CAMERA_WIDTH - tutorialRegion.getWidth()) / 2, 0, tutorialRegion, activity.getVertexBufferObjectManager());
			final int playerIndex = x;
			tutorialExitButton[x] = new TilesMenuButton(tutorialExitButtonRegion, new Runnable()
			{

				@Override
				public void run()
				{
					moveTutorialOut(playerIndex);
				}
			});
			playerTutorials[x].attachChild(tutorialExitButton[x]);
			tutorialExitButton[x].setPosition((playerTutorials[x].getWidth() - tutorialExitButton[x].getWidth())/2 + TUTORIAL_EXIT_X_PADDING, playerTutorials[x].getHeight() - tutorialExitButton[x].getHeight() - TUTORIAL_EXIT_Y_PADDING);
			playerTutorials[x].setZIndex(FOREGROUND_Z + 1);
		}
		playerTutorials[PLAYER_TWO].setRotation(180);
		playerTutorials[PLAYER_TWO].setY(-playerTutorials[PLAYER_TWO].getHeight());
		playerTutorials[PLAYER_ONE].setY(CAMERA_HEIGHT);
		

		prepareTouchControls();

		this.attachChild(barSprite);
		barSprite.setAlpha(0);

		this.attachChild(playerOneIntro);
		this.attachChild(playerTwoIntro);

		this.attachChild(playerTutorials[PLAYER_ONE]);
		this.attachChild(playerTutorials[PLAYER_TWO]);

		for (int i = 0; i < 2; i++)
		{
			errorIndicators[i] = new WrongSelectionIndicator(i);
			errorIndicators[i].setScene(this);

		}

		this.registerUpdateHandler(new IUpdateHandler()
		{
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				Update(pSecondsElapsed);
			}

			@Override
			public void reset()
			{

			}
		});
		
		moveTutorialIn(PLAYER_ONE);
		moveTutorialIn(PLAYER_TWO);
	}

	protected void moveTutorialIn(int playerIndex)
	{
		this.unregisterTouchArea(introTouchControls[(playerIndex + 1) % 2].outerImage);
		tutorialButton[playerIndex].unregisterOwnTouchArea(this);
		tutorialExitButton[playerIndex].registerOwnTouchArea(this);
		introTouchControls[(playerIndex + 1) % 2].resetButton();
		playerTutorials[playerIndex].clearEntityModifiers();
		float targetY = 0;
		switch (playerIndex)
		{
		case PLAYER_ONE:
			targetY = CAMERA_HEIGHT - playerTutorials[playerIndex].getHeight();
			break;
		case PLAYER_TWO:
			targetY = 0;
			break;
		}
		playerInTutorial[playerIndex] = true;
		playerTutorials[playerIndex].registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerTutorials[playerIndex].getY(), targetY));
	}

	private void moveTutorialOut(final int playerIndex)
	{
		playerTutorials[playerIndex].clearEntityModifiers();
		tutorialExitButton[playerIndex].unregisterOwnTouchArea(this);
		tutorialButton[playerIndex].registerOwnTouchArea(this);
		this.registerTouchArea(introTouchControls[(playerIndex + 1) % 2].outerImage);
		float targetY = 0;
		switch (playerIndex)
		{
		case PLAYER_ONE:
			targetY = CAMERA_HEIGHT;
			break;
		case PLAYER_TWO:
			targetY = -playerTutorials[playerIndex].getHeight();
			break;
		}
		playerInTutorial[playerIndex] = false;
		playerTutorials[playerIndex].registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerTutorials[playerIndex].getY(), targetY));

	}

	protected void addTile(final int player, final boolean resetOtherPlayer)
	{

		tilesCollected[player]++;
		if (resetOtherPlayer)
			breakStreak((player + 1) % 2);
		currentStreak[player]++;
		if (currentStreak[player] > maxStreak[player])
			maxStreak[player] = currentStreak[player];

	}

	protected void breakStreak(final int player)
	{
		currentStreak[player] = 0;
	}

	public void transitionChildScene(TilesMenuScene childScene)
	{
		childScene.logFlurryEvent();
		childScene.initScene();
		if (childScene.hasParent())
			childScene.detachSelf();
		setChildScene(childScene, false, true, true);
		if (childScene.hasChildScene())
			childScene.clearChildScene();
		childScene.setX(0);

		childScene.registerTouchAreas();

	}

	private void setChildSceneNull()
	{
		this.mChildScene = null;
	}

	@Override
	public void clearChildScene()
	{
		TilesMainActivity.getInstance().backEnabled = false;
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), 0));

		this.getChildScene().registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, this.getChildScene().getX(), CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				TilesMainActivity.getInstance().backEnabled = true;
				setChildSceneNull();
				super.onModifierFinished(pItem);
			}
		});
	}

	protected void badPulseText(Text textToPulse)
	{
		textToPulse.clearEntityModifiers();
		textToPulse.registerEntityModifier(new ColorModifier(TEXT_PULSE_DURATION, Color.RED, VALUE_TEXT_COLOR));
		textToPulse.registerEntityModifier(new ScaleModifier(TEXT_PULSE_DURATION, TEXT_PULSE_START_SCALE, 1.0f));
	}

	protected void bigPulseText(Text textToPulse)
	{
		textToPulse.clearEntityModifiers();
		textToPulse.registerEntityModifier(new ColorModifier(TEXT_PULSE_DURATION, Color.GREEN, VALUE_TEXT_COLOR));
		textToPulse.registerEntityModifier(new ScaleModifier(TEXT_PULSE_DURATION, TEXT_PULSE_START_SCALE, 1.0f));
	}

	protected void neutralPulseText(Text textToPulse)
	{
		textToPulse.clearEntityModifiers();
		textToPulse.setColor(VALUE_TEXT_COLOR);
		textToPulse.registerEntityModifier(new ScaleModifier(TEXT_PULSE_DURATION, TEXT_PULSE_START_SCALE, 1.0f));
	}

	protected void smallPulseText(Text textToPulse)
	{
		textToPulse.registerEntityModifier(new ScaleModifier(TEXT_PULSE_DURATION / 3, TEXT_PULSE_START_SCALE / 3, 1.0f));
	}

	private void prepareTouchControls()
	{
		introTouchControls[PLAYER_TWO] = new TouchControl("Ready", new Runnable()
		{

			@Override
			public void run()
			{
				playerOneReady = true;

			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				playerOneReady = false;

			}
		});
		final int TOUCH_CONTROL_Y = 170;
		final Sprite touchImage = introTouchControls[PLAYER_TWO].outerImage;
		introTouchControls[PLAYER_TWO].center((playerOneIntro.getWidth()) / 2, TOUCH_CONTROL_Y);
		playerOneIntro.attachChild(introTouchControls[PLAYER_TWO]);
		this.registerTouchArea(touchImage);

		introTouchControls[PLAYER_ONE] = new TouchControl("Ready", new Runnable()
		{
			@Override
			public void run()
			{
				playerTwoReady = true;

			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				playerTwoReady = false;

			}
		});
		final Sprite secondTouchImage = introTouchControls[PLAYER_ONE].outerImage;
		introTouchControls[PLAYER_ONE].center((playerTwoIntro.getWidth()) / 2, TOUCH_CONTROL_Y);
		playerTwoIntro.attachChild(introTouchControls[PLAYER_ONE]);
		this.registerTouchArea(secondTouchImage);

	}

	protected void Update(final float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.INTRO:
			if (playerOneReady && playerTwoReady && !playerInTutorial[PLAYER_ONE] && !playerInTutorial[PLAYER_TWO])
			{
				SongManager.getInstance().fadeOut();
				playerOneIntro.registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerOneIntro.getY(), CAMERA_HEIGHT));
				playerTwoIntro.registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerTwoIntro.getY(), -playerTwoIntro.getHeight()));
				startAnimateIn();
			}
			break;
		}

		secondsOnCurrentState += pSecondsElapsed;

	}

	public void startRematch()
	{
		this.clearChildScene();
		FlurryAgent.logEvent(FlurryAgentEventStrings.REMATCH);
		resetGame();
		currentTileset.resetPlayerTiles();
		resetValues();
		SongManager.getInstance().setVolumeMultiplier(1.0f);
	}

	protected void resetValues()
	{
		for (int i = 0; i < 2; i++)
		{
			tilesCollected[i] = 0;
			maxStreak[i] = 0;
			currentStreak[i] = 0;
		}
		barSpeedMulti = 1.0f;
	}

	protected void checkPlayerWillWin(int player)
	{
		if ((player == PLAYER_ONE && barSprite.getY() + barSprite.getHeight() + BAR_SPEED * barSpeedMulti > CAMERA_HEIGHT) || (player == PLAYER_TWO && barSprite.getY() - BAR_SPEED * barSpeedMulti < 0))
		{
			showGameOver(player);
			changeState(GameState.GAME_OVER);
		}
	}

	protected void checkBar()
	{
		if(barSprite.getY() +barSprite.getHeight() > CAMERA_HEIGHT)
		{
			showGameOver(PLAYER_ONE);
			changeState(GameState.GAME_OVER);
		}else if(barSprite.getY() < 0 )
		{
			showGameOver(PLAYER_TWO);
			changeState(GameState.GAME_OVER);
		}
	}
	
	protected void showGameOver(int player)
	{
		SongManager.getInstance().fadeOut();
		SharedResources.getInstance().countdownFinalHit.setRate(GAME_OVER_HIT_RATE);
		SharedResources.getInstance().countdownFinalHit.play();
		for (int i = 0; i < 2; i++)
			gameOverScreen.setPlayerValues(i, "" + tilesCollected[i], "" + maxStreak[i]);
		gameOverScreen.setWinner(player);
		transitionChildScene(gameOverScreen);
	}

	protected void startAnimateIn()
	{
		changeState(GameState.START_COUNTDOWN);

		final int BUTTON_PADDING = 3;
		pauseButton.setPosition(BUTTON_PADDING, (CAMERA_HEIGHT - pauseButton.getHeight()) / 2);
		pauseButton.setZIndex(FOREGROUND_Z);
		this.attachChild(pauseButton);
		pauseButton.registerOwnTouchArea(this);
		pauseButton.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME * 3, 0, 1.0f));

		barSprite.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME * 3, 0, 1.0f));

		currentTileset.animatePlayerTilesIn(new Runnable()
		{
			@Override
			public void run()
			{
				startCountdown();
			}
		});
	}

	protected void startCountdown()
	{
		changeState(GameState.START_COUNTDOWN);
		gameCountdown.startCountdown(new Runnable()
		{

			@Override
			public void run()
			{
				TilesMainActivity.startGameEvent();
				if (SetupScene.getDifficulty() == Difficulty.INSANE)
					currentTileset.startInsaneDelay();
				changeState(GameState.PICKING_NEW_BUTTON);
				playGameSong();

			}
		});
	}

	private void playGameSong()
	{
		if (this instanceof PracticeGameScene)
			SongManager.getInstance().playSong(SharedResources.getInstance().freePlayMusic);
		else
			SongManager.getInstance().playSong(SharedResources.getInstance().getVersusSong());
	}

	protected void moveBar(final float distance)
	{
		barSprite.registerEntityModifier(new MoveByModifier(WIN_MOVE_MOD_TIME, 0, distance));
		barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, barSprite.getScaleX(), 1.5f, 1.0f, 1.0f), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.5f, 1.0f, 1.0f, 1.0f)));
	}

	protected void resetBar()
	{
		barSprite.clearEntityModifiers();
		barSprite.setY((CAMERA_HEIGHT - barSprite.getHeight()) / 2);
	}

	protected boolean checkPlayerDisabled(int player)
	{
		switch (player)
		{

		case PLAYER_TWO:
			if (playerOneDisabled)
				return true;
			break;
		case PLAYER_ONE:
			if (playerTwoDisabled)
				return true;
			break;

		}
		return false;
	}

	protected void disablePlayer(GameButton button)
	{
		SharedResources.getInstance().wrongTileSound.play();
		currentTileset.disablePlayer(button.getPlayer());
		this.errorIndicators[button.getPlayer()].startIndicator(button.buttonSprite.getX() + button.buttonSprite.getWidth() / 2, button.buttonSprite.getY() + button.buttonSprite.getHeight() / 2);
		switch (button.getPlayer())
		{
		case PLAYER_TWO:
			playerOneDisabled = true;
			break;
		case PLAYER_ONE:
			playerTwoDisabled = true;
			break;
		}
	}

	public void enablePlayer(int player)
	{
		switch (player)
		{
		case PLAYER_TWO:
			playerOneDisabled = false;
			break;
		case PLAYER_ONE:
			playerTwoDisabled = false;
			break;
		}
	}

	protected void changeState(int newState)
	{
		this.gameState = newState;
		secondsOnCurrentState = 0;
	}

	public class GameState
	{
		public static final int	INTRO				= 0;
		public static final int	ANIMATING_TILES_IN	= INTRO + 1;
		public static final int	START_COUNTDOWN		= ANIMATING_TILES_IN + 1;
		public static final int	WAITING_FOR_INPUT	= START_COUNTDOWN + 1;
		public static final int	PICKING_NEW_BUTTON	= WAITING_FOR_INPUT + 1;
		public static final int	SHOWING_WIN			= PICKING_NEW_BUTTON + 1;
		public static final int	GAME_OVER			= SHOWING_WIN + 1;
	}

	public void showPauseScene()
	{
		transitionChildScene(pauseScene);
	}

	public void playTileCollectSound()
	{
		final Sound tileCollectSound;
		tileCollectSound = SharedResources.getInstance().tileCollectSound;

		tileCollectSound.setRate(MIN_TILE_COLLECT_RATE + rand.nextFloat() * (MAX_TILE_COLLECT_RATE - MIN_TILE_COLLECT_RATE));

		tileCollectSound.play();
	}
}
