package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public class TilesetEntity extends Entity implements ReflexConstants
{
	private ReflexActivity	activity;
	private GameButton[]	displayButtons;
	private Sprite			buttonSprite;
	final private float		buttonScale	= .33f;
	
	final private int		START_X		= 70;
	final private int		START_Y		= 50;
	
	private Runnable		buttonAction;
	
	public TilesetEntity()
	{
		activity = ReflexActivity.getInstance();
		final BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 256);
		final TextureRegion buttonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "tileButton.png", 0, 0);
		atlas.load();
		
		buttonSprite = new Sprite(0, 0, buttonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
					if (buttonAction != null)
						buttonAction.run();
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		final float buttonWidth = BUTTON_WIDTH * buttonScale;
		displayButtons = new GameButton[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			displayButtons[i] = new GameButton(i + 1, null, -1);
			displayButtons[i].buttonSprite.setScale(buttonScale);			
			displayButtons[i].buttonSprite.setPosition(START_X + (buttonWidth/2)*i, START_Y - (i%2)*buttonWidth);
			buttonSprite.attachChild(displayButtons[i].buttonSprite);
		}
		
	}
	
	public void setAction(Runnable action)
	{
		this.buttonAction = action;
	}
	
	public Sprite getButtonSprite()
	{
		return buttonSprite;
	}
}
