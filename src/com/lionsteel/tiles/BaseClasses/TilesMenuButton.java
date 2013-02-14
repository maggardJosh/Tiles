package com.lionsteel.tiles.BaseClasses;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TilesMenuButton extends Entity implements TilesConstants
{
	private Sprite	buttonSprite;

	private int		mPointerID	= -1;

	public TilesMenuButton(final TextureRegion buttonRegion, final Runnable action)
	{
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
					break;
				case TouchEvent.ACTION_MOVE:
					if (pTouchAreaLocalX < 0 || pTouchAreaLocalY < 0 || pTouchAreaLocalX > buttonSprite.getWidth() || pTouchAreaLocalY > buttonSprite.getHeight())
					{
						mPointerID = -1;
						buttonSprite.setColor(1.0f, 1.0f, 1.0f);
					}
					break;
				case TouchEvent.ACTION_UP:
					if (pSceneTouchEvent.getPointerID() == mPointerID)
					{
						buttonSprite.setColor(1.0f, 1.0f, 1.0f);
						mPointerID = -1;
						action.run();
					}
					break;
				}
				return true;
			}
		};
		this.attachChild(buttonSprite);
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

}
