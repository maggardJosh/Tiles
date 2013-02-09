package com.lionsteel.reflexmulti.Entities.TouchControls;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.SharedResources;

import android.sax.StartElementListener;

public class TouchControl extends Entity implements ReflexConstants
{
	public final Sprite	touchImage;
	public final Sprite	readyImage;

	boolean				isPressed	= false;
	final Runnable		action;
	final Runnable		resetAction;
	int					pointerID	= -1;
	final float			READY_ALPHA	= .3f;

	public TouchControl(final Runnable action, final Runnable resetAction, final TextureRegion readyRegion)
	{
		this.action = action;
		this.resetAction = resetAction;
		readyImage = new Sprite(0, 0, readyRegion, ReflexActivity.getInstance().getVertexBufferObjectManager());
		readyImage.setAlpha(READY_ALPHA);
		touchImage = new Sprite(0, 0, SharedResources.getInstance().touchImageRegion, ReflexActivity.getInstance().getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
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
								readyImage.registerEntityModifier(new AlphaModifier(TOUCH_CONTROL_DURATION, readyImage.getAlpha(), 1.0f)
								{
									protected void onModifierFinished(IEntity pItem)
									{
										if (action != null)
											action.run();
									};
								});
								super.onModifierFinished(pItem);
							}
						});
					}
				} else
				{
					if (pSceneTouchEvent.isActionMove())
					{
						if (pTouchAreaLocalX < 0 || pTouchAreaLocalX > touchImage.getWidth() || pTouchAreaLocalY < 0 || pTouchAreaLocalY > touchImage.getHeight())
						{

							resetButton();
						}
					} else if (pSceneTouchEvent.isActionUp())
					{
						resetButton();
					}
				}

				return true;
			}
		};
		touchImage.setScaleCenter(touchImage.getWidth() / 2, touchImage.getHeight() / 2);
		this.setRotationCenter(touchImage.getWidth() / 2, touchImage.getHeight() / 2);
		this.attachChild(touchImage);
		this.attachChild(readyImage);
	}

	public void setPosition(float pX, float pY)
	{
		this.setRotationCenter(pX + touchImage.getWidth() / 2, pY + touchImage.getHeight() / 2);
		touchImage.setPosition(pX, pY);
		readyImage.setPosition(pX + touchImage.getWidth() / 2 - readyImage.getWidth() / 2, pY - 80);
	}
	
	public void initButton()
	{
		touchImage.setScale(1.0f);
		readyImage.setAlpha(READY_ALPHA);
	}

	public void resetButton()
	{
		touchImage.clearEntityModifiers();
		readyImage.clearEntityModifiers();
		isPressed = false;
		if (resetAction != null)
			resetAction.run();
		touchImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_RESET, touchImage.getScaleX(), 1.0f));
		readyImage.registerEntityModifier(new AlphaModifier(TOUCH_CONTROL_RESET, readyImage.getAlpha(), READY_ALPHA));
	}
}
