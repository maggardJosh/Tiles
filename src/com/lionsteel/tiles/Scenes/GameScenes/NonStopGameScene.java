package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Constants.ReflexConstants;
import com.lionsteel.tiles.Entities.GameButton;

public class NonStopGameScene extends GameScene implements ReflexConstants
{

	public NonStopGameScene()
	{
		super();
		activity = TilesMainActivity.getInstance();

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
						switch (button.getPlayer())
						{
						case PLAYER_ONE:
							checkPlayerWillWin(PLAYER_ONE);
							moveBar(-BAR_SPEED);
							break;
						case PLAYER_TWO:
							checkPlayerWillWin(PLAYER_TWO);
							moveBar(BAR_SPEED);
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
			currentTileset.startNonStop();
			changeState(GameState.WAITING_FOR_INPUT);
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
