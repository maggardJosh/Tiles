package com.lionsteel.tiles.BaseClasses;

import org.andengine.entity.modifier.MoveYModifier;

public abstract class PracticeGameScene extends GameScene
{

	public PracticeGameScene()
	{
		super();
		this.detachChild(barSprite);
		this.detachChild(playerTwoIntro);
		
		gameCountdown.countdownSprite.setRotation(-90);
		pauseScene.setTwoPlayerMode(false);
		
		currentTileset.detachPlayerTwo();
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{

		switch (gameState)
		{
		case GameState.INTRO:
			if(playerOneReady)
			{
				playerOneIntro.registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerOneIntro.getY(), CAMERA_HEIGHT));
				startAnimateIn();
			}
			break;
		}
	}

}
