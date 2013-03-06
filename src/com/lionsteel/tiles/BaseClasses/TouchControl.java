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
	private final Sprite	innerImage;
	public final Sprite		outerImage;
	public final Text		readyText;

	private final float		START_SCALE		= .1f;
	private final float		FINISH_SCALE	= 1.0f;

	boolean					isPressed		= false;
	final Runnable			action;
	final Runnable			resetAction;
	int						pointerID		= -1;
	final float				READY_ALPHA		= .3f;

	public TouchControl(final String readyTextValue, final Runnable action, final Runnable resetAction)
	{
		this.action = action;
		this.resetAction = resetAction;
		readyText = new Text(0, 0, SharedResources.getInstance().mFont, readyTextValue, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		readyText.setAlpha(READY_ALPHA);

		innerImage = new Sprite(0, 0, SharedResources.getInstance().innerTouchImageRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		innerImage.setScaleCenter(innerImage.getWidth() / 2, innerImage.getHeight() / 2);
		innerImage.setScale(START_SCALE);

		outerImage = new Sprite(0, 0, SharedResources.getInstance().outerTouchImageRegion, TilesMainActivity.getInstance().getVertexBufferObjectManager())
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
						innerImage.clearEntityModifiers();
						innerImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_DURATION, innerImage.getScaleX(), FINISH_SCALE)
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
						if (pTouchAreaLocalX > 0 && pTouchAreaLocalX < innerImage.getWidth() && pTouchAreaLocalY > 0 && pTouchAreaLocalY < innerImage.getHeight())
						{
							isPressed = true;
							pointerID = pSceneTouchEvent.getPointerID();
							innerImage.clearEntityModifiers();
							innerImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_DURATION, innerImage.getScaleX(), FINISH_SCALE)
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
						if (pTouchAreaLocalX < 0 || pTouchAreaLocalX > innerImage.getWidth() || pTouchAreaLocalY < 0 || pTouchAreaLocalY > innerImage.getHeight())
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
		readyText.setPosition((outerImage.getWidth() - readyText.getWidth()) / 2, -45);

		this.setRotationCenter(outerImage.getWidth() / 2, outerImage.getHeight() / 2);

		this.attachChild(outerImage);
		this.attachChild(innerImage);
		this.attachChild(readyText);
	}

	public void setPosition(float pX, float pY)
	{
		//this.setRotationCenter(pX + outerImage.getWidth() / 2, pY + outerImage.getHeight() / 2);
		super.setPosition(pX, pY);
		//readyText.setPosition(pX + outerImage.getWidth() / 2 - readyText.getWidth() / 2, pY - 80);
	}

	public void center(float pX, float pY)
	{
		//this.setRotationCenter(pX + outerImage.getWidth() / 2, pY + outerImage.getHeight() / 2);
		super.setPosition(pX - outerImage.getWidth() / 2, pY - outerImage.getHeight() / 2);
		//readyText.setPosition(pX - readyText.getWidth() / 2, pY - 150);
	}

	public void initButton()
	{
		innerImage.clearEntityModifiers();
		readyText.clearEntityModifiers();
		innerImage.setScale(START_SCALE);
		readyText.setAlpha(READY_ALPHA);
	}

	public void resetButton()
	{
		innerImage.clearEntityModifiers();
		readyText.clearEntityModifiers();
		isPressed = false;
		if (resetAction != null)
			resetAction.run();
		innerImage.registerEntityModifier(new ScaleModifier(TOUCH_CONTROL_RESET, innerImage.getScaleX(), START_SCALE));
		readyText.registerEntityModifier(new AlphaModifier(TOUCH_CONTROL_RESET, readyText.getAlpha(), READY_ALPHA));
	}
}
