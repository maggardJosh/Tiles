package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesSharedPreferenceStrings;
import com.lionsteel.tiles.BaseClasses.PracticeGameScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Entities.GameButton;
import com.lionsteel.tiles.Entities.TimerRect;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public class FrenzyGameScene extends PracticeGameScene
{
	private final Text	difficultyLabel;
	private final Text	difficultyText;
	private final Text	bestTilesLabel;
	private final Text	bestTilesValue;
	private final Text	roundTilesLabel;
	private final Text	roundTilesText;

	private TimerRect	tileRect;

	public FrenzyGameScene()
	{
		super();
		this.setBackgroundEnabled(false);
		tileRect = new TimerRect(FRENZY_SECONDS, new Runnable()
		{
			@Override
			public void run()
			{
				gameOver();
			}
		});

		this.attachChild(tileRect);

		this.setGameModeText("Frenzy");

		difficultyLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Difficulty", activity.getVertexBufferObjectManager());
		difficultyText = new Text(0, 0, SharedResources.getInstance().mFont, Difficulty.getName(SetupScene.getDifficulty()), activity.getVertexBufferObjectManager());
		bestTilesLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Best Tiles", activity.getVertexBufferObjectManager());
		bestTilesValue = new Text(0, 0, SharedResources.getInstance().mFont, getBestTilesString(), activity.getVertexBufferObjectManager());
		roundTilesLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Round Tiles", activity.getVertexBufferObjectManager());
		roundTilesText = new Text(0, 0, SharedResources.getInstance().mFont, getRoundTilesString(), 5, activity.getVertexBufferObjectManager());

		this.attachChild(difficultyLabel);
		this.attachChild(difficultyText);
		this.attachChild(bestTilesLabel);
		this.attachChild(bestTilesValue);
		this.attachChild(roundTilesLabel);
		this.attachChild(roundTilesText);

		difficultyText.setColor(VALUE_TEXT_COLOR);
		bestTilesValue.setColor(VALUE_TEXT_COLOR);
		roundTilesText.setColor(VALUE_TEXT_COLOR);

		difficultyLabel.setAlpha(0);
		difficultyText.setAlpha(0);
		bestTilesLabel.setAlpha(0);
		bestTilesValue.setAlpha(0);
		roundTilesLabel.setAlpha(0);
		roundTilesText.setAlpha(0);

		difficultyLabel.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - difficultyLabel.getWidth()) / 2, gameModeText.getY() + gameModeText.getHeight() + LABEL_SPACING * 3);
		difficultyText.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - difficultyText.getWidth()) / 2, difficultyLabel.getY() + difficultyLabel.getHeight() + LABEL_SPACING);
		bestTilesLabel.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - bestTilesLabel.getWidth()) / 2, difficultyText.getY() + difficultyText.getHeight() + LABEL_SPACING * 2);
		bestTilesValue.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - bestTilesValue.getWidth()) / 2, bestTilesLabel.getY() + bestTilesLabel.getHeight() + LABEL_SPACING);
		roundTilesLabel.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - roundTilesLabel.getWidth()) / 2, bestTilesValue.getY() + bestTilesValue.getHeight() + LABEL_SPACING * 2);
		roundTilesText.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - roundTilesText.getWidth()) / 2, roundTilesLabel.getY() + roundTilesLabel.getHeight() + LABEL_SPACING);

		barSprite.setVisible(false);
		this.sortChildren();
	}

	private String getSaveString()
	{
		return TilesSharedPreferenceStrings.FRENZY_BEST_TILES + Difficulty.getName(SetupScene.getDifficulty()) + currentTileset.getBasePath();
	}

	private int getBestTiles()
	{
		return activity.sharedPrefs.getInt(getSaveString(), 0);
	}

	private CharSequence getRoundTilesString()
	{
		return String.format("%03d", getTilesCollected(PLAYER_ONE));
	}

	private CharSequence getBestTilesString()
	{
		return String.format("%03d", getBestTiles());
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
				currentTileset.animateNonStopDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButtonPressed);
						addTile(button.getPlayer(), false);
						roundTilesText.setText(getRoundTilesString());
						smallPulseText(roundTilesText);

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

	private void gameOver()
	{
		practiceGameOverScene.setLabels("Most Tiles", "Round Tiles");
		practiceGameOverScene.setValues(getBestTilesString(), getRoundTilesString());
		if(getBestTiles() < getTilesCollected(PLAYER_ONE))
		{
			activity.saveInt(getSaveString(), getTilesCollected(PLAYER_ONE));
			FlurryAgent.logEvent(FlurryAgentEventStrings.NEW_FRENZY_RECORD);
			practiceGameOverScene.pulseNewRecord();
		}
		showPracticeGameOver();
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.PICKING_NEW_BUTTON:
			currentTileset.startNonStop();
			tileRect.startTimer();
			changeState(GameState.WAITING_FOR_INPUT);
			break;
		case GameState.WAITING_FOR_INPUT:
			break;
		}
		super.Update(pSecondsElapsed);
	}

	@Override
	protected void startAnimateIn()
	{
		final AlphaModifier fadeInMod = new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f);
		difficultyLabel.registerEntityModifier(fadeInMod);
		difficultyText.registerEntityModifier(fadeInMod);
		bestTilesLabel.registerEntityModifier(fadeInMod);
		bestTilesValue.registerEntityModifier(fadeInMod);
		roundTilesLabel.registerEntityModifier(fadeInMod);
		roundTilesText.registerEntityModifier(fadeInMod);
		tileRect.fadeIn();
		super.startAnimateIn();
	}
	
	private void updateTexts()
	{
		bestTilesValue.setText(getBestTilesString());
		roundTilesText.setText(getRoundTilesString());
	}

	@Override
	protected void resetGame()
	{
		tileRect.reset();
		resetValues();
		currentTileset.reset();
		updateTexts();
		startCountdown();
	}

}
