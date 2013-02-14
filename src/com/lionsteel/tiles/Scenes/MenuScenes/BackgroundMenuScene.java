package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;

public class BackgroundMenuScene extends Scene
{	
	public BackgroundMenuScene(TilesMenuScene childScene)
	{
		final Sprite backgroundSprite = new Sprite(0,0,SharedResources.getInstance().backgroundRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		this.attachChild(backgroundSprite);
		this.setChildScene(childScene, false, true, true);
	}
}
