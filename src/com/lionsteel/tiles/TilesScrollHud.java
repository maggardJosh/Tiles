package com.lionsteel.tiles;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.util.modifier.ease.EaseCubicInOut;

import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.TilesScrollableScene;

public class TilesScrollHud extends HUD implements TilesConstants
{
	TilesScrollableScene	sceneToControl;
	
	final TilesMenuButton upButton;
	final TilesMenuButton downButton;

	public TilesScrollHud(final TilesScrollableScene sceneToControl)
	{
		this.sceneToControl = sceneToControl;

		final float ARROW_BUTTON_MOVEMENT = 300;
		final float BUTTON_MOVE_TIME = 2.0f;
		final float BUTTON_X_PADDING = 30;
		final float BUTTON_Y_PADDING = 200;

		upButton = new TilesMenuButton(SharedResources.getInstance().upArrowRegion, new Runnable()
		{

			@Override
			public void run()
			{
				float targetY = sceneToControl.boundY(sceneToControl.getY() + ARROW_BUTTON_MOVEMENT);

				sceneToControl.clearEntityModifiers();
				sceneToControl.registerEntityModifier(new MoveYModifier(BUTTON_MOVE_TIME, sceneToControl.getY(), targetY, EaseCubicInOut.getInstance()));
			}
		});
		this.attachChild(upButton);
		upButton.registerOwnTouchArea(this);
		upButton.setX(CAMERA_WIDTH - upButton.getWidth() - BUTTON_X_PADDING);
		upButton.setY(0 + BUTTON_Y_PADDING);

		downButton = new TilesMenuButton(SharedResources.getInstance().upArrowRegion, new Runnable()
		{

			@Override
			public void run()
			{
				float targetY = sceneToControl.boundY(sceneToControl.getY() - ARROW_BUTTON_MOVEMENT);

				sceneToControl.clearEntityModifiers();
				sceneToControl.registerEntityModifier(new MoveYModifier(BUTTON_MOVE_TIME, sceneToControl.getY(), targetY, EaseCubicInOut.getInstance()));
			}
		});
		this.attachChild(downButton);
		downButton.registerOwnTouchArea(this);
		downButton.setRotation(180);
		downButton.setX(CAMERA_WIDTH - downButton.getWidth() - BUTTON_X_PADDING);
		downButton.setY(CAMERA_HEIGHT - BUTTON_Y_PADDING);

	}
	
	public void unsetButtons()
	{
		upButton.unsetButton();
		downButton.unsetButton();
	}
}
