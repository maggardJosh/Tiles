package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public class TilesetEntity extends Entity implements ReflexConstants
{
	private ReflexActivity	activity;
	private GameButton[]	displayButtons;
	private Sprite			buttonSprite;
	final BuildableBitmapTextureAtlas atlas;
	final private float		buttonScale	= .33f;
	
	final private int		START_X		= 70;
	final private int		START_Y		= 50;
	
	private Runnable		buttonAction;
	
	public TilesetEntity(final Tileset tileset)
	{
		activity = ReflexActivity.getInstance();
		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256);
		final TextureRegion buttonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "tileButton.png");
		try
		{	
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
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
			displayButtons[i] = new GameButton(i, tileset, null, -1);
			displayButtons[i].buttonSprite.setScale(buttonScale);			
			displayButtons[i].buttonSprite.setPosition(START_X + (buttonWidth/2)*i, START_Y - (i%2)*buttonWidth);
			buttonSprite.attachChild(displayButtons[i].buttonSprite);
		}
		
	}
	public void clear()
	{
		buttonSprite.detachSelf();
		buttonSprite.detachChildren();
		atlas.unload();
		for(GameButton b : displayButtons)
			b.clear();
		
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
