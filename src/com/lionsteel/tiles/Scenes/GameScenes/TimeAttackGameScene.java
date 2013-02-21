package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.PracticeGameScene;
import com.lionsteel.tiles.BaseClasses.GameScene.GameState;
import com.lionsteel.tiles.Entities.GameButton;

public class TimeAttackGameScene extends PracticeGameScene
{
	private final Text	playerTileCountLabel;
	private final Text	playerTileCountText;
	private final Text	timePlayedText;

	private int			hoursPlayed;
	private int			minutesPlayed;
	private float		secondsPlayed;

	public TimeAttackGameScene()
	{
		super();
		this.setBackgroundEnabled(false);

		playerTileCountLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Tiles Left", activity.getVertexBufferObjectManager());
		playerTileCountText = new Text(0, 0, SharedResources.getInstance().mFont, "" + TIME_ATTACK_NUM_TILES, 6, activity.getVertexBufferObjectManager());
		timePlayedText = new Text(0, 0, SharedResources.getInstance().mFont, "00:00:00.000", 15, activity.getVertexBufferObjectManager());

		hoursPlayed = 0;
		minutesPlayed = 0;
		secondsPlayed = 0;

		this.attachChild(playerTileCountText);
		this.attachChild(playerTileCountLabel);
		this.attachChild(timePlayedText);

		playerTileCountText.setAlpha(0);
		playerTileCountLabel.setAlpha(0);
		timePlayedText.setAlpha(0);

		timePlayedText.setPosition((CAMERA_WIDTH + BAR_WIDTH - timePlayedText.getWidth()) / 2, (CAMERA_HEIGHT / 4) - playerTileCountText.getHeight() / 2 - playerTileCountText.getHeight() * 2);
		playerTileCountLabel.setPosition((CAMERA_WIDTH + BAR_WIDTH - playerTileCountLabel.getWidth()) / 2, (CAMERA_HEIGHT - playerTileCountLabel.getHeight()) / 2 - playerTileCountLabel.getHeight() * 2 - BUTTON_WIDTH);
		playerTileCountText.setPosition(playerTileCountLabel.getX() + (playerTileCountLabel.getWidth() - playerTileCountText.getWidth()) / 2, playerTileCountLabel.getY() + playerTileCountLabel.getHeight() * 2);

		barSprite.setVisible(false);
		this.sortChildren();
	}

	@Override
	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_INPUT:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			final GameButton displayButtonPressed = currentTileset.isButtonCurrentlyActive(button.getButtonNumber());
			if (displayButtonPressed != null)
			{
				currentTileset.animateDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButtonPressed);

						smallPulseText(playerTileCountText);
						addTile(button.getPlayer(), false);
						updateTexts();
						checkWin();
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
	
	private void checkWin()
	{
		if(getTilesCollected(PLAYER_ONE) >= TIME_ATTACK_NUM_TILES)
		{
			showPracticeGameOver();
		}
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.PICKING_NEW_BUTTON:
			currentTileset.startNonStop();
			changeState(GameState.WAITING_FOR_INPUT);
			break;
		case GameState.WAITING_FOR_INPUT:
			secondsPlayed += pSecondsElapsed;
			updateTime();
			break;
		}
		super.Update(pSecondsElapsed);
	}

	private void updateTime()
	{
		if (secondsPlayed >= 60)
		{
			secondsPlayed -= 60;
			minutesPlayed++;
			if (minutesPlayed >= 60)
			{
				minutesPlayed -= 60;
				hoursPlayed++;
			}
		}
		timePlayedText.setText(String.format("%02d:%02d:%06.3f", hoursPlayed, minutesPlayed, secondsPlayed));
	}

	@Override
	protected void startAnimateIn()
	{
		final AlphaModifier fadeInMod = new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f);
		playerTileCountText.registerEntityModifier(fadeInMod);
		playerTileCountLabel.registerEntityModifier(fadeInMod);
		timePlayedText.registerEntityModifier(fadeInMod);
		super.startAnimateIn();
	}

	private void updateTexts()
	{

		playerTileCountText.setText("" + (TIME_ATTACK_NUM_TILES -getTilesCollected(PLAYER_ONE)));
		playerTileCountText.setX(playerTileCountLabel.getX() + (playerTileCountLabel.getWidth() - playerTileCountText.getWidth()) / 2);

	}

	@Override
	protected void resetGame()
	{
		resetValues();
		currentTileset.reset();
		hoursPlayed = 0;
		minutesPlayed = 0;
		secondsPlayed = 0;
		updateTime();
		updateTexts();
		startCountdown();
	}

}
