package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SharedResources;

public class BackgroundMenuScene extends Scene
{	
	public BackgroundMenuScene(ReflexMenuScene childScene)
	{
		final Sprite backgroundSprite = new Sprite(0,0,SharedResources.getInstance().backgroundRegion, ReflexActivity.getInstance().getVertexBufferObjectManager());
		this.attachChild(backgroundSprite);
		this.setChildScene(childScene, false, true, true);
	}
}
