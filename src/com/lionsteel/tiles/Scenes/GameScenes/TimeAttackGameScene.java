package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesSharedPreferenceStrings;
import com.lionsteel.tiles.BaseClasses.PracticeGameScene;
import com.lionsteel.tiles.BaseClasses.GameScene.GameState;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Entities.GameButton;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public class TimeAttackGameScene extends PracticeGameScene
{
	private final Text	difficultyLabel;
	private final Text	difficultyText;
	private final Text	bestTimeLabel;
	private final Text	bestTimeValue;
	private final Text	tileCountLabel;
	private final Text	tileCountText;
	private final Text	timePlayedLabel;
	private final Text	timePlayedText;

	private int			hoursPlayed;
	private int			minutesPlayed;
	private float		secondsPlayed;
	private float		totalSecondsPlayed;

	private final float	START_Y	= 40;

	public TimeAttackGameScene()
	{
		super();
		this.setBackgroundEnabled(false);

		difficultyLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Difficulty", activity.getVertexBufferObjectManager());
		difficultyText = new Text(0, 0, SharedResources.getInstance().mFont, Difficulty.getName(SetupScene.getDifficulty()), activity.getVertexBufferObjectManager());
		bestTimeLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Best Time", activity.getVertexBufferObjectManager());
		bestTimeValue = new Text(0, 0, SharedResources.getInstance().mFont, getBestTimeString(), activity.getVertexBufferObjectManager());
		tileCountLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Tiles Left", activity.getVertexBufferObjectManager());
		tileCountText = new Text(0, 0, SharedResources.getInstance().mFont, "" + TIME_ATTACK_NUM_TILES, 6, activity.getVertexBufferObjectManager());
		timePlayedLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Round Time", activity.getVertexBufferObjectManager());
		timePlayedText = new Text(0, 0, SharedResources.getInstance().mFont, "00:00:00.000", 15, activity.getVertexBufferObjectManager());

		hoursPlayed = 0;
		minutesPlayed = 0;
		secondsPlayed = 0;

		this.attachChild(difficultyLabel);
		this.attachChild(difficultyText);
		this.attachChild(bestTimeLabel);
		this.attachChild(bestTimeValue);
		this.attachChild(tileCountText);
		this.attachChild(tileCountLabel);
		this.attachChild(timePlayedLabel);
		this.attachChild(timePlayedText);

		difficultyText.setColor(VALUE_TEXT_COLOR);
		bestTimeValue.setColor(VALUE_TEXT_COLOR);
		tileCountText.setColor(VALUE_TEXT_COLOR);
		timePlayedText.setColor(VALUE_TEXT_COLOR);

		difficultyLabel.setAlpha(0);
		difficultyText.setAlpha(0);
		bestTimeLabel.setAlpha(0);
		bestTimeValue.setAlpha(0);
		tileCountText.setAlpha(0);
		tileCountLabel.setAlpha(0);
		timePlayedLabel.setAlpha(0);
		timePlayedText.setAlpha(0);

		difficultyLabel.setPosition((CAMERA_WIDTH + BAR_WIDTH - difficultyLabel.getWidth()) / 2, START_Y);
		difficultyText.setPosition((CAMERA_WIDTH + BAR_WIDTH - difficultyText.getWidth()) / 2, difficultyLabel.getY() + difficultyLabel.getHeight() + LABEL_SPACING);
		bestTimeLabel.setPosition((CAMERA_WIDTH + BAR_WIDTH - bestTimeLabel.getWidth()) / 2, difficultyText.getY() + difficultyText.getHeight() + LABEL_SPACING * 2);
		bestTimeValue.setPosition((CAMERA_WIDTH + BAR_WIDTH - bestTimeValue.getWidth()) / 2, bestTimeLabel.getY() + bestTimeLabel.getHeight() + LABEL_SPACING);
		timePlayedLabel.setPosition((CAMERA_WIDTH + BAR_WIDTH - timePlayedLabel.getWidth()) / 2, bestTimeValue.getY() + bestTimeValue.getHeight() + LABEL_SPACING * 2);
		timePlayedText.setPosition((CAMERA_WIDTH + BAR_WIDTH - timePlayedText.getWidth()) / 2, timePlayedLabel.getY() + timePlayedLabel.getHeight() + LABEL_SPACING);
		tileCountLabel.setPosition((CAMERA_WIDTH + BAR_WIDTH - tileCountLabel.getWidth()) / 2, timePlayedText.getY() + timePlayedText.getHeight() + LABEL_SPACING * 2);
		tileCountText.setPosition(tileCountLabel.getX() + (tileCountLabel.getWidth() - tileCountText.getWidth()) / 2, tileCountLabel.getY() + tileCountLabel.getHeight() + LABEL_SPACING);

		barSprite.setVisible(false);
		this.sortChildren();
	}

	private String getBestTimeString()
	{
		float bestTimeAttackSeconds = activity.sharedPrefs.getFloat(TilesSharedPreferenceStrings.bestTimeAttack[SetupScene.getDifficulty()], 0);

		int bestTimeMinutes = (int) (bestTimeAttackSeconds / 60);
		final int bestTimeHours = (int) (bestTimeMinutes / 60);
		bestTimeMinutes = bestTimeMinutes % 60;
		bestTimeAttackSeconds = bestTimeAttackSeconds % 60;

		if (bestTimeAttackSeconds == 0)
			return "99:99:99.999";
		else
			return String.format("%02d:%02d:%06.3f", bestTimeHours, bestTimeMinutes, bestTimeAttackSeconds);

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

						smallPulseText(tileCountText);
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
		if (getTilesCollected(PLAYER_ONE) >= TIME_ATTACK_NUM_TILES)
		{
			practiceGameOverScene.setLabels("Best Time", "Round Time");
			float bestTimeAttackSeconds = activity.sharedPrefs.getFloat(TilesSharedPreferenceStrings.bestTimeAttack[SetupScene.getDifficulty()], 0);

			int bestTimeMinutes = (int) (bestTimeAttackSeconds / 60);
			final int bestTimeHours = (int) (bestTimeMinutes / 60);
			bestTimeMinutes = bestTimeMinutes % 60;
			bestTimeAttackSeconds = bestTimeAttackSeconds % 60;

			final String bestTimeValue;
			if (bestTimeAttackSeconds == 0)
				bestTimeValue = "None";
			else
				bestTimeValue = String.format("%02d:%02d:%06.3f", bestTimeHours, bestTimeMinutes, bestTimeAttackSeconds);

			practiceGameOverScene.setValues(bestTimeValue, String.format("%02d:%02d:%06.3f", hoursPlayed, minutesPlayed, secondsPlayed));
			showPracticeGameOver();

			if (bestTimeAttackSeconds > totalSecondsPlayed || bestTimeAttackSeconds == 0)
			{
				activity.saveFloat(TilesSharedPreferenceStrings.bestTimeAttack[SetupScene.getDifficulty()], totalSecondsPlayed);
				practiceGameOverScene.pulseNewRecord();
			}
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
			totalSecondsPlayed += pSecondsElapsed;
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
		difficultyLabel.registerEntityModifier(fadeInMod);
		difficultyText.registerEntityModifier(fadeInMod);
		bestTimeLabel.registerEntityModifier(fadeInMod);
		bestTimeValue.registerEntityModifier(fadeInMod);
		tileCountText.registerEntityModifier(fadeInMod);
		tileCountLabel.registerEntityModifier(fadeInMod);
		timePlayedLabel.registerEntityModifier(fadeInMod);
		timePlayedText.registerEntityModifier(fadeInMod);
		super.startAnimateIn();
	}

	private void updateTexts()
	{

		tileCountText.setText("" + (TIME_ATTACK_NUM_TILES - getTilesCollected(PLAYER_ONE)));
		tileCountText.setX(tileCountLabel.getX() + (tileCountLabel.getWidth() - tileCountText.getWidth()) / 2);

	}

	private void updateBestTimeText()
	{
		bestTimeValue.setText(getBestTimeString());
		bestTimeValue.setX((CAMERA_WIDTH + BAR_WIDTH - bestTimeValue.getWidth()) / 2);
	}

	@Override
	protected void resetGame()
	{
		resetValues();
		currentTileset.reset();
		hoursPlayed = 0;
		minutesPlayed = 0;
		secondsPlayed = 0;
		totalSecondsPlayed = 0;
		updateTime();
		updateTexts();
		updateBestTimeText();
		startCountdown();
	}

}
