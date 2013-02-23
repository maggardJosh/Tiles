package com.lionsteel.tiles.Entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicInOut;

import android.os.FileObserver;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TilesetParticleSystem extends Entity implements TilesConstants
{
	private TilesMainActivity	activity;
	BuildableBitmapTextureAtlas	atlas;
	private TextureRegion[]		particleRegion	= new TextureRegion[2];
	private Sprite[]			particleSprite	= new Sprite[2];

	public TilesetParticleSystem(String basePath)
	{
		activity = TilesMainActivity.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		boolean hasParticles = false;
		try
		{
			//check for particle image file
			final String[] files = activity.getAssets().list("gfx/tilesets/"+basePath); 
			for(String s : files)
				if(s=="particles1.png")
				{
					hasParticles= true;
					break;
				}
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		//If there are no particles... Just abort making a particle system.
		if(!hasParticles)
			return;
		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);

		for (int i = 0; i < 2; i++)
			particleRegion[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "particles" + (i + 1) + ".png");

		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		for (int i = 0; i < 2; i++)
		{
			particleSprite[i] = new Sprite(0, 0, particleRegion[i], activity.getVertexBufferObjectManager());
			particleSprite[i].setRotationCenter(particleSprite[i].getWidth() / 2, particleSprite[i].getHeight() / 2);
			attachChild(particleSprite[i]);

		}
		moveRandom(particleSprite[0]);
		particleSprite[1].registerEntityModifier(new DelayModifier(MAX_SECONDS / 2)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				moveRandom(pItem);
				super.onModifierFinished(pItem);
			}
		});

	}

	final float	MIN_X			= -CAMERA_WIDTH / 2;
	final float	MAX_X			= CAMERA_WIDTH / 2;
	final float	MIN_Y			= -CAMERA_HEIGHT / 2;
	final float	MAX_Y			= CAMERA_HEIGHT / 2;
	final float	MAX_X_MOVE		= 20;
	final float	MIN_X_MOVE		= 10;
	final float	MAX_Y_MOVE		= 20;
	final float	MIN_Y_MOVE		= 10;
	final float	MAX_ROTATION	= 60;
	final float	MIN_ROTATION	= 20;

	final float	MAX_DIFF		= 70;
	final float	MAX_SECONDS		= 12.0f;
	final float	MIN_SECONDS		= 8.0f;

	private void moveRandom(IEntity entityToMove)
	{
		float newX = MIN_X + (float) (Math.random() * (MAX_X - MIN_X));
		float xDiff = newX - entityToMove.getX();
		if (xDiff > MAX_DIFF)
			newX = entityToMove.getX() + MAX_DIFF;
		else if (xDiff < MAX_DIFF)
			newX = entityToMove.getX() - MAX_DIFF;
		float newY = MIN_Y + (float) (Math.random() * (MAX_Y - MIN_Y));
		float yDiff = newY - entityToMove.getY();
		if (yDiff > MAX_DIFF)
			newY = entityToMove.getY() + MAX_DIFF;
		else if (yDiff < MAX_DIFF)
			newY = entityToMove.getY() - MAX_DIFF;
		final float movementTime = MIN_SECONDS + (float) (Math.random() * (MAX_SECONDS - MIN_SECONDS));

		entityToMove.registerEntityModifier(new MoveModifier(movementTime, entityToMove.getX(), newX, entityToMove.getY(), newY, EaseCubicInOut.getInstance()));

		float rotation = MIN_ROTATION + (float) (Math.random() * (MAX_ROTATION - MIN_ROTATION));
		if (Math.random() < .5)
			rotation = -rotation;
		entityToMove.registerEntityModifier(new RotationModifier(movementTime, entityToMove.getRotation(), entityToMove.getRotation() + rotation, EaseCubicInOut.getInstance())
		{
			protected void onModifierFinished(IEntity pItem)
			{
				moveRandom(pItem);
			};
		});
	}

	public void clear()
	{
		atlas.unload();
		for (int i = 0; i < 2; i++)
			particleSprite[i].detachSelf();
	}

}
