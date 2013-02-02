package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;

public class PrototypeScene extends Scene
{	
	ReflexActivity activity;
	BitmapTextureAtlas atlas;
	
	
	public PrototypeScene()
	{
		activity = ReflexActivity.getInstance();
		atlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 1024);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "prototypeBackground.png", 0, 0);
		atlas.load();
		
		final Sprite background = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		
		this.attachChild(background);
	}
}
