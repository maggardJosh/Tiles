package com.lionsteel.tiles.Entities;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimationData;
import org.andengine.entity.sprite.IAnimationData;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Constants.TilesConstants;

public class GameButton implements TilesConstants
{
	final TilesMainActivity	activity;

	public final Sprite		buttonSprite;
	private final int		playerOwner;

	private final int		buttonNumber;
	private GameScene		parent;

	public GameButton(final int buttonNumber, final Tileset tileset, final GameScene parent, final int player)
	{
		activity = TilesMainActivity.getInstance();
		this.buttonNumber = buttonNumber;
		this.parent = parent;
		playerOwner = player;
		
		ITiledTextureRegion tiledRegion= tileset.getTiledButtonRegion(buttonNumber);
		if(tiledRegion != null)
		{
			
			buttonSprite = new AnimatedSprite(0, 0, tiledRegion, activity.getVertexBufferObjectManager())
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
						onTouched();
					return false;
				}
			};

					((AnimatedSprite)buttonSprite).animate(TILE_ANIMATE_LENGTH);
					((AnimatedSprite)buttonSprite).setCurrentFrameIndex((int)(Math.random()*(float)tiledRegion.getTileCount()));
					
			
		}else{
			buttonSprite = new Sprite(0,0,tileset.getButtonRegion(buttonNumber), activity.getVertexBufferObjectManager())
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
						onTouched();
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
		}
	}

	public void clear()
	{
		buttonSprite.detachSelf();
	}

	public void setParent(GameScene parent)
	{
		this.parent = parent;
	}

	public int getButtonNumber()
	{
		return buttonNumber;
	}

	public int getPlayer()
	{
		return playerOwner;
	}

	public float getX()
	{
		return buttonSprite.getX();
	}

	final float	PULSE_TIME	= .5f;
	final float	PULSE_SCALE	= 3.0f;

	private void pulseButton()
	{
		SharedResources.getInstance().buttonTouchSound.play();
		buttonSprite.registerEntityModifier(new ScaleModifier(PULSE_TIME, PULSE_SCALE, 1.0f, EaseCubicOut.getInstance())
		{
			@Override
			protected void onModifierStarted(IEntity pItem)
			{
				buttonSprite.setZIndex(BUTTON_Z);
				super.onModifierStarted(pItem);
			}
		});
		buttonSprite.setZIndex(BUTTON_Z + 1);
		parent.sortChildren();
	}

	private void onTouched()
	{
		if (parent.buttonPressed(this))
			pulseButton();
	}

	public float getY()
	{
		return buttonSprite.getY();
	}

}
