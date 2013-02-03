package com.lionsteel.reflexmulti;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

public class LoadingScene extends Scene implements ReflexConstants
{
	ReflexActivity	activity;
	
	public LoadingScene()
	{
		activity = ReflexActivity.getInstance();
		
		final BitmapTextureAtlas sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 256);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/LoadScene/");
		
		final TextureRegion loadingRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "loading.png", 0, 0);
		
		sceneAtlas.load();
		
		Rectangle background = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		background.setColor(0,0,0,.9f);
		//setBackground(new Background(0, 0, 0, .4f));
		setBackgroundEnabled(false);
		final Sprite loadingSprite = new Sprite((CAMERA_WIDTH - loadingRegion.getWidth()) / 2, (CAMERA_HEIGHT - loadingRegion.getHeight()) / 2, loadingRegion, activity.getVertexBufferObjectManager());
		
		this.attachChild(background);
		this.attachChild(loadingSprite);
		
	}
	
}
