package com.lionsteel.reflexmulti.Scenes;

import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
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
import com.lionsteel.reflexmulti.Entities.WrongSelectionIndicator;

public class GameScene extends Scene implements ReflexConstants
{
	final BitmapTextureAtlas			sceneAtlas;
	final ReflexActivity				activity;
	private int							gameState				= GameState.INTRO;

	private int							currentButton			= -1;
	private float						secondsOnCurrentState	= 0;

	private final Random				rand;

	private final Sprite				barSprite;

	private final Sprite				playerOneIntro;
	private final Sprite				playerTwoIntro;

	private boolean						playerOneReady			= false;
	private boolean						playerTwoReady			= false;

	private boolean						playerOneDisabled		= false;
	private boolean						playerTwoDisabled		= false;

	private WrongSelectionIndicator[]	errorIndicators			= new WrongSelectionIndicator[2];

	private GameButton[]				gameButtons				= new GameButton[6];

	public GameScene()
	{
		activity = ReflexActivity.getInstance();
		rand = new Random();
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 2048);
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		final TextureRegion barRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "bar.png", 0, (int) backgroundRegion.getHeight());
		final TextureRegion playerOneIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerOneIntro.png", 0, (int) (barRegion.getTextureY() + barRegion.getHeight()));
		final TextureRegion playerTwoIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerTwoIntro.png", 0, (int) (playerOneIntroRegion.getTextureY() + playerOneIntroRegion.getHeight()));
		sceneAtlas.load();
		
		final Sprite backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());

		barSprite = new Sprite((CAMERA_WIDTH - barRegion.getWidth()) / 2, CAMERA_HEIGHT - BAR_HEIGHT, barRegion, activity.getVertexBufferObjectManager());

		playerOneIntro = new Sprite(0, 0, playerOneIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerOneIntro.registerEntityModifier(new MoveXModifier(1.0f, playerOneIntro.getX(), -playerOneIntro.getWidth())
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
		playerTwoIntro = new Sprite(CAMERA_WIDTH - playerTwoIntroRegion.getWidth(), 0, playerTwoIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerTwoIntro.registerEntityModifier(new MoveXModifier(1.0f, playerTwoIntro.getX(), CAMERA_WIDTH)
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

		this.registerTouchArea(playerOneIntro);
		this.registerTouchArea(playerTwoIntro);
		
		this.attachChild(backgroundSprite);
		this.attachChild(barSprite);

		createButtons(PLAYER_ONE);
		createButtons(PLAYER_TWO);
		createButtons(DISPLAY_BUTTONS);

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

		this.attachChild(playerOneIntro);
		this.attachChild(playerTwoIntro);

		for (int i = 0; i < 2; i++)
		{
			errorIndicators[i] = new WrongSelectionIndicator(i);
			errorIndicators[i].setScene(this);
		}

	}

	private void createButtons(int player)
	{
		switch (player)
		{
		case PLAYER_ONE:

			for (int x = 0; x < 6; x++)
			{
				GameButton button = new GameButton(x + 1, this, PLAYER_ONE);
				button.buttonSprite.setPosition((int) (x / 3) * BUTTON_WIDTH, (x % 3) * BUTTON_WIDTH);
				this.attachChild(button.buttonSprite);
				this.registerTouchArea(button.buttonSprite);
			}
			break;
		case PLAYER_TWO:
			for (int x = 0; x < 6; x++)
			{
				GameButton button = new GameButton(x + 1, this, PLAYER_TWO);
				button.buttonSprite.setPosition(500 + (int) ((5 - x) / 3) * BUTTON_WIDTH, (x % 3) * BUTTON_WIDTH);
				this.attachChild(button.buttonSprite);
				this.registerTouchArea(button.buttonSprite);
			}

			break;

		case DISPLAY_BUTTONS:
			for (int x = 0; x < 6; x++)
			{
				gameButtons[x] = new GameButton(x + 1, this, DISPLAY_BUTTONS);
				gameButtons[x].buttonSprite.setPosition((CAMERA_WIDTH - BUTTON_WIDTH) / 2, ((CAMERA_HEIGHT - BUTTON_WIDTH - BAR_HEIGHT) / 2));
				gameButtons[x].buttonSprite.setVisible(false);
				this.attachChild(gameButtons[x].buttonSprite);
			}
			break;
		}
	}

	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_BUTTON:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			if (button.getButtonNumber() == (currentButton + 1))
			{
				gameButtons[currentButton].buttonSprite.registerEntityModifier(new MoveModifier(WIN_MOVE_MOD_TIME, gameButtons[currentButton].buttonSprite.getX(), button.buttonSprite.getX(), gameButtons[currentButton].buttonSprite.getY(), button.buttonSprite.getY())
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.setPosition((CAMERA_WIDTH - BUTTON_WIDTH) / 2, (CAMERA_HEIGHT - BUTTON_WIDTH - BAR_HEIGHT) / 2);
						pItem.setVisible(false);
						changeState(GameState.PICKING_NEW_BUTTON);
						switch (button.getPlayer())
						{
						case PLAYER_ONE:
							barSprite.registerEntityModifier(new MoveByModifier(WIN_MOVE_MOD_TIME, -BAR_SPEED, 0));
							barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME/2, 1.0f, 1.0f, barSprite.getScaleY(), 1.5f), new ScaleModifier(WIN_MOVE_MOD_TIME/2, 1.0f, 1.0f, 1.5f, 1.0f)));
							break;
						case PLAYER_TWO:
							barSprite.registerEntityModifier(new MoveByModifier(WIN_MOVE_MOD_TIME, BAR_SPEED, 0));
							barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME/2, 1.0f, 1.0f, barSprite.getScaleY(), 1.5f), new ScaleModifier(WIN_MOVE_MOD_TIME/2, 1.0f, 1.0f, 1.5f, 1.0f)));
							break;
						}
						super.onModifierFinished(pItem);
					}

				});
				changeState(GameState.SHOWING_WIN);
			} else
			{
				disablePlayer(button);
			}
			break;
		}
	}

	private void disablePlayer(GameButton button)
	{
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

	private boolean checkPlayerDisabled(int player)
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

	private void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.INTRO:
			if (playerOneReady && playerTwoReady)
				changeState(GameState.PICKING_NEW_BUTTON);
			break;
		case GameState.PICKING_NEW_BUTTON:
			if (secondsOnCurrentState >= 1)
			{
				newButton();
				enablePlayer(PLAYER_ONE);
				enablePlayer(PLAYER_TWO);
				changeState(GameState.WAITING_FOR_BUTTON);
			}
			break;
		case GameState.WAITING_FOR_BUTTON:

			break;
		}

		secondsOnCurrentState += pSecondsElapsed;
	}

	private void newButton()
	{
		currentButton = rand.nextInt(6);
		gameButtons[currentButton].buttonSprite.setVisible(true);
	}

	private void changeState(int newState)
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
	}

}
