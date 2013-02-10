package com.lionsteel.reflexmulti.Scenes.GameScenes;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuScene;

public class LoadingScene extends ReflexMenuScene implements ReflexConstants
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
		background.setColor(0, 0, 0, .7f);
		setBackgroundEnabled(false);
		final Sprite loadingSprite = new Sprite((CAMERA_WIDTH - loadingRegion.getWidth()) / 2, (CAMERA_HEIGHT - loadingRegion.getHeight()) / 2, loadingRegion, activity.getVertexBufferObjectManager());
		
		this.attachChild(background);
		this.attachChild(loadingSprite);
		
	}
	
	@Override
	public void logFlurryEvent()
	{
		//DON'T LOG LOADING SCREEN EVENTS
		//We don't care about them
	}
	
}
