package com.lionsteel.reflexmulti.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.SequenceModifier;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

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
			playerTileCountTexts[i].setAlpha(0);
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
	
	private void fadeInCounter()
	{
		for(Text tileCount : playerTileCountTexts)
			tileCount.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f));
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
		case GameState.PICKING_NEW_BUTTON:
			currentTileset.startRace();
			fadeInCounter();
			changeState(GameState.WAITING_FOR_INPUT);
			startTimer();
			break;
		}
		super.Update(pSecondsElapsed);
	}
	final float RACE_SECONDS = 30.0f;
	private void startTimer()
	{
		final float currentScale = barSprite.getScaleY();
		if(currentScale < .01f)
		{
			if(playerTileCount[PLAYER_ONE] > playerTileCount[PLAYER_TWO])
				gameOverScreen.show(PLAYER_ONE);
			else
				if(playerTileCount[PLAYER_TWO]>playerTileCount[PLAYER_ONE])
					gameOverScreen.show(PLAYER_TWO);
			return;
		}
		barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(.5f, 1.0f, 2.0f, currentScale, currentScale-(1.0f/RACE_SECONDS)*.5f, EaseCubicIn.getInstance()), new ScaleModifier(.5f, 2.0f, 1.0f, currentScale-(1.0f/RACE_SECONDS)/2, currentScale-(1.0f/RACE_SECONDS), EaseCubicOut.getInstance())){
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				startTimer();
				super.onModifierFinished(pItem);
			}
		});
		
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
