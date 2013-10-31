package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Entities.GameButton;
import com.lionsteel.tiles.Entities.TimerRect;

public class RaceGameScene extends GameScene
{
	private int[]			playerTileCount			= new int[2];
	private final Text[]	playerTileCountTexts	= new Text[2];
	private TimerRect		timerRect;

	public RaceGameScene()
	{
		super();
		this.setBackgroundEnabled(false);
		activity = TilesMainActivity.getInstance();

		timerRect = new TimerRect(RACE_SECONDS, new Runnable(){
			@Override
			public void run()
			{
				showGameOver();
			}
		});
		timerRect.setZIndex(BUTTON_Z-1);
		attachChild(timerRect);
		this.ropeSprite.setVisible(false);
		
		for (int i = 0; i < 2; i++)
		{
			playerTileCount[i] = 0;
			playerTileCountTexts[i] = new Text(0, 0, SharedResources.getInstance().mFont, "0", 3, activity.getVertexBufferObjectManager());
			playerTileCountTexts[i].setAlpha(0);
			this.attachChild(playerTileCountTexts[i]);
		}
		playerTileCountTexts[PLAYER_ONE].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - playerTileCountTexts[PLAYER_TWO].getWidth()) / 2, (CAMERA_HEIGHT) / 2 + playerTileCountTexts[PLAYER_TWO].getHeight());
		playerTileCountTexts[PLAYER_TWO].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - playerTileCountTexts[PLAYER_ONE].getWidth()) / 2, (CAMERA_HEIGHT) / 2 - playerTileCountTexts[PLAYER_TWO].getHeight() * 2);
		playerTileCountTexts[PLAYER_TWO].setRotation(180);

		this.sortChildren();

	}

	public boolean buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.IN_COUNTDOWN:
		case GameState.TUTORIAL_ANIM:
			return true;
		case GameState.WAITING_FOR_INPUT:
			if (checkPlayerDisabled(button.getPlayer()))
				return false;
			final GameButton displayButtonPressed = currentTileset.isRaceButtonCurrentlyActive(button);
			if (displayButtonPressed != null)
			{
				currentTileset.animateNonStopDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						addTile(button.getPlayer(), false);
						currentTileset.resetDisplayButton(displayButtonPressed);
						playerTileCount[button.getPlayer()]++;
						playerTileCountTexts[button.getPlayer()].setText("" + playerTileCount[button.getPlayer()]);
						playerTileCountTexts[button.getPlayer()].setX((CAMERA_WIDTH + PAUSE_BAR_WIDTH - playerTileCountTexts[button.getPlayer()].getWidth()) / 2);
						pulseTileTexts();
					}

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{

					}
				});

				sortChildren();
			} else
			{
				breakStreak(button.getPlayer());
				disablePlayer(button);
			}
			return true;
		}
		return false;
	}

	private final float	NEUTRAL_TEXT_SCALE	= 2.0f;
	private final float	MAX_TEXT_SCALE		= 3.0f;
	private final float	MAX_DIFF			= 3;

	private void pulseTileTexts()
	{
		final float diff = playerTileCount[PLAYER_TWO] - playerTileCount[PLAYER_ONE];

		final float playerOneScale = NEUTRAL_TEXT_SCALE + (Math.max(-MAX_DIFF, Math.min(diff, MAX_DIFF)) / MAX_DIFF) * (MAX_TEXT_SCALE - NEUTRAL_TEXT_SCALE);
		final float playerTwoScale = NEUTRAL_TEXT_SCALE - (Math.max(-MAX_DIFF, Math.min(diff, MAX_DIFF)) / MAX_DIFF) * (MAX_TEXT_SCALE - NEUTRAL_TEXT_SCALE);

		playerTileCountTexts[PLAYER_TWO].setScale(playerOneScale);
		playerTileCountTexts[PLAYER_ONE].setScale(playerTwoScale);

		if (diff > 0)
		{
			playerTileCountTexts[PLAYER_TWO].setColor(Color.GREEN);
			playerTileCountTexts[PLAYER_ONE].setColor(Color.RED);
		} else if (diff < 0)
		{
			playerTileCountTexts[PLAYER_TWO].setColor(Color.RED);
			playerTileCountTexts[PLAYER_ONE].setColor(Color.GREEN);
		} else
		{
			playerTileCountTexts[PLAYER_TWO].setColor(Color.WHITE);
			playerTileCountTexts[PLAYER_ONE].setColor(Color.WHITE);
		}

	}

	private void fadeInCounter()
	{
		for (Text tileCount : playerTileCountTexts)
			tileCount.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f));
	}

	@Override
	protected void startAnimateIn()
	{
		fadeInCounter();
		timerRect.fadeIn();
		super.startAnimateIn();
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.PICKING_NEW_BUTTON:
			currentTileset.startRace();
			changeState(GameState.WAITING_FOR_INPUT);
			timerRect.startTimer();
			break;
		case GameState.WAITING_FOR_INPUT:
			
			break;
		}
		super.Update(pSecondsElapsed);
	}

	private void updateTexts()
	{
		for (int i = 0; i < 2; i++)
		{
			playerTileCountTexts[i].setText("" + playerTileCount[i]);
			playerTileCountTexts[i].setX((CAMERA_WIDTH + PAUSE_BAR_WIDTH - playerTileCountTexts[i].getWidth()) / 2);
		}
	}
	
	private void showGameOver()
	{
		if (playerTileCount[PLAYER_TWO] > playerTileCount[PLAYER_ONE])
			showGameOver(PLAYER_TWO);
		else if (playerTileCount[PLAYER_ONE] > playerTileCount[PLAYER_TWO])
			showGameOver(PLAYER_ONE);
		else
			showGameOver(TIE);
	}
	

	@Override
	protected void resetGame()
	{
		currentTileset.reset();
		playerTileCount[0] = 0;
		playerTileCount[1] = 0;
		resetTexts();
		updateTexts();
		timerRect.reset();
		startCountdown();

	}

	private void resetTexts()
	{
		for (Text texts : playerTileCountTexts)
		{
			texts.clearEntityModifiers();
			texts.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			texts.setScale(1.0f);
		}

	}

}
