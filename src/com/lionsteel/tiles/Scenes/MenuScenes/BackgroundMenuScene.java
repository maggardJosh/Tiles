package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.TilesConstants;

public class BackgroundMenuScene extends Scene implements TilesConstants
{
	final Sprite	backgroundSprite;
	final float		moveByAmount;
	
	public BackgroundMenuScene(TilesMenuScene childScene)
	{
		backgroundSprite = new Sprite(0, 0, SharedResources.getInstance().backgroundRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		moveByAmount = (backgroundSprite.getWidth() - CAMERA_WIDTH) / (MAX_NUM_MENU_SCENES-1);
		this.attachChild(backgroundSprite);
		this.setChildScene(childScene, false, false, true);
	}
	
	public void moveBackground(boolean moveToRight)
	{
		if (moveToRight)
		{
			if (backgroundSprite.getX() + moveByAmount <= 0)
				backgroundSprite.registerEntityModifier(new MoveByModifier(SCENE_TRANSITION_SECONDS, moveByAmount, 0));
			else
				backgroundSprite.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, backgroundSprite.getX(), 0));
		} else
		{
			if (backgroundSprite.getX() - CAMERA_WIDTH - moveByAmount >= -backgroundSprite.getWidth())
				backgroundSprite.registerEntityModifier(new MoveByModifier(SCENE_TRANSITION_SECONDS, -moveByAmount, 0));
			else
				backgroundSprite.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, backgroundSprite.getX(), CAMERA_WIDTH - backgroundSprite.getWidth()));
		}
	}
}
