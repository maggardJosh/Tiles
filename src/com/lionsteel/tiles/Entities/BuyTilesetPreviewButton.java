package com.lionsteel.tiles.Entities;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import android.util.Log;
import android.widget.Toast;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.MenuScenes.BuyTilesetSelectScene;
import com.lionsteel.tiles.Scenes.MenuScenes.TilesetSelectScene;
import com.lionsteel.tiles.util.IabHelper.OnIabPurchaseFinishedListener;
import com.lionsteel.tiles.util.IabResult;
import com.lionsteel.tiles.util.Purchase;

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
				BuyTilesetSelectScene.getInstance().resetScrollDetector();
				TilesMainActivity.getInstance().load(new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Real In-App Purchases here.
						TilesMainActivity.getInstance().getIABHelper().launchPurchaseFlow(TilesMainActivity.getInstance(), "android.test.purchased", 10001, new OnIabPurchaseFinishedListener()
						{

							@Override
							public void onIabPurchaseFinished(final IabResult result, final Purchase info)
							{
								if (result.isFailure())
								{
									Log.d("IAB", "Purchase Failure " + result.getMessage());
									TilesMainActivity.getInstance().clearLoadingScreen();
									return;
								}

								if (info != null)
									TilesMainActivity.getInstance().getIABHelper().consumeAsync(info, null);
								Tileset.purchasedTilesets.add(basePath);
								TilesetSelectScene.getInstance().redoButtons();
								TilesMainActivity.getInstance().clearLoadingScreen();

							}
						}, "");
					}
				}, false);

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