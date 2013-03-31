package com.lionsteel.tiles.Entities;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TilesetEntity extends Entity implements TilesConstants
{
	private TilesMainActivity				activity;
	private GameButton[]				displayButtons;
	private final TextureRegion			buttonRegion;
//	private Sprite						buttonSprite;
	final BuildableBitmapTextureAtlas	atlas;
	final private float					buttonScale	= .33f;
	private Entity						buttonEntity;

	final private int					START_X		= 70;
	final private int					START_Y		= 50;

	public TilesetEntity(final Tileset tileset)
	{
		activity = TilesMainActivity.getInstance();
		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256);
		buttonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "tileButton.png");
		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		buttonEntity = new Entity();
		final float buttonWidth = TILE_WIDTH * buttonScale;
		displayButtons = new GameButton[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			displayButtons[i] = new GameButton(i, tileset, null, -1);
			displayButtons[i].buttonSprite.setScale(buttonScale);
			displayButtons[i].buttonSprite.setPosition(START_X + (buttonWidth / 2) * i, START_Y - (i % 2) * buttonWidth);
			buttonEntity.attachChild(displayButtons[i].buttonSprite);
		}

	}

	public Entity getButtonEntity()
	{
		return buttonEntity;
	}

	public TextureRegion getButtonRegion()
	{
		return buttonRegion;
	}

	public void clear()
	{
		atlas.unload();
		for (GameButton b : displayButtons)
			b.clear();
	}

}
