package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.tiles.TilesMainActivity;

public class SplashScene extends Scene
{
	private TilesMainActivity		activity;
	final BitmapTextureAtlas	sceneAtlas;
	final Sprite				backgroundSprite;
	
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
	
	public void fadeOut(final IEntityModifierListener listener)
	{
		activity.runOnUpdateThread(new Runnable(){
			@Override
			public void run()
			{
				backgroundSprite.registerEntityModifier(new AlphaModifier(1.0f, 1.0f, 0, listener));
				
			}
		});
		
	}
}
