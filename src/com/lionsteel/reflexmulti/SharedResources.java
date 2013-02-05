package com.lionsteel.reflexmulti;

import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

public class SharedResources
{
	private ReflexActivity			activity;
	
	private static SharedResources	instance;
	
	public TextureRegion			backgroundRegion;
	
	public static SharedResources getInstance()
	{
		if (instance == null)
			instance = new SharedResources();
		return instance;
	}
	
	public SharedResources()
	{
		instance = this;
		activity = ReflexActivity.getInstance();
		final BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 1024);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SharedResources/");
		backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "background.png", 0, 0);
		atlas.load();
		
	}

	public static void clear()
	{
		instance = null;
		
	}
}
