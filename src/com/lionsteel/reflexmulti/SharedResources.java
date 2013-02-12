package com.lionsteel.reflexmulti;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.reflexmulti.Constants.GameMode;

public class SharedResources
{
	private ReflexActivity			activity;

	private static SharedResources	instance;

	public final TextureRegion		backgroundRegion;
	public final TextureRegion		touchImageRegion;
	public final TextureRegion		readyRegion;
	public final TextureRegion		yesRegion;
	public final TextureRegion		noRegion;
	public final TextureRegion		modeRegion[]	= new TextureRegion[6];
	public final TextureRegion		displayIndicatorRegion;
	public final Font				mFont;
	public final ITexture			fontTexture;

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

		FontFactory.setAssetBasePath("fonts/");
		fontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		mFont = FontFactory.createFromAsset(activity.getFontManager(), fontTexture, activity.getAssets(), "gameFont.ttf", 18f, true, android.graphics.Color.WHITE);
		fontTexture.load();
		mFont.load();
		
		final BuildableBitmapTextureAtlas buildableAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		//final BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SharedResources/");
		backgroundRegion = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "background.png");
		touchImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "touchImage.png");
		readyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "ready.png");
		modeRegion[GameMode.REFLEX] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "reflex.png");
		modeRegion[GameMode.NON_STOP] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "nonStop.png");
		modeRegion[GameMode.RACE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "race.png");
		modeRegion[GameMode.FREE_PLAY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "freePlay.png");
		modeRegion[GameMode.FRENZY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "frenzy.png");
		modeRegion[GameMode.TIME_ATTACK] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "timeAttack.png");
		
		
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
