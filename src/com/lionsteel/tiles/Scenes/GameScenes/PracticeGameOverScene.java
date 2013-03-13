package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.SongManager;
import com.lionsteel.tiles.BaseClasses.PracticeGameScene;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.BaseClasses.TouchControl;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public class PracticeGameOverScene extends TilesMenuScene implements TilesConstants
{
	final Text			titleText;
	final Text			holdToRestartText;

	final Text			gameModeLabel;
	final Text			difficultyLabel;
	final Text			difficultyValue;

	final Text			labelOne;
	final Text			labelTwo;
	final Text			valueOne;
	final Text			valueTwo;

	final Text			newRecordText;

	final TouchControl	restartTouchControl;

	final float			START_Y	= 80;

	public PracticeGameOverScene()
	{
		super();
		this.setBackgroundEnabled(false);
		final Rectangle background = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		background.setColor(0, 0, 0, OVERLAY_BACKGROUND_ALPHA);
		this.attachChild(background);
		background.setZIndex(BACKGROUND_Z);

		gameModeLabel = new Text(0, 0, SharedResources.getInstance().headingFont, "Time-Attack", 20, activity.getVertexBufferObjectManager());
		gameModeLabel.setScale(.9f);
		gameModeLabel.setPosition((CAMERA_WIDTH - gameModeLabel.getWidth()) / 2, START_Y);

		titleText = new Text(0, 0, SharedResources.getInstance().headingFont, "Results", activity.getVertexBufferObjectManager());
		titleText.setPosition((CAMERA_WIDTH - titleText.getWidth()) / 2, gameModeLabel.getY() + gameModeLabel.getHeight() + LABEL_SPACING);
		this.attachChild(titleText);

		holdToRestartText = new Text(0, 0, SharedResources.getInstance().mFont, "Hold to restart", activity.getVertexBufferObjectManager());
		holdToRestartText.setPosition((CAMERA_WIDTH - holdToRestartText.getWidth()) / 2, CAMERA_HEIGHT - holdToRestartText.getHeight() - 50);
		this.attachChild(holdToRestartText);

		difficultyLabel = new Text(0, 0, SharedResources.getInstance().mFont, "Difficulty", 20, activity.getVertexBufferObjectManager());
		difficultyValue = new Text(0, 0, SharedResources.getInstance().mFont, "Normal", activity.getVertexBufferObjectManager());

		labelOne = new Text(0, 0, SharedResources.getInstance().mFont, "Label One", 20, activity.getVertexBufferObjectManager());
		labelTwo = new Text(0, 0, SharedResources.getInstance().mFont, "Label Two", 20, activity.getVertexBufferObjectManager());
		valueOne = new Text(0, 0, SharedResources.getInstance().mFont, "0", 20, activity.getVertexBufferObjectManager());
		valueTwo = new Text(0, 0, SharedResources.getInstance().mFont, "0", 20, activity.getVertexBufferObjectManager());

		newRecordText = new Text(0, 0, SharedResources.getInstance().mFont, "New Record!", activity.getVertexBufferObjectManager());

		valueOne.setColor(VALUE_TEXT_COLOR);
		valueTwo.setColor(VALUE_TEXT_COLOR);
		difficultyValue.setColor(VALUE_TEXT_COLOR);

		updateTextPositions();

		this.attachChild(gameModeLabel);

		this.attachChild(difficultyLabel);
		this.attachChild(difficultyValue);

		this.attachChild(labelOne);
		this.attachChild(labelTwo);
		this.attachChild(valueOne);
		this.attachChild(valueTwo);
		this.attachChild(newRecordText);

		//Set this above because this text can bounce if it's a new record
		valueTwo.setZIndex(FOREGROUND_Z);

		restartTouchControl = new TouchControl("Ready", new Runnable()
		{
			@Override
			public void run()
			{
				if (mParentScene instanceof PracticeGameScene)
					((PracticeGameScene) mParentScene).restartGame();
				else
					Debug.e(mParentScene + " not PracticeGameScene");
			}
		}, null);
		restartTouchControl.setPosition((CAMERA_WIDTH - restartTouchControl.outerImage.getWidth()) / 2, CAMERA_HEIGHT - restartTouchControl.outerImage.getHeight() - 100);
		this.attachChild(restartTouchControl);

		final TilesMenuButton quitButton = new TilesMenuButton(SharedResources.getInstance().exitGameButtonRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.onBackPressed();
			}
		});
		quitButton.setPosition(CAMERA_WIDTH - 3 - quitButton.getWidth(), (CAMERA_HEIGHT - quitButton.getHeight()) / 2);
		addButton(quitButton);

		this.sortChildren();
	}

	public void setLabels(final String labelOne, final String labelTwo)
	{
		this.labelOne.setText(labelOne);
		this.labelTwo.setText(labelTwo);

		updateTextPositions();
	}

	public void setValues(final CharSequence valueOne, final CharSequence valueTwo)
	{
		this.gameModeLabel.setText(GameMode.getName(SetupScene.getGameMode()));
		this.difficultyValue.setText(Difficulty.getName(SetupScene.getDifficulty()));
		this.valueOne.setText(valueOne);
		this.valueTwo.setText(valueTwo);

		this.valueTwo.clearEntityModifiers();
		this.valueTwo.setScale(1.0f);
		this.valueTwo.setRotation(0);

		newRecordText.clearEntityModifiers();
		newRecordText.setVisible(false);

		updateTextPositions();
	}

	private void updateTextPositions()
	{
		this.gameModeLabel.setX((CAMERA_WIDTH - gameModeLabel.getWidth()) / 2);
		this.difficultyLabel.setPosition((CAMERA_WIDTH - difficultyLabel.getWidth()) / 2, titleText.getY() + titleText.getHeight() + LABEL_SPACING * 4);
		this.difficultyValue.setPosition((CAMERA_WIDTH - difficultyValue.getWidth()) / 2, difficultyLabel.getY() + difficultyLabel.getHeight() + LABEL_SPACING);
		this.labelOne.setPosition((CAMERA_WIDTH - this.labelOne.getWidth()) / 2, difficultyValue.getY() + difficultyValue.getHeight() + LABEL_SPACING * 2);
		this.valueOne.setPosition(labelOne.getX() + (labelOne.getWidth() - valueOne.getWidth()) / 2, labelOne.getY() + labelOne.getHeight() + LABEL_SPACING);
		this.labelTwo.setPosition((CAMERA_WIDTH - this.labelTwo.getWidth()) / 2, valueOne.getY() + valueOne.getHeight() + LABEL_SPACING * 2);
		this.valueTwo.setPosition(labelTwo.getX() + (labelTwo.getWidth() - valueTwo.getWidth()) / 2, labelTwo.getY() + labelTwo.getHeight() + LABEL_SPACING);
		this.newRecordText.setPosition((CAMERA_WIDTH-newRecordText.getWidth())/2, valueTwo.getY()+valueTwo.getHeight()+LABEL_SPACING*2);
	}

	@Override
	public void logFlurryEvent()
	{

	}

	@Override
	public void registerTouchAreas()
	{
		registerTouchArea(restartTouchControl.outerImage);
		super.registerTouchAreas();
	}

	@Override
	public void initScene()
	{
		restartTouchControl.initButton();
		
		SongManager.getInstance().setVolumeMultiplier(MUFFLED_VOLUME);
	}

	public void pulseNewRecord()
	{
		final float pulseSeconds = 1.0f;
		final float PULSE_SCALE = 1.6f;
		final float PULSE_ROTATION = 3;
		this.valueTwo.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new ScaleModifier(pulseSeconds / 2, 1.0f, PULSE_SCALE, EaseCubicOut.getInstance()), new ScaleModifier(pulseSeconds / 2, PULSE_SCALE, 1.0f, EaseCubicIn.getInstance()))));
		this.valueTwo.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(pulseSeconds, -PULSE_ROTATION, PULSE_ROTATION), new RotationModifier(pulseSeconds, PULSE_ROTATION, -PULSE_ROTATION))));

		this.newRecordText.setVisible(true);
		this.newRecordText.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new ScaleModifier(pulseSeconds / 2, 1.0f, PULSE_SCALE, EaseCubicOut.getInstance()), new ScaleModifier(pulseSeconds / 2, PULSE_SCALE, 1.0f, EaseCubicIn.getInstance()))));
		this.newRecordText.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(pulseSeconds, -PULSE_ROTATION / 4, PULSE_ROTATION / 4), new RotationModifier(pulseSeconds, PULSE_ROTATION / 4, -PULSE_ROTATION / 4))));

	}

	@Override
	protected void exitScene()
	{
		// TODO Auto-generated method stub
		
	}
}
