package com.lionsteel.tiles.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseCubicInOut;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TimerRect extends Entity implements TilesConstants
{

	private final Color		baseColor			= new Color(0, .5f, 0);
	private final Color		highlightColor		= new Color(0, 1.0f, 0);
	private final Color		hurryBaseColor		= new Color(.5f, 0, 0);
	private final Color		hurryHighlightColor	= new Color(1.0f, 0, 0);

	private final Rectangle	timerRect;

	private final Text		countText;

	private float			value;
	private final float		maxValue;
	private final Runnable	endAction;

	private final float		RECT_ALPHA			= .7f;

	public TimerRect(final float maxValue, final Runnable endAction)
	{
		this.maxValue = maxValue;
		this.endAction = endAction;

		timerRect = new Rectangle(TILE_BASE_RIGHT_SIDE, 0, CAMERA_WIDTH - TILE_BASE_RIGHT_SIDE, CAMERA_HEIGHT, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		this.attachChild(timerRect);
		countText = new Text(0, 0, SharedResources.getInstance().mFont, String.format("%02d", (int) maxValue), 3, TilesMainActivity.getInstance().getVertexBufferObjectManager());

		initCountText();

		reset();
		timerRect.setAlpha(0);
		countText.setAlpha(0);
	}

	public void fadeIn()
	{
		timerRect.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, RECT_ALPHA));
		countText.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1));
		countText.registerEntityModifier(new ScaleModifier(TILE_BASE_ANIMATE_IN / 2, 10.0f, 1.0f));
	}

	private void initCountText()
	{
		countText.setRotationCenter(countText.getWidth() / 2, countText.getHeight() / 2);
		countText.setScaleCenter(countText.getWidth() / 2, countText.getHeight() / 2);
		countText.setPosition(timerRect.getX() + (timerRect.getWidth() - countText.getWidth()) / 2, timerRect.getY() + (timerRect.getHeight() - countText.getHeight()) / 2);
		countText.setRotation(90);
		countText.setAlpha(0);
		this.attachChild(countText);
	}

	public void decrement()
	{
		value += 1.0f;
		timerRect.clearEntityModifiers();
		timerRect.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 1.0f, timerRect.getScaleY(), 1.0f - (value / maxValue), EaseCubicInOut.getInstance()));
		if (maxValue - value > 5)
			timerRect.registerEntityModifier(new ColorModifier(1.0f, highlightColor, baseColor, EaseCubicInOut.getInstance()));
		else
		{
			SharedResources.getInstance().countdownSound.play();
			timerRect.registerEntityModifier(new ColorModifier(1.0f, hurryHighlightColor, hurryBaseColor, EaseCubicInOut.getInstance()));
		}
		countText.setText(String.format("%02d", (int) (maxValue - value)));
		if (maxValue - value <= 0)
			endAction.run();
	}
	
	public float getValue()
	{
		return maxValue - value;
	}

	public void startTimer()
	{
		final float currentScale = timerRect.getScaleY();

		timerRect.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 1.0f, currentScale, currentScale - (1.0f / maxValue), EaseCubicInOut.getInstance())
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				value += 1.0f;
				countText.setText(String.format("%02d", (int) (maxValue - value)));
				SharedResources.getInstance().timerClick.play();
				if (maxValue - value <= 0)
					endAction.run();
				else
					startTimer();

				super.onModifierFinished(pItem);
			}
		});
		if (maxValue - value > 5)
			timerRect.registerEntityModifier(new ColorModifier(1.0f, highlightColor, baseColor, EaseCubicInOut.getInstance()));
		else
		{
			SharedResources.getInstance().countdownSound.play();
			timerRect.registerEntityModifier(new ColorModifier(1.0f, hurryHighlightColor, hurryBaseColor, EaseCubicInOut.getInstance()));
		}
	}

	public void reset()
	{
		timerRect.clearEntityModifiers();
		timerRect.setScale(1.0f, 1.0f);
		timerRect.setColor(new Color(0, .5f, 0, RECT_ALPHA));
		countText.setText(String.format("%02d", (int) maxValue));
		value = 0;
	}

}
