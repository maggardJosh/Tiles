package com.lionsteel.tiles.Entities;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;

public class BuyTilesetPreviewButton extends Entity implements TilesConstants
{
	final TilesetEntity		tilesetEntity;
	final Tileset			temporaryTileset;
	final String			basePath;
	final TilesMenuButton	button;

	public BuyTilesetPreviewButton(final String basePath)
	{
		this.basePath = basePath;
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		temporaryTileset = new Tileset(basePath, true);
		tilesetEntity = new TilesetEntity(temporaryTileset);
		button = new TilesMenuButton(tilesetEntity.getButtonRegion(), new Runnable()
		{
			@Override
			public void run()
			{
				//TODO: Buy tileset here 
				//SetupScene.loadTileset(basePath);
			}
		});
		button.attachChild(tilesetEntity.getButtonEntity());

	}

	public void clear()
	{
		tilesetEntity.clear();
	}

	public TilesMenuButton getButton()
	{
		return button;
	}

}
