package com.lionsteel.reflexmulti.Scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReadyTouchControl;
import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.TouchControl;
import com.lionsteel.reflexmulti.Entities.GameButton;
import com.lionsteel.reflexmulti.Entities.GameOverScreen;
import com.lionsteel.reflexmulti.Entities.Tileset;
import com.lionsteel.reflexmulti.Entities.WrongSelectionIndicator;

public abstract class GameScene extends Scene implements ReflexConstants
{
	protected ReflexActivity			activity;
	private boolean						playerOneDisabled		= false;
	private boolean						playerTwoDisabled		= false;

	protected Tileset					currentTileset;

	final BitmapTextureAtlas			sceneAtlas;
	private final Sprite				playerOneIntro;
	private final Sprite				playerTwoIntro;
	private final Sprite				barSprite;

	private final TouchControl[]		introTouchControls		= new TouchControl[2];

	private boolean						playerOneReady			= false;
	private boolean						playerTwoReady			= false;

	protected GameCountdown				gameCountdown;
	private WrongSelectionIndicator[]	errorIndicators			= new WrongSelectionIndicator[2];
	private GameOverScreen				gameOverScreen;

	protected int						gameState				= GameState.INTRO;
	protected float						secondsOnCurrentState	= 0;

	public abstract void buttonPressed(GameButton button);

	protected abstract void resetGame();

	public GameScene()
	{
		activity = ReflexActivity.getInstance();
		this.setTouchAreaBindingOnActionDownEnabled(true);

		currentTileset = SetupScene.getTileset();
		currentTileset.setParent(this);

		currentTileset.setupScene();

		gameCountdown = new GameCountdown(this);

		gameOverScreen = new GameOverScreen(this);
		gameOverScreen.setZIndex(GAME_OVER_Z);
		this.attachChild(gameOverScreen);

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 2048, 1024);
		final TextureRegion barRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "bar.png", 0, 0);
		final TextureRegion playerOneIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerOneIntro.png", (int) (barRegion.getTextureX() + barRegion.getWidth()), 0);
		final TextureRegion playerTwoIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerTwoIntro.png", (int) (playerOneIntroRegion.getTextureX() + playerOneIntroRegion.getWidth()), 0);
		sceneAtlas.load();

		barSprite = new Sprite(0, (CAMERA_HEIGHT - barRegion.getHeight()) / 2, barRegion, activity.getVertexBufferObjectManager());
		barSprite.setZIndex(FOREGROUND_Z);

		playerOneIntro = new Sprite(0, CAMERA_HEIGHT - playerTwoIntroRegion.getHeight(), playerOneIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerOneIntro.registerEntityModifier(new MoveYModifier(1.0f, playerOneIntro.getY(), CAMERA_HEIGHT)
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							playerOneReady = true;
							super.onModifierFinished(pItem);
						}
					});
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		playerOneIntro.setZIndex(FOREGROUND_Z);
		playerTwoIntro = new Sprite(0, 0, playerTwoIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerTwoIntro.registerEntityModifier(new MoveYModifier(1.0f, playerTwoIntro.getY(), -playerTwoIntro.getHeight())
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							playerTwoReady = true;
							super.onModifierFinished(pItem);
						}
					});
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		playerTwoIntro.setZIndex(FOREGROUND_Z);

		prepareTouchControls();

		this.attachChild(barSprite);

		this.attachChild(playerOneIntro);
		this.attachChild(playerTwoIntro);

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
	}

	public void transitionChildScene(ReflexMenuScene childScene)
	{

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
		if (this.mChildScene instanceof LoadingScene)
		{

			this.mChildScene = null;
			return;
		}
		ReflexActivity.getInstance().backEnabled = false;
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), 0));

		this.getChildScene().registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, this.getChildScene().getX(), CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				ReflexActivity.getInstance().backEnabled = true;
				setChildSceneNull();
				super.onModifierFinished(pItem);
			}
		});
	}

	private void prepareTouchControls()
	{
		introTouchControls[PLAYER_ONE] = new ReadyTouchControl(new Runnable()
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
		final Sprite touchImage = introTouchControls[PLAYER_ONE].touchImage;
		introTouchControls[PLAYER_ONE].setPosition((CAMERA_WIDTH - touchImage.getWidth()) / 2, 150);
		playerOneIntro.attachChild(introTouchControls[PLAYER_ONE]);
		this.registerTouchArea(touchImage);

		introTouchControls[PLAYER_TWO] = new ReadyTouchControl(new Runnable()
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
		final Sprite secondTouchImage = introTouchControls[PLAYER_TWO].touchImage;
		introTouchControls[PLAYER_TWO].setPosition((CAMERA_WIDTH - secondTouchImage.getWidth()) / 2, 50);
		playerTwoIntro.attachChild(introTouchControls[PLAYER_TWO]);
		introTouchControls[PLAYER_TWO].setRotation(180);
		this.registerTouchArea(secondTouchImage);

	}

	protected void Update(final float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.INTRO:
			if (playerOneReady && playerTwoReady)
			{
				playerOneIntro.registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerOneIntro.getY(), CAMERA_HEIGHT));
				playerTwoIntro.registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerTwoIntro.getY(), -playerTwoIntro.getHeight()));
				startAnimateIn();
			}
			break;
		case GameState.GAME_OVER:
			if (gameOverScreen.isRematchTrue())
				resetGame();
			break;
		}

		secondsOnCurrentState += pSecondsElapsed;
	}

	protected void checkPlayerWillWin(int player)
	{
		if ((player == PLAYER_TWO && barSprite.getY() + barSprite.getHeight() + BAR_SPEED > CAMERA_HEIGHT) || (player == PLAYER_ONE && barSprite.getY() - BAR_SPEED < 0))
		{
			gameOverScreen.show(player);
			changeState(GameState.GAME_OVER);
		}
	}

	protected void startAnimateIn()
	{
		changeState(GameState.START_COUNTDOWN);
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
				changeState(GameState.PICKING_NEW_BUTTON);

			}
		});
	}

	protected void moveBar(final float distance)
	{
		barSprite.registerEntityModifier(new MoveByModifier(WIN_MOVE_MOD_TIME, 0, distance));
		barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, barSprite.getScaleX(), 1.5f, 1.0f, 1.0f), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.5f, 1.0f, 1.0f, 1.0f)));
	}

	protected void resetBar()
	{
		barSprite.setY((CAMERA_HEIGHT - barSprite.getHeight()) / 2);
	}

	protected void turnOffGameOver()
	{
		gameOverScreen.hide();
	}

	protected boolean checkPlayerDisabled(int player)
	{
		switch (player)
		{
		case GameButton.PLAYER_ONE:
			if (playerOneDisabled)
				return true;
			break;
		case GameButton.PLAYER_TWO:
			if (playerTwoDisabled)
				return true;
			break;

		}
		return false;
	}

	protected void disablePlayer(GameButton button)
	{
		currentTileset.disablePlayer(button.getPlayer());
		this.errorIndicators[button.getPlayer()].startIndicator(button.buttonSprite.getX() + button.buttonSprite.getWidth() / 2, button.buttonSprite.getY() + button.buttonSprite.getHeight() / 2);
		switch (button.getPlayer())
		{
		case PLAYER_ONE:
			playerOneDisabled = true;
			break;
		case PLAYER_TWO:
			playerTwoDisabled = true;
			break;
		}
	}

	public void enablePlayer(int player)
	{
		switch (player)
		{
		case PLAYER_ONE:
			playerOneDisabled = false;
			break;
		case PLAYER_TWO:
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
}
