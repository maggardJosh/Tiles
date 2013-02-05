package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.Scenes.GameScene;

public class GameButton implements ReflexConstants
{
	final ReflexActivity	activity;
	
	public final Sprite		buttonSprite;
	private final int		playerOwner;
	
	private final int		buttonNumber;
	private GameScene		parent;
	
	public GameButton(final int buttonNumber,final Tileset tileset, final GameScene parent,
			final int player)
	{
		activity = ReflexActivity.getInstance();
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
	
	private void onTouched()
	{
		parent.buttonPressed(this);
	}
	
}
