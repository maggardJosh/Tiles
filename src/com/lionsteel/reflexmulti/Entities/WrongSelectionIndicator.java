package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.BaseClasses.GameScene;

public class WrongSelectionIndicator implements ReflexConstants
{
	private ReflexActivity		activity;
	private BitmapTextureAtlas	atlas;
	private final TextureRegion	region;
	private final Sprite		sprite;
	private GameScene			parent;
	private final int			playerOwner;

	public WrongSelectionIndicator(final int player)
	{
		activity = ReflexActivity.getInstance();
		atlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512);
		region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "wrongIndicator.png", 0, 0);
		atlas.load();

		sprite = new Sprite(0, 0, region, activity.getVertexBufferObjectManager());
		sprite.setZIndex(FOREGROUND_Z);
		sprite.setScaleCenter(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setRotationCenter(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setVisible(false);

		this.playerOwner = player;
	}

	public void setScene(GameScene scene)
	{
		scene.attachChild(sprite);
		this.parent = scene;
	}

	public void startIndicator(float xPos, float yPos)
	{
		sprite.setPosition(xPos - sprite.getWidth() / 2, yPos - sprite.getHeight() / 2);
		sprite.setVisible(true);
		sprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(DISABLE_TIME/3, 1.2f, 1.3f), new ScaleModifier(DISABLE_TIME*2/3,  1.3f, .9f)));
		sprite.registerEntityModifier(new SequenceEntityModifier(new RotationModifier(DISABLE_TIME/2, -20f, 20f), new RotationModifier(DISABLE_TIME/2, 20, -20)));
		sprite.registerEntityModifier(new AlphaModifier(DISABLE_TIME, 1.0f, 0)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				parent.enablePlayer(playerOwner);
				super.onModifierFinished(pItem);
			}
		});
	}
}
