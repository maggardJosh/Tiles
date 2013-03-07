package com.lionsteel.tiles.Entities;

import java.util.HashMap;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.MenuScenes.BuyTilesetSelectScene;
import com.lionsteel.tiles.Scenes.MenuScenes.TilesetSelectScene;
import com.lionsteel.tiles.util.IabException;
import com.lionsteel.tiles.util.IabHelper.OnIabPurchaseFinishedListener;
import com.lionsteel.tiles.util.IabResult;
import com.lionsteel.tiles.util.Purchase;

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
			progressDialog = new ProgressDialog(TilesMainActivity.getInstance());
			progressDialog.setMessage("Launching Market");
			progressDialog.setCancelable(false);
			progressDialog.show();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(String... params)
		{
			TilesMainActivity.getInstance().getIABHelper().launchPurchaseFlow(TilesMainActivity.getInstance(), "android.test.purchased", 10001, new OnIabPurchaseFinishedListener()
			{

				@Override
				public void onIabPurchaseFinished(final IabResult result, final Purchase info)
				{
					if (result.isFailure())
					{
						Log.d("IAB", "Purchase Failure " + result.getMessage());

						TilesMainActivity.getInstance().progressDialog.dismiss();

						return;
					}

					if (info != null)
						try
						{
							TilesMainActivity.getInstance().getIABHelper().consume(info);
						} catch (IabException e)
						{
							e.printStackTrace();
						}
					
					HashMap<String, String> gameParams = new HashMap<String, String>();
					gameParams.put("Tileset_Bought", basePath);
					FlurryAgent.logEvent(FlurryAgentEventStrings.BUY_TILESET, gameParams);
					
					Tileset.purchasedTilesets.add(basePath);
					TilesetSelectScene.getInstance().redoButtons();
					TilesMainActivity.getInstance().progressDialog.dismiss();

				}
			}, "");
			return null;
		}
		@Override
		protected void onPostExecute(Void result)
		{
			progressDialog.dismiss();
			super.onPostExecute(result);
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
