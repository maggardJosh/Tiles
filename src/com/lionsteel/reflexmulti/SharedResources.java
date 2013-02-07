package com.lionsteel.reflexmulti;

import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.reflexmulti.SetupScene.GameMode;

public class SharedResources
{
	private ReflexActivity			activity;
	
	private static SharedResources	instance;
	
	public final TextureRegion		backgroundRegion;
	public final TextureRegion		touchImageRegion;
	public final TextureRegion		readyRegion;
	public final TextureRegion		modeRegion[]	= new TextureRegion[3];
	
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
		final BuildableBitmapTextureAtlas buildableAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		//final BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SharedResources/");
		backgroundRegion = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "background.png");// buildableAtlas, activity, "background.png", 0, 0);
		touchImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "touchImage.png");//, (int) backgroundRegion.getWidth() + 3, 0);
		readyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "ready.png");//, (int) touchImageRegion.getTextureX(), (int) (touchImageRegion.getTextureY() + touchImageRegion.getHeight()) + 2);
		modeRegion[GameMode.REFLEX] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "reflex.png");//, (int) touchImageRegion.getTextureX(), (int) (readyRegion.getTextureY() + readyRegion.getHeight()));
		modeRegion[GameMode.NON_STOP] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "nonStop.png");//, (int) touchImageRegion.getTextureX(), (int) (modeRegion[GameMode.REFLEX].getTextureY() + modeRegion[GameMode.REFLEX].getHeight()));
		modeRegion[GameMode.RACE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "race.png");//, (int) touchImageRegion.getTextureX(), (int) (modeRegion[GameMode.NON_STOP].getTextureY() + modeRegion[GameMode.NON_STOP].getHeight()));
		
		try
		{	
			buildableAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			buildableAtlas.load();// load(activity.getTextureManager());
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		//atlas.load();
		
	}
	
	public static void clear()
	{
		instance = null;
	}
}
