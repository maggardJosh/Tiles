package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Constants.TilesConstants;

public class GameCountdown implements TilesConstants
{
	TilesMainActivity		activity;

	BitmapTextureAtlas	atlas;
	public TiledSprite			countdownSprite;

	GameScene			currentScene;
	
	public void dispose()
	{
		atlas.unload();
		countdownSprite.dispose();
	}

	public GameCountdown(GameScene scene)
	{
		activity = TilesMainActivity.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameCountdown/");
		atlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);
		final TiledTextureRegion countdownRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(atlas, activity, "countdown.png", 0, 0, 1,3);
		atlas.load();

		countdownSprite = new TiledSprite((CAMERA_WIDTH - countdownRegion.getWidth(0)) / 2, (CAMERA_HEIGHT - countdownRegion.getHeight(0)) / 2, countdownRegion, activity.getVertexBufferObjectManager());
		countdownSprite.setScaleCenter(countdownSprite.getWidth() / 2, countdownSprite.getHeight() / 2);
		countdownSprite.setRotationCenter(countdownSprite.getWidth() / 2, countdownSprite.getHeight() / 2);
		countdownSprite.setVisible(false);
		countdownSprite.setZIndex(FOREGROUND_Z);
		
		this.currentScene = scene;
		
		currentScene.attachChild(countdownSprite);
	}

	public void startCountdown(final Runnable onFinishedAction)
	{
		countdownSprite.setCurrentTileIndex(2);
		countdownSprite.setVisible(true);
		countdownSprite.setAlpha(1.0f);
		final float startScale = 1.0f;
		countdownSprite.setScale(startScale);
		final float finalScale = 3.0f;
		SharedResources.getInstance().countdownHit.play();
		countdownSprite.registerEntityModifier(new ScaleModifier(COUNTDOWN_TIME, startScale, finalScale));
		countdownSprite.registerEntityModifier(new AlphaModifier(COUNTDOWN_TIME, 1.0f, 0.0f)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				SharedResources.getInstance().countdownHit.play();
				countdownSprite.setCurrentTileIndex(1);
				countdownSprite.registerEntityModifier(new ScaleModifier(COUNTDOWN_TIME, startScale, finalScale));
				countdownSprite.registerEntityModifier(new AlphaModifier(COUNTDOWN_TIME, 1.0f, 0.0f)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						SharedResources.getInstance().countdownHit.play();
						countdownSprite.setCurrentTileIndex(0);
						countdownSprite.registerEntityModifier(new ScaleModifier(COUNTDOWN_TIME, startScale, finalScale));
						countdownSprite.registerEntityModifier(new AlphaModifier(COUNTDOWN_TIME, 1.0f, 0.0f)
						{
							@Override
							protected void onModifierFinished(IEntity pItem)
							{
								SharedResources.getInstance().countdownFinalHit.setRate(1.0f);
								SharedResources.getInstance().countdownFinalHit.play();
								onFinishedAction.run();
								super.onModifierFinished(pItem);
							}
						});
						super.onModifierFinished(pItem);
					}
				});
				super.onModifierFinished(pItem);
			}
		});

	}
}
