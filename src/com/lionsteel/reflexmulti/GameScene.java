package com.lionsteel.reflexmulti;

import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

public class GameScene extends Scene implements ReflexConstants
{
	final BitmapTextureAtlas	sceneAtlas;
	final ReflexActivity		activity;
	private int					gameState				= GameState.INTRO;

	private int					currentButton			= -1;
	private float				secondsOnCurrentState	= 0;

	private final Random		rand;

	private GameButton[]		gameButtons				= new GameButton[6];

	public GameScene()
	{
		activity = ReflexActivity.getInstance();
		rand = new Random();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 512);
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		sceneAtlas.load();
		final Sprite backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());

		this.attachChild(backgroundSprite);

		for (int x = 0; x < 6; x++)
		{
			GameButton button = new GameButton(x + 1, this, PLAYER_ONE);
			button.buttonSprite.setPosition((int) (x / 3) * BUTTON_WIDTH, (x % 3) * BUTTON_WIDTH);
			this.attachChild(button.buttonSprite);
			this.registerTouchArea(button.buttonSprite);
		}
		for (int x = 0; x < 6; x++)
		{
			GameButton button = new GameButton(x + 1, this, PLAYER_TWO);
			button.buttonSprite.setPosition(500 + (int) ((5-x) / 3) * BUTTON_WIDTH, (x % 3) * BUTTON_WIDTH);
			this.attachChild(button.buttonSprite);
			this.registerTouchArea(button.buttonSprite);
		}

		for (int x = 0; x < 6; x++)
		{
			gameButtons[x] = new GameButton(x + 1, this, -1);
			gameButtons[x].buttonSprite.setPosition((CAMERA_WIDTH - BUTTON_WIDTH) / 2, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
			gameButtons[x].buttonSprite.setVisible(false);
			this.attachChild(gameButtons[x].buttonSprite);
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

	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_BUTTON:
			if (button.buttonNumber == (currentButton+1))
			{
				gameButtons[currentButton].buttonSprite.registerEntityModifier(new MoveModifier(.5f, gameButtons[currentButton].buttonSprite.getX(), button.buttonSprite.getX()
						, gameButtons[currentButton].buttonSprite.getY(), button.buttonSprite.getY())
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.setPosition((CAMERA_WIDTH-BUTTON_WIDTH)/2, (CAMERA_HEIGHT-BUTTON_WIDTH)/2);
						pItem.setVisible(false);
						changeState(GameState.PICKING_NEW_BUTTON);
						super.onModifierFinished(pItem);
					}
				});
			}
			break;
		}
	}

	private void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.INTRO:
			if (secondsOnCurrentState >= 1)
				changeState(GameState.PICKING_NEW_BUTTON);
			break;
		case GameState.PICKING_NEW_BUTTON:
			if (secondsOnCurrentState >= 1)
			{
				newButton();
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
	}

}
