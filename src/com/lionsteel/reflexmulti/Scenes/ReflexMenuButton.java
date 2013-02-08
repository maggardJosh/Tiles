package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public class ReflexMenuButton extends Entity implements ReflexConstants
{
	private Sprite	buttonSprite;

	private int		mPointerID	= -1;

	public ReflexMenuButton(final TextureRegion buttonRegion, final Runnable action)
	{
		buttonSprite = new Sprite(0, 0, buttonRegion, ReflexActivity.getInstance().getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
				case TouchEvent.ACTION_DOWN:
					mPointerID = pSceneTouchEvent.getPointerID();
					buttonSprite.setAlpha(.8f);
					break;
				case TouchEvent.ACTION_MOVE:
					if (pTouchAreaLocalX < 0 || pTouchAreaLocalY < 0 || pTouchAreaLocalX > buttonSprite.getWidth() || pTouchAreaLocalY > buttonSprite.getHeight())
					{
						mPointerID = -1;
						buttonSprite.setAlpha(1.0f);
					}
					break;
				case TouchEvent.ACTION_UP:
					if (pSceneTouchEvent.getPointerID() == mPointerID)
					{
						buttonSprite.setAlpha(1.0f);
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

	public void center(float pY)
	{
		this.setPosition((CAMERA_WIDTH - buttonSprite.getWidth()) / 2, pY);
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