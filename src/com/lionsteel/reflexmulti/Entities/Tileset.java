package com.lionsteel.reflexmulti.Entities;

import java.util.Random;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.Scenes.GameScene;

public class Tileset implements ReflexConstants
{
	private ReflexActivity		activity;
	private static final int	NUM_BUTTONS				= 6;

	private GameButton[]		playerOneGameButtons	= new GameButton[NUM_BUTTONS];
	private GameButton[]		playerTwoGameButtons	= new GameButton[NUM_BUTTONS];
	private GameButton[]		displayGameButtons		= new GameButton[NUM_BUTTONS];

	private GameScene			currentScene;
	private int					currentButton			= -1;

	private final Random		rand;

	public Tileset(final String basePath, final GameScene currentScene)
	{
		activity = ReflexActivity.getInstance();
		this.currentScene = currentScene;
		rand = new Random();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i] = new GameButton(i + 1, currentScene, PLAYER_ONE);
			playerTwoGameButtons[i] = new GameButton(i + 1, currentScene, PLAYER_TWO);
			displayGameButtons[i] = new GameButton(i + 1, currentScene, DISPLAY_BUTTONS);
		}
	}

	public void setupScene()
	{
		createButtons(PLAYER_ONE);
		createButtons(PLAYER_TWO);
		createButtons(DISPLAY_BUTTONS);

		currentScene.sortChildren();
	}

	public void clearTileset()
	{
		activity.runOnUpdateThread(new Runnable()
		{

			@Override
			public void run()
			{
				for (int i = 0; i < playerOneGameButtons.length; i++)
				{
					currentScene.detachChild(playerOneGameButtons[i].buttonSprite);
					currentScene.detachChild(playerTwoGameButtons[i].buttonSprite);
					currentScene.detachChild(displayGameButtons[i].buttonSprite);
				}

			}
		});

	}

	public int getCurrentButtonNumber()
	{
		return currentButton;
	}

	private void createButtons(int player)
	{
		switch (player)
		{
		case PLAYER_ONE:

			for (int x = 0; x < NUM_BUTTONS; x++)
			{
				playerOneGameButtons[x].buttonSprite.setPosition(BAR_WIDTH + (x % 3) * BUTTON_WIDTH, (int) (x / 3) * BUTTON_WIDTH);
				playerOneGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				currentScene.attachChild(playerOneGameButtons[x].buttonSprite);
				currentScene.registerTouchArea(playerOneGameButtons[x].buttonSprite);
			}
			break;
		case PLAYER_TWO:
			for (int x = 0; x < NUM_BUTTONS; x++)
			{
				playerTwoGameButtons[x].buttonSprite.setPosition(BAR_WIDTH + (x % 3) * BUTTON_WIDTH, 500 + (int) ((5 - x) / 3) * BUTTON_WIDTH);
				playerTwoGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				currentScene.attachChild(playerTwoGameButtons[x].buttonSprite);
				currentScene.registerTouchArea(playerTwoGameButtons[x].buttonSprite);
			}

			break;
		case DISPLAY_BUTTONS:
			for (int x = 0; x < NUM_BUTTONS; x++)
			{
				resetDisplayButton(displayGameButtons[x].buttonSprite);
				displayGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				currentScene.attachChild(displayGameButtons[x].buttonSprite);
			}
			break;
		}
	}

	public GameButton getDisplayButton()
	{
		return displayGameButtons[currentButton];
	}

	public void newButton()
	{
		currentButton = rand.nextInt(6);
		displayGameButtons[currentButton].buttonSprite.setVisible(true);
	}

	public void resetDisplayButton(IEntity pItem)
	{
		pItem.setPosition(((CAMERA_WIDTH - BUTTON_WIDTH + BAR_WIDTH) / 2), (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
		pItem.setVisible(false);

	}

	private final int	SHAKE_ANGLE	= 3;

	public void disablePlayer(int player)
	{
		switch (player)
		{
		case PLAYER_ONE:
			for (int i = 0; i < NUM_BUTTONS; i++)
			{
				playerOneGameButtons[i].buttonSprite.setAlpha(.7f);
				playerOneGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(DISABLE_TIME / 12, 0, -SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, SHAKE_ANGLE, 0)), 4)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.setAlpha(1.0f);
						super.onModifierFinished(pItem);
					}
				});
			}

			break;
		case PLAYER_TWO:
			for (int i = 0; i < NUM_BUTTONS; i++)
			{
				playerTwoGameButtons[i].buttonSprite.setAlpha(.7f);
				playerTwoGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(DISABLE_TIME / 12, 0, -SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, SHAKE_ANGLE, 0)), 4)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.setAlpha(1.0f);
						super.onModifierFinished(pItem);
					}
				});
			}
			break;
		}

	}
}
