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
		restartTouchControl.setPosition((CAMERA_WIDTH - restartTouchControl.touchImage.getWidth()) / 2, CAMERA_HEIGHT - restartTouchControl.touchImage.getHeight() - REMATCH_TOUCH_PADDING);
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
