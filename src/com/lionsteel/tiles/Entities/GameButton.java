package com.lionsteel.tiles.Entities;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseCubicIn;
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
	
	public GameButton(final int buttonNumber,final Tileset tileset, final GameScene parent,
			final int player)
	{
		activity = TilesMainActivity.getInstance();
		this.buttonNumber = buttonNumber;
		this.parent = parent;
		playerOwner = player;
		
		buttonSprite = new Sprite(0, 0, tileset.getButtonRegion(buttonNumber), activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
					onTouched();
				return false;
			}
		};
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
	final float PULSE_TIME = .5f;
	final float PULSE_SCALE = 3.0f;
	
	private void pulseButton()
	{
		SharedResources.getInstance().buttonTouchSound.play();
		buttonSprite.registerEntityModifier(new ScaleModifier(PULSE_TIME, PULSE_SCALE, 1.0f, EaseCubicOut.getInstance()){
			@Override
			protected void onModifierStarted(IEntity pItem)
			{
				buttonSprite.setZIndex(BUTTON_Z);
				super.onModifierStarted(pItem);
			}
		});
		buttonSprite.setZIndex(BUTTON_Z+1);
		parent.sortChildren();
	}
	
	private void onTouched()
	{
		if(parent.buttonPressed(this))
			pulseButton();
	}

	public float getY()
	{
		return buttonSprite.getY();
	}
	
}
