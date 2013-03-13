package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.tiles.TilesMainActivity;

public class SplashScene extends Scene
{
	private TilesMainActivity	activity;
	final BitmapTextureAtlas	sceneAtlas;
	final Sprite				backgroundSprite;

	private boolean				isFadingOut	= false;
	private Runnable			fadeOutRunnable;

	public SplashScene()
	{

		activity = TilesMainActivity.getInstance();

		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 1024);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SplashScene/");

		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "splashBackground.png", 0, 0);

		sceneAtlas.load();

		backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());

		this.attachChild(backgroundSprite);

	}

	final float SECONDS_TO_FADE = 1.0f;
	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		if (isFadingOut && activity.isReadyToFadeOut())
		{
			if(pSecondsElapsed<.5)
				backgroundSprite.setAlpha(backgroundSprite.getAlpha() - pSecondsElapsed / SECONDS_TO_FADE);
			if (backgroundSprite.getAlpha() <= 0)
			{
				backgroundSprite.setAlpha(0.0f);
				if (fadeOutRunnable != null)
					fadeOutRunnable.run();
			}
		}
		super.onManagedUpdate(pSecondsElapsed);
	}

	public void fadeOut(final Runnable fadeOutAction)
	{
		isFadingOut = true;
		fadeOutRunnable = fadeOutAction;
	}
}
