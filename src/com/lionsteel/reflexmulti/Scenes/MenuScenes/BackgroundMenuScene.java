package com.lionsteel.reflexmulti.Scenes.MenuScenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SharedResources;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuScene;

public class BackgroundMenuScene extends Scene
{	
	public BackgroundMenuScene(ReflexMenuScene childScene)
	{
		final Sprite backgroundSprite = new Sprite(0,0,SharedResources.getInstance().backgroundRegion, ReflexActivity.getInstance().getVertexBufferObjectManager());
		this.attachChild(backgroundSprite);
		this.setChildScene(childScene, false, true, true);
	}
}
