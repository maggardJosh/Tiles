package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.Entities.GameButton;

public class OneTileGameScene extends GameScene 
{

	public OneTileGameScene()
	{
		super();
		activity = ReflexActivity.getInstance();
		
		this.sortChildren();

	}

	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_INPUT:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			if (button.getButtonNumber() == (currentTileset.getCurrentButtonNumber()))
			{
				
				final GameButton displayButton = currentTileset.getDisplayButton();
				currentTileset.animateDisplayButton(displayButton, button, new IEntityModifierListener()
				{
					
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{
					
					}
					
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButton);
						changeState(GameState.PICKING_NEW_BUTTON);
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
				});
				
				changeState(GameState.SHOWING_WIN);
			} else
			{
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
				currentTileset.newButton();
				enablePlayer(PLAYER_ONE);
				enablePlayer(PLAYER_TWO);
				changeState(GameState.WAITING_FOR_INPUT);
			}
			break;

		}
		super.Update(pSecondsElapsed);
	}

	@Override
	protected void resetGame()
	{
		resetBar();
		turnOffGameOver();
		startCountdown();
	}
}
