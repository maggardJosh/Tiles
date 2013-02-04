package com.lionsteel.reflexmulti.Entities;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.Scenes.GameScene;
import com.lionsteel.reflexmulti.SetupScene.Difficulty;

public class Tileset implements ReflexConstants
{
	private ReflexActivity			activity;

	private final GameButton[]		playerOneGameButtons	= new GameButton[NUM_BUTTONS];
	private final GameButton[]		playerTwoGameButtons	= new GameButton[NUM_BUTTONS];
	private final GameButton[]		displayGameButtons		= new GameButton[NUM_BUTTONS];

	private final Sprite			background;

	private ArrayList<GameButton>	displayedGameButtons	= new ArrayList<GameButton>();

	private GameScene				currentScene;
	private int						currentButton			= -1;

	private int						numberOfButtonsToUse	= 3;
	private final Random			rand;

	private DifficultyEntity		difficultyEntity[]		= new DifficultyEntity[3];

	public Tileset(final String basePath)
	{
		activity = ReflexActivity.getInstance();
		rand = new Random();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i] = new GameButton(i + 1, currentScene, PLAYER_TWO);
			playerTwoGameButtons[i] = new GameButton(i + 1, currentScene, PLAYER_ONE);
			displayGameButtons[i] = new GameButton(i + 1, currentScene, DISPLAY_BUTTONS);
		}

		createDifficultyEntities();

		//load background
		final BitmapTextureAtlas backgroundAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 1024, TextureOptions.BILINEAR);
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundAtlas, activity, "background.png", 0, 0);
		backgroundAtlas.load();
		background = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		background.setZIndex(BACKGROUND_Z);
	}

	private void createDifficultyEntities()
	{
		for(int x=0; x<3; x++)
			difficultyEntity[x] = new DifficultyEntity(x);
	}

	public void setParent(GameScene parent)
	{
		this.currentScene = parent;
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i].setParent(parent);
			playerTwoGameButtons[i].setParent(parent);
			displayGameButtons[i].setParent(parent);
		}
	}

	public void setupScene()
	{
		switch (SetupScene.getDifficulty())
		{
		case Difficulty.EASY:
			numberOfButtonsToUse = 3;
			break;
		case Difficulty.NORMAL:
			numberOfButtonsToUse = 5;
			break;
		case Difficulty.HARD:
			numberOfButtonsToUse = 9;
			break;
		}
		createButtons(PLAYER_ONE);
		createButtons(PLAYER_TWO);
		createButtons(DISPLAY_BUTTONS);

		currentScene.attachChild(background);

		currentScene.sortChildren();
	}

	public void clearTileset()
	{
		activity.runOnUpdateThread(new Runnable()
		{

			@Override
			public void run()
			{
				for (int i = 0; i < playerTwoGameButtons.length; i++)
				{
					currentScene.detachChild(playerTwoGameButtons[i].buttonSprite);
					currentScene.detachChild(playerOneGameButtons[i].buttonSprite);
					currentScene.detachChild(displayGameButtons[i].buttonSprite);
				}
				currentScene.detachChild(background);

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

			//Easy Buttons
			for (int x = 0; x < 3; x++)
			{
				playerTwoGameButtons[x].buttonSprite.setPosition(90 + ((2 - x) % 3) * BUTTON_WIDTH, BUTTON_WIDTH);
			}

			//Medium Buttons
			{
				playerTwoGameButtons[3].buttonSprite.setPosition(90 + BUTTON_WIDTH, BUTTON_WIDTH * 2);
				playerTwoGameButtons[4].buttonSprite.setPosition(90 + BUTTON_WIDTH, 0);
			}

			//Hard Buttons
			{

				playerTwoGameButtons[5].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, BUTTON_WIDTH * 2);
				playerTwoGameButtons[6].buttonSprite.setPosition(90, BUTTON_WIDTH * 2);
				playerTwoGameButtons[7].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, 0);
				playerTwoGameButtons[8].buttonSprite.setPosition(90, 0);

			}

			for (int x = 0; x < numberOfButtonsToUse; x++)
			{
				playerTwoGameButtons[x].buttonSprite.setRotation(180);
				playerTwoGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				currentScene.attachChild(playerTwoGameButtons[x].buttonSprite);
				currentScene.registerTouchArea(playerTwoGameButtons[x].buttonSprite);
			}

			break;
		case PLAYER_TWO:

			//Easy Buttons
			for (int x = 0; x < 3; x++)
			{
				playerOneGameButtons[x].buttonSprite.setPosition(90 + (x % 3) * BUTTON_WIDTH, 470 + BUTTON_WIDTH);
			}

			//Medium Buttons
			{
				playerOneGameButtons[3].buttonSprite.setPosition(90 + BUTTON_WIDTH, 470 + 0);
				playerOneGameButtons[4].buttonSprite.setPosition(90 + BUTTON_WIDTH, 470 + BUTTON_WIDTH * 2);
			}

			//Hard Buttons
			{

				playerOneGameButtons[5].buttonSprite.setPosition(90, 470 + 0);
				playerOneGameButtons[6].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, 470 + 0);
				playerOneGameButtons[7].buttonSprite.setPosition(90, 470 + BUTTON_WIDTH * 2);
				playerOneGameButtons[8].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, 470 + BUTTON_WIDTH * 2);

			}

			for (int x = 0; x < numberOfButtonsToUse; x++)
			{
				playerOneGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				currentScene.attachChild(playerOneGameButtons[x].buttonSprite);
				currentScene.registerTouchArea(playerOneGameButtons[x].buttonSprite);
			}
			break;
		case DISPLAY_BUTTONS:
			for (int x = 0; x < NUM_BUTTONS; x++)
			{
				resetDisplayButton(displayGameButtons[x]);
				displayGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				currentScene.attachChild(displayGameButtons[x].buttonSprite);
			}
			break;
		}
	}

	public void startStream()
	{
		spawnNewStreamTile();
	}

	private void spawnNewStreamTile()
	{
		boolean isTileAvailable = false;
		for (int i = 0; i < numberOfButtonsToUse; i++)
			if (!displayGameButtons[i].buttonSprite.isVisible())
				isTileAvailable = true;
		if (!isTileAvailable)
			return;

		int randomTile = rand.nextInt(numberOfButtonsToUse);
		while (true)
		{
			if (displayGameButtons[randomTile].buttonSprite.isVisible())
				randomTile = rand.nextInt(numberOfButtonsToUse);
			else
				break;
		}
		final GameButton randomTileButton = displayGameButtons[randomTile];
		final Sprite randomTileSprite = displayGameButtons[randomTile].buttonSprite;
		displayedGameButtons.add(displayGameButtons[randomTile]);
		randomTileSprite.setVisible(true);
		randomTileSprite.setX(-randomTileSprite.getWidth());
		randomTileSprite.registerEntityModifier(new SequenceEntityModifier(new MoveXModifier(STREAM_ON_SCREEN_SECONDS, -randomTileSprite.getWidth(), 0)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				spawnNewStreamTile();
				super.onModifierFinished(pItem);
			}
		}, new MoveXModifier(STREAM_OFF_SCREEN_SECONDS, 0, CAMERA_WIDTH))
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				resetDisplayButton(randomTileButton);
				super.onModifierFinished(pItem);
			}
		});
	}

	public GameButton getDisplayButton()
	{
		return displayGameButtons[currentButton];
	}

	public void newButton()
	{
		currentButton = rand.nextInt(numberOfButtonsToUse);
		displayGameButtons[currentButton].buttonSprite.setVisible(true);
	}

	public void animateDisplayButton(GameButton displayButton, GameButton playerButton, IEntityModifierListener listener)
	{
		displayButton.buttonSprite.clearEntityModifiers();
		if (displayButton.buttonSprite.getX() < 0)
			spawnNewStreamTile();
		displayButton.buttonSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.0f, 2.0f, EaseCubicOut.getInstance()), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 2.0f, 1.0f, EaseCubicIn.getInstance())));
		displayButton.buttonSprite.registerEntityModifier(new MoveModifier(WIN_MOVE_MOD_TIME, displayButton.buttonSprite.getX(), playerButton.buttonSprite.getX(), displayButton.buttonSprite.getY(), playerButton.buttonSprite.getY(), listener));
		displayButton.buttonSprite.registerEntityModifier(new RotationModifier(WIN_MOVE_MOD_TIME * 2 / 3, displayButton.buttonSprite.getRotation(), playerButton.buttonSprite.getRotation()));
		displayedGameButtons.remove(displayButton);
	}

	public void resetDisplayButton(final GameButton pItem)
	{
		displayedGameButtons.remove(pItem);
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				pItem.buttonSprite.clearEntityModifiers();
			}
		});
		pItem.buttonSprite.setRotation(90);
		pItem.buttonSprite.setPosition(((CAMERA_WIDTH - BUTTON_WIDTH + BAR_WIDTH) / 2), (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
		pItem.buttonSprite.setVisible(false);

	}

	private final int	SHAKE_ANGLE	= 3;

	public void disablePlayer(int player)
	{
		switch (player)
		{
		case PLAYER_ONE:
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
		case PLAYER_TWO:
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
		}

	}

	public GameButton isButtonDisplayed(int buttonNumber)
	{
		if (displayedGameButtons.contains(displayGameButtons[buttonNumber]) && displayGameButtons[buttonNumber].buttonSprite.isVisible() && displayGameButtons[buttonNumber].buttonSprite.getX() > -displayGameButtons[buttonNumber].buttonSprite.getWidth())
			return displayGameButtons[buttonNumber];
		return null;
	}

	public void reset()
	{
		for (int i = 0; i < NUM_BUTTONS; i++)
			resetDisplayButton(displayGameButtons[i]);

	}

	public DifficultyEntity getDifficultySprite(int difficulty)
	{
		return difficultyEntity[difficulty];
	}
}
