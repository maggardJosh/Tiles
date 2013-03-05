package com.lionsteel.tiles.BaseClasses;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TilesMenuButton extends Entity implements TilesConstants
{
	private Sprite	buttonSprite;

	private int		mPointerID	= -1;

	final Runnable	action;

	public TilesMenuButton(final TextureRegion buttonRegion, final Runnable action)
	{
		this.action = action;
		buttonSprite = new Sprite(0, 0, buttonRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
				case TouchEvent.ACTION_DOWN:
					mPointerID = pSceneTouchEvent.getPointerID();
					buttonSprite.setColor(.8f, .8f, .8f);
					return true;
				case TouchEvent.ACTION_MOVE:
					if (pTouchAreaLocalX < 0 || pTouchAreaLocalY < 0 || pTouchAreaLocalX > buttonSprite.getWidth() || pTouchAreaLocalY > buttonSprite.getHeight())
					{
						unsetButton();
					}
					break;
				case TouchEvent.ACTION_UP:
					if (pSceneTouchEvent.getPointerID() == mPointerID)
					{
						unsetButton();
						SharedResources.getInstance().menuBlip.play();
						runAction();
					}
					break;
				}
				return false;
			}
		};
		this.attachChild(buttonSprite);
		this.setRotationCenter(buttonSprite.getWidth()/2, buttonSprite.getHeight()/2);
	}
	
	public void unsetButton()
	{
		mPointerID = -1;
		buttonSprite.setColor(1.0f, 1.0f, 1.0f);
	}

	@Override
	public void setAlpha(float pAlpha)
	{
		buttonSprite.setAlpha(pAlpha);
		super.setAlpha(pAlpha);
	}

	@Override
	public void registerEntityModifier(IEntityModifier pEntityModifier)
	{
		buttonSprite.registerEntityModifier(pEntityModifier);
		super.registerEntityModifier(pEntityModifier);
	}

	protected void runAction()
	{
		action.run();
	}

	public void clearButtonChildren()
	{
		this.detachChildren();
		this.attachChild(buttonSprite);
	}

	public void center(float pY)
	{
		this.setPosition((CAMERA_WIDTH - buttonSprite.getWidth()) / 2, pY);
	}

	public float getBottom()
	{
		return this.getY() + buttonSprite.getHeight();
	}

	public float getWidth()
	{
		return buttonSprite.getWidth();
	}

	public float getHeight()
	{
		return buttonSprite.getHeight();
	}

	public void registerOwnTouchArea(Scene scene)
	{
		scene.registerTouchArea(buttonSprite);
	}
	
	public void unregisterOwnTouchArea(Scene scene)
	{
		scene.unregisterTouchArea(buttonSprite);
	}

}
