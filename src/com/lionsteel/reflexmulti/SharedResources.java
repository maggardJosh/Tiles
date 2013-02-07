package com.lionsteel.reflexmulti;

import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.SetupScene.GameMode;

public class SharedResources
{
	private ReflexActivity			activity;
	
	private static SharedResources	instance;
	
	public final TextureRegion			backgroundRegion;
	public final TextureRegion			touchImageRegion;
	public final TextureRegion			readyRegion;
	public final TextureRegion			modeRegion[]	= new TextureRegion[3];
	
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
		final BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SharedResources/");
		backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "background.png", 0, 0);
		touchImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "touchImage.png", (int) backgroundRegion.getWidth() + 1, 0);
		readyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "ready.png", (int) touchImageRegion.getTextureX(), (int) (touchImageRegion.getTextureY() + touchImageRegion.getHeight()) + 1);
		modeRegion[GameMode.REFLEX] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "reflex.png", (int) touchImageRegion.getTextureX(), (int) (touchImageRegion.getTextureY() + touchImageRegion.getHeight()));
		modeRegion[GameMode.NON_STOP] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "nonStop.png", (int) touchImageRegion.getTextureX(), (int) (modeRegion[GameMode.REFLEX].getTextureY() + modeRegion[GameMode.REFLEX].getHeight()));
		modeRegion[GameMode.RACE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "race.png", (int) touchImageRegion.getTextureX(), (int) (modeRegion[GameMode.NON_STOP].getTextureY() + modeRegion[GameMode.NON_STOP].getHeight()));
		
		atlas.load();
		
	}
	
	public static void clear()
	{
		instance = null;
	}
}
