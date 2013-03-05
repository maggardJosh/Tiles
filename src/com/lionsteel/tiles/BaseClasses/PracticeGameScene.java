package com.lionsteel.tiles.BaseClasses;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.text.Text;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.SongManager;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.GameScenes.PracticeGameOverScene;

public abstract class PracticeGameScene extends GameScene
{

	protected PracticeGameOverScene	practiceGameOverScene;

	protected final Text			gameModeText;

	private final float				START_Y	= 40;

	public PracticeGameScene()
	{
		super();
		practiceGameOverScene = new PracticeGameOverScene();
		this.detachChild(barSprite);
		this.detachChild(playerTwoIntro);
		this.detachChild(playerTutorials[PLAYER_TWO]);

		gameModeText = new Text(0, 0, SharedResources.getInstance().mFont, "Game Mode", 20, activity.getVertexBufferObjectManager());
		gameModeText.setScale(GAME_MODE_SCALE);
		gameModeText.setAlpha(0);
		this.attachChild(gameModeText);
		gameModeText.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - gameModeText.getWidth()) / 2, START_Y);

		gameCountdown.countdownSprite.setRotation(-90);
		pauseScene.setTwoPlayerMode(false);

		currentTileset.detachPlayerTwo();
	}

	protected void setGameModeText(final String gameModeString)
	{
		gameModeText.setText(gameModeString);
		gameModeText.setX((CAMERA_WIDTH + PAUSE_BAR_WIDTH - gameModeText.getWidth()) / 2);
	}

	@Override
	protected void Update(float pSecondsElapsed)
	{

		switch (gameState)
		{
		case GameState.INTRO:
			if (playerOneReady)
			{
				SongManager.getInstance().fadeOut();
				playerOneIntro.registerEntityModifier(new MoveYModifier(INTRO_OUT_DURATION, playerOneIntro.getY(), CAMERA_HEIGHT));
				startAnimateIn();
				
			}
			break;
		}
	}
	
	@Override
	protected void startAnimateIn()
	{
		gameModeText.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1.0f));
		super.startAnimateIn();
	}

	protected void showPracticeGameOver()
	{
		SongManager.getInstance().fadeOut();
		SharedResources.getInstance().countdownFinalHit.setRate(GAME_OVER_HIT_RATE);
		SharedResources.getInstance().countdownFinalHit.play();
		transitionChildScene(practiceGameOverScene);
	}

	public void restartGame()
	{
		this.clearChildScene();
		SongManager.getInstance().setVolumeMultiplier(1.0f);
		FlurryAgent.logEvent(FlurryAgentEventStrings.RESTART_PRACTICE);
		resetGame();
		currentTileset.resetPlayerTiles();

	}

}
