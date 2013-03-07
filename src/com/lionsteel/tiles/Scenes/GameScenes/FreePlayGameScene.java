package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.PracticeGameScene;
import com.lionsteel.tiles.Entities.GameButton;

public class FreePlayGameScene extends PracticeGameScene
{
	private int			playerTileCount	= 0;
	private final Text	playerTileCountLabel;
	private final Text	playerTileCountText;
	private final Text	timePlayedLabel;
	private final Text	timePlayedText;
	private final Text	inARowLabel;
	private final Text	inARowText;
	private int			hoursPlayed;
	private int			minutesPlayed;
	private float		secondsPlayed;
	private int			tilesInARow		= 0;

	public FreePlayGameScene()
	{
		super();
		this.setBackgroundEnabled(false);

		playerTileCountLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Tiles", 7, activity.getVertexBufferObjectManager());
		playerTileCountText = new Text(0, 0, SharedResources.getInstance().mFont, "0", 6, activity.getVertexBufferObjectManager());
		timePlayedText = new Text(0, 0, SharedResources.getInstance().mFont, "00:00:00.000", 15, activity.getVertexBufferObjectManager());
		timePlayedLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Time Played", activity.getVertexBufferObjectManager());
		inARowLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Streak", 15, activity.getVertexBufferObjectManager());
		inARowText = new Text(0, 0, SharedResources.getInstance().mFont, "x0", 6, activity.getVertexBufferObjectManager());

		playerTileCount = 0;
		hoursPlayed = 0;
		minutesPlayed = 0;
		secondsPlayed = 0;
		tilesInARow = 0;

		this.attachChild(playerTileCountText);
		this.attachChild(playerTileCountLabel);
		this.attachChild(timePlayedLabel);
		this.attachChild(timePlayedText);
		this.attachChild(inARowLabel);
		this.attachChild(inARowText);

		playerTileCountText.setColor(VALUE_TEXT_COLOR);
		timePlayedText.setColor(VALUE_TEXT_COLOR);
		inARowText.setColor(VALUE_TEXT_COLOR);

		playerTileCountText.setAlpha(0);
		playerTileCountLabel.setAlpha(0);
		timePlayedLabel.setAlpha(0);
		timePlayedText.setAlpha(0);
		inARowLabel.setAlpha(0);
		inARowText.setAlpha(0);

		this.setGameModeText("Free Play");

		timePlayedLabel.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - timePlayedLabel.getWidth()) / 2, gameModeText.getY() + gameModeText.getHeight() + LABEL_SPACING * 3);
		timePlayedText.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - timePlayedText.getWidth()) / 2, timePlayedLabel.getY() + timePlayedLabel.getHeight() + LABEL_SPACING);
		playerTileCountLabel.setPosition((CAMERA_WIDTH - playerTileCountLabel.getWidth()) / 3, (CAMERA_HEIGHT - playerTileCountLabel.getHeight()) / 2 - playerTileCountLabel.getHeight() * 2 - BUTTON_WIDTH);
		playerTileCountText.setPosition(playerTileCountLabel.getX() + (playerTileCountLabel.getWidth() - playerTileCountText.getWidth()) / 2, playerTileCountLabel.getY() + playerTileCountLabel.getHeight() + LABEL_SPACING);
		inARowLabel.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - inARowLabel.getWidth()) * 2 / 3, (CAMERA_HEIGHT - inARowLabel.getHeight()) / 2 - inARowLabel.getHeight() * 2 - BUTTON_WIDTH);
		inARowText.setPosition(inARowLabel.getX() + (inARowLabel.getWidth() - inARowText.getWidth()) / 2, inARowLabel.getY() + inARowLabel.getHeight() + LABEL_SPACING);

		barSprite.setVisible(false);
		this.sortChildren();

	}

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
				currentTileset.animateNonStopDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButtonPressed);
						playerTileCount++;
						tilesInARow++;
						if (playerTileCount % BIG_PULSE_MOD == 0)
							bigPulseText(playerTileCountText);
						else
							smallPulseText(playerTileCountText);
						if (tilesInARow % BIG_PULSE_MOD == 0)
							bigPulseText(inARowText);
						else
							smallPulseText(inARowText);
						updateTexts();
						addTile(button.getPlayer(), false);
					}

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{

					}
				});

				sortChildren();
			} else
			{
				if (tilesInARow > 0)
					badPulseText(inARowText);
				tilesInARow = 0;
				updateTexts();
				disablePlayer(button);
			}
			break;
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

	private void updateTexts()
	{

		playerTileCountText.setText("" + playerTileCount);
		playerTileCountText.setX(playerTileCountLabel.getX() + (playerTileCountLabel.getWidth() - playerTileCountText.getWidth()) / 2);

		inARowText.setText("x" + tilesInARow);
		inARowText.setX(inARowLabel.getX() + (inARowLabel.getWidth() - inARowText.getWidth()) / 2);
	}

	@Override
	protected void startAnimateIn()
	{
		final AlphaModifier fadeInMod = new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f);
		playerTileCountText.registerEntityModifier(fadeInMod);
		playerTileCountLabel.registerEntityModifier(fadeInMod);
		timePlayedLabel.registerEntityModifier(fadeInMod);
		timePlayedText.registerEntityModifier(fadeInMod);
		inARowLabel.registerEntityModifier(fadeInMod);
		inARowText.registerEntityModifier(fadeInMod);
		super.startAnimateIn();
	}

	@Override
	protected void resetGame()
	{
		playerTileCount = 0;
		hoursPlayed = 0;
		minutesPlayed = 0;
		secondsPlayed = 0;
		tilesInARow = 0;
		currentTileset.reset();
		updateTexts();
		startCountdown();

	}

}
