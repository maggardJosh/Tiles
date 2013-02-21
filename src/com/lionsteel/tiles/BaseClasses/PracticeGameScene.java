package com.lionsteel.tiles.BaseClasses;

import org.andengine.entity.modifier.MoveYModifier;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Scenes.GameScenes.PracticeGameOverScene;

public abstract class PracticeGameScene extends GameScene
{
	
	protected PracticeGameOverScene practiceGameOverScene;

	public PracticeGameScene()
	{
		super();
		practiceGameOverScene = new PracticeGameOverScene();
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
	
	protected void showPracticeGameOver()
	{
		transitionChildScene(practiceGameOverScene);
	}
	
	public void restartGame()
	{
		this.clearChildScene();
		FlurryAgent.logEvent(FlurryAgentEventStrings.RESTART_PRACTICE);
		TilesMainActivity.startGameEvent();
		resetGame();
		currentTileset.resetPlayerTiles();
		
	}

}
