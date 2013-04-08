package com.lionsteel.tiles.Entities;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.amazon.inapp.purchasing.PurchasingManager;
import com.lionsteel.tiles.AmazonPurchaser;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.MenuScenes.BuyTilesetSelectScene;

public class BuyTilesetPreviewButton extends Entity implements TilesConstants
{
	final TilesetEntity		tilesetEntity;
	final Tileset			temporaryTileset;
	final String			basePath;
	final TilesMenuButton	button;

	private class BuyTilesetTask extends AsyncTask<String, Void, Void>
	{
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			progressDialog = AmazonPurchaser.purchaseDialog;
			progressDialog.setMessage("Launching Market");
			progressDialog.setCancelable(false);
			progressDialog.show();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(String... params)
		{
			PurchasingManager.initiatePurchaseRequest("com.lionsteel.tiles."+basePath);
			return null;
		}

	}
	
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
				TilesMainActivity.getInstance().runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						new BuyTilesetTask().execute(basePath);
					}
				});

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
