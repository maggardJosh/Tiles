package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.BaseClasses.GameScene.GameState;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Entities.GameButton;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public class ReflexGameScene extends GameScene
{
	protected float barSpeedIncrease;

	public ReflexGameScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

		switch (SetupScene.getDifficulty())
		{
		case Difficulty.EASY:
			barSpeedIncrease = .3f;
			break;
		case Difficulty.NORMAL:
			barSpeedIncrease = .2f;
			break;
		case Difficulty.HARD:
		case Difficulty.INSANE:
			barSpeedIncrease = .1f;
			break;
		}
		
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
				currentTileset.animateDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButtonPressed);
						addTile(button.getPlayer(), true);
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
			if (secondsOnCurrentState >= 1.0f)
			{
				currentTileset.startNonStop();
				enablePlayer(PLAYER_TWO);
				enablePlayer(PLAYER_ONE);
				changeState(GameState.WAITING_FOR_INPUT);
			}
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
