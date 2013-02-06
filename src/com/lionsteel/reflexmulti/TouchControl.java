package com.lionsteel.reflexmulti;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.util.Log;

public class TouchControl extends Entity implements ReflexConstants
{
	public final Sprite	touchImage;
	boolean				isPressed	= false;
	final Runnable		action;
	final Runnable		resetAction;
	int					pointerID	= -1;
	
	public TouchControl(final Runnable action, final Runnable resetAction)
	{
		this.action = action;
		this.resetAction = resetAction;
		
		touchImage = new Sprite(0, 0, SharedResources.getInstance().touchImageRegion, ReflexActivity.getInstance().getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (!isPressed)
				{
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
					{
						isPressed = true;
						pointerID = pSceneTouchEvent.getPointerID();
						touchImage.clearEntityModifiers();
						touchImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_DURATION, touchImage.getScaleX(), 1.6f)
						{
							@Override
							protected void onModifierFinished(IEntity pItem)
							{
								action.run();
								super.onModifierFinished(pItem);
							}
						});
					}
				}else{
					if(pSceneTouchEvent.isActionMove())
					{
						if(pTouchAreaLocalX < -mWidth || pTouchAreaLocalX > 2*mWidth || pTouchAreaLocalY < -mHeight || pTouchAreaLocalY > mHeight*2)
							resetButton();
					}else if(pSceneTouchEvent.isActionUp())
					{
						resetButton();
					}
				}
				
				return true;
			}
		};
		touchImage.setScaleCenter(touchImage.getWidth() / 2, touchImage.getHeight() / 2);
	}
	
	private void resetButton()
	{
		isPressed = false;
		resetAction.run();
		touchImage.clearEntityModifiers();
		touchImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_RESET, touchImage.getScaleX(), 1.0f));
	}
}
