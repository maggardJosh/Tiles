package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.GameButton;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public class NonStopGameScene extends GameScene implements TilesConstants
{
	protected float	barSpeedIncrease;

	public NonStopGameScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

		switch (SetupScene.getDifficulty())
		{
		case Difficulty.EASY:
			barSpeedIncrease = .15f;
			break;
		case Difficulty.NORMAL:
			barSpeedIncrease = .1f;
			break;
		case Difficulty.HARD:
		case Difficulty.INSANE:
			barSpeedIncrease = .05f;
			break;
		}

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
						switch (button.getPlayer())
						{
						case PLAYER_TWO:
							moveBar(-BAR_SPEED*barSpeedMulti);
							barSpeedMulti += barSpeedIncrease;
							break;
						case PLAYER_ONE:
							moveBar(BAR_SPEED*barSpeedMulti);
							barSpeedMulti += barSpeedIncrease;
							break;
						}
					}

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{

					}
				});

				sortChildren();
			} else
			{
				if (!currentTileset.isButtonVisible(button.getButtonNumber()))
				{
					breakStreak(button.getPlayer());
					disablePlayer(button);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.PICKING_NEW_BUTTON:
			currentTileset.startNonStop();
			changeState(GameState.WAITING_FOR_INPUT);
			checkBar();
			break;
		case GameState.WAITING_FOR_INPUT:
			checkBar();
			break;
		}
		super.Update(pSecondsElapsed);
	}

	@Override
	protected void resetGame()
	{

		currentTileset.reset();
		resetBar();
		startCountdown();

	}
}
