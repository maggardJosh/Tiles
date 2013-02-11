package com.lionsteel.reflexmulti.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SharedResources;
import com.lionsteel.reflexmulti.Entities.GameButton;

public class RaceGameScene extends ReflexGameScene
{
	private int[]			playerTileCount			= new int[2];
	private final Text[]	playerTileCountTexts	= new Text[2];

	public RaceGameScene()
	{
		super();
		activity = ReflexActivity.getInstance();

		for (int i = 0; i < 2; i++)
		{
			playerTileCount[i] = 0;
			playerTileCountTexts[i] = new Text(0, 0, SharedResources.getInstance().mFont, "0",3, activity.getVertexBufferObjectManager());
			
			this.attachChild(playerTileCountTexts[i]);
		}
		playerTileCountTexts[PLAYER_TWO].setPosition((CAMERA_WIDTH+BAR_WIDTH-playerTileCountTexts[PLAYER_ONE].getWidth())/2, (CAMERA_HEIGHT)/2 + playerTileCountTexts[PLAYER_ONE].getHeight());
		playerTileCountTexts[PLAYER_ONE].setPosition((CAMERA_WIDTH+BAR_WIDTH-playerTileCountTexts[PLAYER_TWO].getWidth())/2, (CAMERA_HEIGHT)/2 - playerTileCountTexts[PLAYER_ONE].getHeight()*2);
		playerTileCountTexts[PLAYER_ONE].setRotation(180);		
		
		this.sortChildren();

	}

	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
		case GameState.WAITING_FOR_INPUT:
			if (checkPlayerDisabled(button.getPlayer()))
				return;
			final GameButton displayButtonPressed = currentTileset.isRaceButtonCurrentlyActive(button);
			if (displayButtonPressed != null)
			{
				currentTileset.animateDisplayButton(displayButtonPressed, button, new IEntityModifier.IEntityModifierListener()
				{

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						currentTileset.resetDisplayButton(displayButtonPressed);
						playerTileCount[button.getPlayer()]++;
						playerTileCountTexts[button.getPlayer()].setText(""+playerTileCount[button.getPlayer()]);
						playerTileCountTexts[button.getPlayer()].setX((CAMERA_WIDTH+BAR_WIDTH-playerTileCountTexts[button.getPlayer()].getWidth())/2);
						switch (button.getPlayer())
						{
						case PLAYER_ONE:
							//checkPlayerWillWin(PLAYER_ONE);
							//moveBar(-BAR_SPEED);
							break;
						case PLAYER_TWO:
							//checkPlayerWillWin(PLAYER_TWO);
							//moveBar(BAR_SPEED);
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
			currentTileset.startRace();
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
		turnOffGameOver();
		startCountdown();

	}
}
