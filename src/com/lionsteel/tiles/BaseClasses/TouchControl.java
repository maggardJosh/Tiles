package com.lionsteel.tiles.BaseClasses;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TouchControl extends Entity implements TilesConstants
{
	public final Sprite	touchImage;
	public final Text	readyText;

	boolean				isPressed	= false;
	final Runnable		action;
	final Runnable		resetAction;
	int					pointerID	= -1;
	final float			READY_ALPHA	= .3f;

	public TouchControl(final String readyTextValue, final Runnable action, final Runnable resetAction)
	{
		this.action = action;
		this.resetAction = resetAction;
		readyText = new Text(0,0,SharedResources.getInstance().mFont,readyTextValue, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		readyText.setAlpha(READY_ALPHA);
		touchImage = new Sprite(0, 0, SharedResources.getInstance().touchImageRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager())
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
								readyText.registerEntityModifier(new AlphaModifier(TOUCH_CONTROL_DURATION, readyText.getAlpha(), 1.0f)
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
					} else if (pSceneTouchEvent.isActionMove())
					{
						if (pTouchAreaLocalX > 0 && pTouchAreaLocalX < touchImage.getWidth() && pTouchAreaLocalY > 0 && pTouchAreaLocalY < touchImage.getHeight())
						{
							isPressed = true;
							pointerID = pSceneTouchEvent.getPointerID();
							touchImage.clearEntityModifiers();
							touchImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_DURATION, touchImage.getScaleX(), 1.6f)
							{
								@Override
								protected void onModifierFinished(IEntity pItem)
								{
									readyText.registerEntityModifier(new AlphaModifier(TOUCH_CONTROL_DURATION, readyText.getAlpha(), 1.0f)
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
		this.attachChild(readyText);
	}

	public void setPosition(float pX, float pY)
	{
		this.setRotationCenter(pX + touchImage.getWidth() / 2, pY + touchImage.getHeight() / 2);
		touchImage.setPosition(pX, pY);
		readyText.setPosition(pX + touchImage.getWidth() / 2 - readyText.getWidth() / 2, pY - 80);
	}

	public void initButton()
	{
		touchImage.clearEntityModifiers();
		readyText.clearEntityModifiers();
		touchImage.setScale(1.0f);
		readyText.setAlpha(READY_ALPHA);
	}

	public void resetButton()
	{
		touchImage.clearEntityModifiers();
		readyText.clearEntityModifiers();
		isPressed = false;
		if (resetAction != null)
			resetAction.run();
		touchImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_RESET, touchImage.getScaleX(), 1.0f));
		readyText.registerEntityModifier(new AlphaModifier(TOUCH_CONTROL_RESET, readyText.getAlpha(), READY_ALPHA));
	}
}
