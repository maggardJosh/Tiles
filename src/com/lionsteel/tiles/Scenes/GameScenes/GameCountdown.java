package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Constants.ReflexConstants;

public class GameCountdown implements ReflexConstants
{
	TilesMainActivity		activity;

	BitmapTextureAtlas	atlas;
	TiledSprite			countdownSprite;

	GameScene			currentScene;

	public GameCountdown(GameScene scene)
	{
		activity = TilesMainActivity.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameCountdown/");
		atlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final TiledTextureRegion countdownRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(atlas, activity, "countdown.png", 0, 0, 1,3);
		atlas.load();

		countdownSprite = new TiledSprite((CAMERA_WIDTH - countdownRegion.getWidth(0)) / 2, (CAMERA_HEIGHT - countdownRegion.getHeight(0)) / 2, countdownRegion, activity.getVertexBufferObjectManager());
		countdownSprite.setScaleCenter(countdownSprite.getWidth() / 2, countdownSprite.getHeight() / 2);
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
		countdownSprite.registerEntityModifier(new ScaleModifier(COUNTDOWN_TIME, startScale, finalScale));
		countdownSprite.registerEntityModifier(new AlphaModifier(COUNTDOWN_TIME, 1.0f, 0.0f)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				countdownSprite.setCurrentTileIndex(1);
				countdownSprite.registerEntityModifier(new ScaleModifier(COUNTDOWN_TIME, startScale, finalScale));
				countdownSprite.registerEntityModifier(new AlphaModifier(COUNTDOWN_TIME, 1.0f, 0.0f)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						countdownSprite.setCurrentTileIndex(0);
						countdownSprite.registerEntityModifier(new ScaleModifier(COUNTDOWN_TIME, startScale, finalScale));
						countdownSprite.registerEntityModifier(new AlphaModifier(COUNTDOWN_TIME, 1.0f, 0.0f)
						{
							@Override
							protected void onModifierFinished(IEntity pItem)
							{
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
