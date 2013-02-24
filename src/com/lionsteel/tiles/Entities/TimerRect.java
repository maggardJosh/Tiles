package com.lionsteel.tiles.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.IEntityModifier;
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

	private float			elapsedSeconds;

	private final Text		countText;

	private final float		seconds;

	public TimerRect(final float seconds)
	{
		this.seconds = seconds;

		timerRect = new Rectangle(TILE_BASE_RIGHT_SIDE, 0, CAMERA_WIDTH - TILE_BASE_RIGHT_SIDE, CAMERA_HEIGHT, TilesMainActivity.getInstance().getVertexBufferObjectManager());
		timerRect.setAlpha(0);
		this.attachChild(timerRect);
		countText = new Text(0, 0, SharedResources.getInstance().mFont, String.format("%02d", (int) seconds), 3, TilesMainActivity.getInstance().getVertexBufferObjectManager());

		initCountText();

		reset();
	}

	public void fadeIn()
	{
		timerRect.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1));
		countText.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1));
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

	public void startTimer()
	{
		final float currentScale = timerRect.getScaleY();

		timerRect.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 1.0f, currentScale, currentScale - (1.0f / seconds), EaseCubicInOut.getInstance())
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				elapsedSeconds += 1.0f;
				countText.setText(String.format("%02d", (int) (seconds - elapsedSeconds)));
				startTimer();
				super.onModifierFinished(pItem);
			}
		});
		if (seconds - elapsedSeconds > 5)
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
		timerRect.setColor(new Color(0, .5f, 0));
		countText.setText(String.format("%02d", (int)seconds));
		elapsedSeconds = 0;
	}

}
