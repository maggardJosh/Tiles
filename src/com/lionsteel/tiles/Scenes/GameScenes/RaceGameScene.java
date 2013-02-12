package com.lionsteel.tiles.Scenes.GameScenes;

import java.util.Locale;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Entities.GameButton;

public class RaceGameScene extends GameScene
{
	private int[]			playerTileCount			= new int[2];
	private final Text[]	playerTileCountTexts	= new Text[2];
	private final Text		timerText;
	private final Text		timerTextShadow;
	private float			mSecondsLeft			= RACE_SECONDS;

	public RaceGameScene()
	{
		super();
		this.setBackgroundEnabled(false);
		activity = TilesMainActivity.getInstance();

		for (int i = 0; i < 2; i++)
		{
			playerTileCount[i] = 0;
			playerTileCountTexts[i] = new Text(0, 0, SharedResources.getInstance().mFont, "0", 3, activity.getVertexBufferObjectManager());
			playerTileCountTexts[i].setAlpha(0);
			this.attachChild(playerTileCountTexts[i]);
		}
		playerTileCountTexts[PLAYER_TWO].setPosition((CAMERA_WIDTH + BAR_WIDTH - playerTileCountTexts[PLAYER_ONE].getWidth()) / 2, (CAMERA_HEIGHT) / 2 + playerTileCountTexts[PLAYER_ONE].getHeight());
		playerTileCountTexts[PLAYER_ONE].setPosition((CAMERA_WIDTH + BAR_WIDTH - playerTileCountTexts[PLAYER_TWO].getWidth()) / 2, (CAMERA_HEIGHT) / 2 - playerTileCountTexts[PLAYER_ONE].getHeight() * 2);
		playerTileCountTexts[PLAYER_ONE].setRotation(180);

		timerText = new Text(0, 0, SharedResources.getInstance().mFont, (int) Math.floor(mSecondsLeft) + ".000", activity.getVertexBufferObjectManager());
		timerText.setZIndex(FOREGROUND_Z);
		timerText.setRotation(90);
		timerTextShadow = new Text(0, 0, SharedResources.getInstance().mFont, (int) Math.floor(mSecondsLeft) + ".000", activity.getVertexBufferObjectManager());
		timerTextShadow.setRotation(90);
		timerTextShadow.setColor(0, 0, 0);
		timerTextShadow.setZIndex(FOREGROUND_Z);

		this.attachChild(timerTextShadow);
		this.attachChild(timerText);

		updateTimerText();

		this.sortChildren();

	}

	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_INPUT:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			final GameButton displayButtonPressed = currentTileset.isRaceButtonCurrentlyActive(button);
			if (displayButtonPressed != null)
			{
				currentTileset.animateDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButtonPressed);
						playerTileCount[button.getPlayer()]++;
						playerTileCountTexts[button.getPlayer()].setText("" + playerTileCount[button.getPlayer()]);
						playerTileCountTexts[button.getPlayer()].setX((CAMERA_WIDTH + BAR_WIDTH - playerTileCountTexts[button.getPlayer()].getWidth()) / 2);
					}

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{

					}
				});

				sortChildren();
			} else
			{

				disablePlayer(button);
			}
			break;
		}
	}

	private void fadeInCounter()
	{
		for (Text tileCount : playerTileCountTexts)
			tileCount.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f));
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.PICKING_NEW_BUTTON:
			currentTileset.startRace();
			fadeInCounter();
			changeState(GameState.WAITING_FOR_INPUT);
			startTimer();
			break;
		case GameState.WAITING_FOR_INPUT:
			mSecondsLeft -= pSecondsElapsed;
			if (mSecondsLeft < 0)
				mSecondsLeft = 0;
			updateTimerText();
			break;
		}
		super.Update(pSecondsElapsed);
	}

	private void updateTexts()
	{
		for (int i = 0; i < 2; i++)
		{
			playerTileCountTexts[i].setText("" + playerTileCount[i]);
			playerTileCountTexts[i].setX((CAMERA_WIDTH + BAR_WIDTH - playerTileCountTexts[i].getWidth()) / 2);
		}
	}

	private void updateTimerText()
	{
		final String timerString = String.format(Locale.US, "%2.3f", this.mSecondsLeft);
		timerText.setText(timerString);
		timerText.setPosition((BAR_WIDTH - timerText.getWidth()) / 2, (CAMERA_HEIGHT - timerText.getHeight()) / 2);
		timerTextShadow.setText(timerString);
		timerTextShadow.setPosition(timerText.getX() - 2, timerText.getY() + 2);
	}

	private void startTimer()
	{
		final float currentScale = barSprite.getScaleY();
		if (currentScale < .01f)
		{
			if (playerTileCount[PLAYER_ONE] > playerTileCount[PLAYER_TWO])
				showGameOver(PLAYER_ONE);
			else if (playerTileCount[PLAYER_TWO] > playerTileCount[PLAYER_ONE])
				showGameOver(PLAYER_TWO);
			else
				showGameOver(TIE);
			return;
		}
		barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(.5f, 1.0f, 2.0f, currentScale, currentScale - (1.0f / RACE_SECONDS) * .5f, EaseCubicOut.getInstance()), new ScaleModifier(.5f, 2.0f, 1.0f, currentScale - (1.0f / RACE_SECONDS) / 2, currentScale - (1.0f / RACE_SECONDS), EaseCubicIn.getInstance()))
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				startTimer();
				super.onModifierFinished(pItem);
			}
		});

	}

	@Override
	protected void resetGame()
	{
		currentTileset.reset();
		playerTileCount[0] = 0;
		playerTileCount[1] = 0;
		updateTexts();
		barSprite.setScale(1.0f);
		mSecondsLeft = RACE_SECONDS;
		updateTimerText();
		startCountdown();

	}

}
