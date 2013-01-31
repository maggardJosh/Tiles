package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.Entities.GameButton;

public class OneTileGameScene extends GameScene implements IOnSceneTouchListener
{

	public OneTileGameScene()
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
		case GameState.WAITING_FOR_BUTTON:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			if (button.getButtonNumber() == (currentTileset.getCurrentButtonNumber() + 1))
			{
				final GameButton displayButton = currentTileset.getDisplayButton();
				displayButton.buttonSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.0f, 2.0f, EaseCubicOut.getInstance()), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 2.0f, 1.0f, EaseCubicIn.getInstance())));
				displayButton.buttonSprite.registerEntityModifier(new MoveModifier(WIN_MOVE_MOD_TIME, displayButton.buttonSprite.getX(), button.buttonSprite.getX(), displayButton.buttonSprite.getY(), button.buttonSprite.getY())
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						currentTileset.resetDisplayButton(pItem);
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
						super.onModifierFinished(pItem);
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
			if (secondsOnCurrentState >= 1)
			{
				currentTileset.newButton();
				enablePlayer(PLAYER_ONE);
				enablePlayer(PLAYER_TWO);
				changeState(GameState.WAITING_FOR_BUTTON);
			}
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
		resetBar();
		turnOffGameOver();
		changeState(GameState.PICKING_NEW_BUTTON);

	}
}
