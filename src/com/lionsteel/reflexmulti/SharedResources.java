package com.lionsteel.reflexmulti;

import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

public class SharedResources
{
	private ReflexActivity			activity;

	private static SharedResources	instance;

	public final TextureRegion		backgroundRegion;
	public final TextureRegion		touchImageRegion;
	public final TextureRegion		readyRegion;
	public final TextureRegion		yesRegion;
	public final TextureRegion		noRegion;
	public final TextureRegion		modeRegion[]	= new TextureRegion[3];
	public final TextureRegion		displayIndicatorRegion;

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
		backgroundRegion = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "background.png");
		touchImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "touchImage.png");
		readyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "ready.png");
		modeRegion[GameMode.REFLEX] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "reflex.png");
		modeRegion[GameMode.NON_STOP] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "nonStop.png");
		modeRegion[GameMode.RACE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "race.png");
		yesRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "yes.png");
		noRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "no.png");
		displayIndicatorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "displayIndicator.png");

		try
		{
			buildableAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			buildableAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

	}

	public static void clear()
	{
		instance = null;
	}
}
