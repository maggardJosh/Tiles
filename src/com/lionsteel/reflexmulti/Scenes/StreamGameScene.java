package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.Entities.GameButton;

public class StreamGameScene extends GameScene implements IOnSceneTouchListener
{

	public StreamGameScene()
	{
		super();
		activity = ReflexActivity.getInstance();

		this.setOnSceneTouchListener(this);

		this.sortChildren();

	}

	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_INPUT:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			GameButton displayButtonPressed = currentTileset.isButtonDisplayed(button.getButtonNumber()-1);
			if (displayButtonPressed != null)
			{
				currentTileset.animateDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(pItem);
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
						// TODO Auto-generated method stub

					}
				});
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
			currentTileset.startStream();
			changeState(GameState.WAITING_FOR_INPUT);
			break;
		}
		super.Update(pSecondsElapsed);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		switch (gameState)
		{
		case GameState.GAME_OVER:
			resetGame();
			break;
		}
		return false;
	}

	private void resetGame()
	{
		currentTileset.reset();
		resetBar();
		turnOffGameOver();
		changeState(GameState.PICKING_NEW_BUTTON);

	}
}
