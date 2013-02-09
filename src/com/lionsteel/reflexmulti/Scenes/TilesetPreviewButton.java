package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.Entities.Tileset;
import com.lionsteel.reflexmulti.Entities.TilesetEntity;

public class TilesetPreviewButton extends Entity implements ReflexConstants
{
	final TilesetEntity		tilesetEntity;
	final Tileset			temporaryTileset;
	final String			basePath;
	final ReflexMenuButton	button;

	public TilesetPreviewButton(final String basePath)
	{
		this.basePath = basePath;
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		temporaryTileset = new Tileset(basePath, true);
		tilesetEntity = new TilesetEntity(temporaryTileset);
		button = new ReflexMenuButton(tilesetEntity.getButtonRegion(), new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.loadTileset(basePath);
			}
		});
		button.attachChild(tilesetEntity.getButtonEntity());

	}

	public void clear()
	{
		tilesetEntity.clear();
	}

	public ReflexMenuButton getButton()
	{
		return button;
	}

}
