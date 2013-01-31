package com.lionsteel.reflexmulti.Scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
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

	private boolean						playerOneReady			= false;
	private boolean						playerTwoReady			= false;

	private WrongSelectionIndicator[]	errorIndicators			= new WrongSelectionIndicator[2];
	private GameOverScreen				gameOverScreen;

	protected int						gameState				= GameState.INTRO;
	protected float						secondsOnCurrentState	= 0;

	public abstract void buttonPressed(GameButton button);

	public GameScene()
	{
		activity = ReflexActivity.getInstance();
		currentTileset = new Tileset("second", this);

		currentTileset.setupScene();

		gameOverScreen = new GameOverScreen();
		gameOverScreen.setZIndex(GAME_OVER_Z);
		this.attachChild(gameOverScreen);

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 2048, 1024);
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		final TextureRegion barRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "bar.png", (int) backgroundRegion.getWidth(), 0);
		final TextureRegion playerOneIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerOneIntro.png", (int) (barRegion.getTextureX() + barRegion.getWidth()), 0);
		final TextureRegion playerTwoIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerTwoIntro.png", (int) (playerOneIntroRegion.getTextureX() + playerOneIntroRegion.getWidth()), 0);
		sceneAtlas.load();

		final Sprite backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());

		backgroundSprite.setZIndex(BACKGROUND_Z);

		barSprite = new Sprite(0, (CAMERA_HEIGHT - barRegion.getHeight()) / 2, barRegion, activity.getVertexBufferObjectManager());
		barSprite.setZIndex(FOREGROUND_Z);

		playerOneIntro = new Sprite(0, 0, playerOneIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerOneIntro.registerEntityModifier(new MoveYModifier(1.0f, playerOneIntro.getY(), -playerOneIntro.getHeight())
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
		playerTwoIntro = new Sprite(0, CAMERA_HEIGHT - playerTwoIntroRegion.getHeight(), playerTwoIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerTwoIntro.registerEntityModifier(new MoveYModifier(1.0f, playerTwoIntro.getY(), CAMERA_HEIGHT)
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

		this.registerTouchArea(playerOneIntro);
		this.registerTouchArea(playerTwoIntro);

		this.attachChild(barSprite);
		this.attachChild(backgroundSprite);

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

	protected void Update(final float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.INTRO:
			if (playerOneReady && playerTwoReady)
				changeState(GameState.PICKING_NEW_BUTTON);
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
		gameOverScreen.setVisible(false);
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
		public static final int	WAITING_FOR_BUTTON	= INTRO + 1;
		public static final int	PICKING_NEW_BUTTON	= WAITING_FOR_BUTTON + 1;
		public static final int	SHOWING_WIN			= PICKING_NEW_BUTTON + 1;
		public static final int	GAME_OVER			= SHOWING_WIN + 1;
	}
}
