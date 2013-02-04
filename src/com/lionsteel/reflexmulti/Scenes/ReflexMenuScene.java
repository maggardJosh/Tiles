package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public abstract class ReflexMenuScene extends Scene implements ReflexConstants
{
	protected abstract void registerTouchAreas();
	
	protected abstract void deregisterTouchAreas();
	
	protected void transitionChildScene(final ReflexMenuScene childScene)
	{
		ReflexActivity.getInstance().backEnabled = false;
		setChildScene(childScene, false, false, true);
		childScene.setX(CAMERA_WIDTH);
		transitionOff();
		childScene.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, CAMERA_WIDTH, 0)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				ReflexActivity.getInstance().backEnabled = true;
				childScene.registerTouchAreas();
				super.onModifierFinished(pItem);
			}
		});
	}
	
	protected void transitionOff()
	{
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), -CAMERA_WIDTH));
	}
	
	public void setChildSceneNull()
	{
		if (this.getChildScene() instanceof ReflexMenuScene)
			((ReflexMenuScene) this.getChildScene()).clearTouchAreas();
		super.clearChildScene();
	}
	
	@Override
	public void clearChildScene()
	{
		ReflexActivity.getInstance().backEnabled = false;
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), 0));
		
		this.getChildScene().registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, this.getChildScene().getX(), CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				ReflexActivity.getInstance().backEnabled = true;
				setChildSceneNull();
				super.onModifierFinished(pItem);
			}
		});
	}
	
}
