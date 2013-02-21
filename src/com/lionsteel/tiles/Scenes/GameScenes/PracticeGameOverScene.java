package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.util.debug.Debug;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.PracticeGameScene;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.BaseClasses.TouchControl;
import com.lionsteel.tiles.Constants.TilesConstants;

public class PracticeGameOverScene extends TilesMenuScene implements TilesConstants
{
	final Text			titleText;
	final Text			holdToRestartText;

	final Text			labelOne;
	final Text			labelTwo;
	final Text			valueOne;
	final Text			valueTwo;

	final TouchControl	restartTouchControl;

	public PracticeGameOverScene()
	{
		super();
		this.setBackgroundEnabled(false);
		final Rectangle background = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		background.setColor(0, 0, 0, OVERLAY_BACKGROUND_ALPHA);
		this.attachChild(background);

		titleText = new Text(0, 0, SharedResources.getInstance().headingFont, "Results", activity.getVertexBufferObjectManager());
		titleText.setPosition((CAMERA_WIDTH - titleText.getWidth()) / 2, 100);
		this.attachChild(titleText);

		holdToRestartText = new Text(0, 0, SharedResources.getInstance().mFont, "Hold to restart", activity.getVertexBufferObjectManager());
		holdToRestartText.setPosition((CAMERA_WIDTH - holdToRestartText.getWidth()) / 2, CAMERA_HEIGHT - holdToRestartText.getHeight() - 50);
		this.attachChild(holdToRestartText);

		labelOne = new Text(0, 0, SharedResources.getInstance().mFont, "Label One",20, activity.getVertexBufferObjectManager());
		labelTwo = new Text(0, 0, SharedResources.getInstance().mFont, "Label Two",20, activity.getVertexBufferObjectManager());
		valueOne = new Text(0, 0, SharedResources.getInstance().mFont, "0",20, activity.getVertexBufferObjectManager());
		valueTwo = new Text(0, 0, SharedResources.getInstance().mFont, "0",20, activity.getVertexBufferObjectManager());


		updateLabelPositions();
		updateValuePositions();
		
		this.attachChild(labelOne);
		this.attachChild(labelTwo);
		this.attachChild(valueOne);
		this.attachChild(valueTwo);

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
		restartTouchControl.setPosition((CAMERA_WIDTH - restartTouchControl.touchImage.getWidth()) / 2, CAMERA_HEIGHT - restartTouchControl.touchImage.getHeight() - 100);
		this.attachChild(restartTouchControl);

		final TilesMenuButton quitButton = new TilesMenuButton(SharedResources.getInstance().exitGameButtonRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.onBackPressed();
			}
		});
		quitButton.setPosition(3, (CAMERA_HEIGHT - quitButton.getHeight()) / 2);
		addButton(quitButton);
	}
	
	public void setLabels(final String labelOne, final String labelTwo)
	{
		this.labelOne.setText(labelOne);
		this.labelTwo.setText(labelTwo);
		
		updateLabelPositions();
		
	}
	
	public void setValues(final String valueOne, final String valueTwo)
	{
		this.valueOne.setText(valueOne);
		this.valueTwo.setText(valueTwo);
		
		updateValuePositions();
	}
	
	private void updateLabelPositions()
	{
		this.labelOne.setPosition((CAMERA_WIDTH - this.labelOne.getWidth()) / 2, CAMERA_HEIGHT * 2 / 7);
		this.labelTwo.setPosition((CAMERA_WIDTH - this.labelTwo.getWidth()) / 2, CAMERA_HEIGHT * 3 / 7);
	}

	private void updateValuePositions()
	{
		this.valueOne.setPosition(labelOne.getX() + (labelOne.getWidth() - valueOne.getWidth()) / 2, labelOne.getY() + labelOne.getHeight() + 16);
		this.valueTwo.setPosition(labelTwo.getX() + (labelTwo.getWidth() - valueTwo.getWidth()) / 2, labelTwo.getY() + labelTwo.getHeight() + 16);
	}
	
	@Override
	public void logFlurryEvent()
	{

	}

	@Override
	public void registerTouchAreas()
	{
		registerTouchArea(restartTouchControl.touchImage);
		super.registerTouchAreas();
	}

	@Override
	public void initScene()
	{
		restartTouchControl.initButton();
	}

}
