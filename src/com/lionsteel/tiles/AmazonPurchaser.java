package com.lionsteel.tiles;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.amazon.inapp.purchasing.BasePurchasingObserver;
import com.amazon.inapp.purchasing.GetUserIdResponse;
import com.amazon.inapp.purchasing.GetUserIdResponse.GetUserIdRequestStatus;
import com.amazon.inapp.purchasing.Item;
import com.amazon.inapp.purchasing.ItemDataResponse;
import com.amazon.inapp.purchasing.Offset;
import com.amazon.inapp.purchasing.PurchaseResponse;
import com.amazon.inapp.purchasing.PurchaseUpdatesResponse;
import com.amazon.inapp.purchasing.PurchasingManager;
import com.amazon.inapp.purchasing.Receipt;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Scenes.MenuScenes.TilesetSelectScene;

public class AmazonPurchaser extends BasePurchasingObserver
{

	private TilesMainActivity	activity;
	private static final String	TAG		= "Amazon-IAP";
	private final String		OFFSET	= "offset";

	public AmazonPurchaser(TilesMainActivity activity)
	{
		super(activity);
		this.activity = activity;
	}

	/**
	 * Invoked once the observer is registered with the Puchasing Manager If the boolean is false, the application is receiving responses from the SDK Tester. If the boolean is
	 * true, the application is live in production.
	 * 
	 * @param isSandboxMode
	 *            Boolean value that shows if the app is live or not.
	 */
	@Override
	public void onSdkAvailable(final boolean isSandboxMode)
	{
		Log.v(TAG, "onSdkAvailable recieved: Response -" + isSandboxMode);
		PurchasingManager.initiateGetUserIdRequest();
	}

	/**
	 * Invoked once the call from initiateGetUserIdRequest is completed. On a successful response, a response object is passed which contains the request id, request status, and
	 * the userid generated for your application.
	 * 
	 * @param getUserIdResponse
	 *            Response object containing the UserID
	 */
	@Override
	public void onGetUserIdResponse(final GetUserIdResponse getUserIdResponse)
	{
		Log.v(TAG, "onGetUserIdResponse recieved: Response -" + getUserIdResponse);
		Log.v(TAG, "RequestId:" + getUserIdResponse.getRequestId());
		Log.v(TAG, "IdRequestStatus:" + getUserIdResponse.getUserIdRequestStatus());
		new GetUserIdAsyncTask().execute(getUserIdResponse);
	}

	/**
	 * Invoked once the call from initiateItemDataRequest is completed. On a successful response, a response object is passed which contains the request id, request status, and a
	 * set of item data for the requested skus. Items that have been suppressed or are unavailable will be returned in a set of unavailable skus.
	 * 
	 * @param itemDataResponse
	 *            Response object containing a set of purchasable/non-purchasable items
	 */
	@Override
	public void onItemDataResponse(final ItemDataResponse itemDataResponse)
	{
		Log.v(TAG, "onItemDataResponse recieved");
		Log.v(TAG, "ItemDataRequestStatus" + itemDataResponse.getItemDataRequestStatus());
		Log.v(TAG, "ItemDataRequestId" + itemDataResponse.getRequestId());
		new ItemDataAsyncTask().execute(itemDataResponse);
	}

	/**
	 * Is invoked once the call from initiatePurchaseRequest is completed. On a successful response, a response object is passed which contains the request id, request status, and
	 * the receipt of the purchase.
	 * 
	 * @param purchaseResponse
	 *            Response object containing a receipt of a purchase
	 */
	@Override
	public void onPurchaseResponse(final PurchaseResponse purchaseResponse)
	{
		Log.v(TAG, "onPurchaseResponse recieved");
		Log.v(TAG, "PurchaseRequestStatus:" + purchaseResponse.getPurchaseRequestStatus());
		new PurchaseAsyncTask().execute(purchaseResponse);
	}

	/**
	 * Is invoked once the call from initiatePurchaseUpdatesRequest is completed. On a successful response, a response object is passed which contains the request id, request
	 * status, a set of previously purchased receipts, a set of revoked skus, and the next offset if applicable. If a user downloads your application to another device, this call
	 * is used to sync up this device with all the user's purchases.
	 * 
	 * @param purchaseUpdatesResponse
	 *            Response object containing the user's recent purchases.
	 */
	@Override
	public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse purchaseUpdatesResponse)
	{
		Log.v(TAG, "onPurchaseUpdatesRecived recieved: Response -" + purchaseUpdatesResponse);
		Log.v(TAG, "PurchaseUpdatesRequestStatus:" + purchaseUpdatesResponse.getPurchaseUpdatesRequestStatus());
		Log.v(TAG, "RequestID:" + purchaseUpdatesResponse.getRequestId());
		new PurchaseUpdatesAsyncTask().execute(purchaseUpdatesResponse);
	}

	/*
	 * Helper method to print out relevant receipt information to the log.
	 */
	private void printReceipt(final Receipt receipt)
	{
		Log.v(TAG, String.format("Receipt: ItemType: %s Sku: %s SubscriptionPeriod: %s", receipt.getItemType(), receipt.getSku(), receipt.getSubscriptionPeriod()));
	}

	/*
	 * Helper method to retrieve the correct key to use with our shared preferences
	 */
	private String getKey(final String sku)
	{
		String[] parts = sku.split("\\.");
		return parts[parts.length-1];
	}

	/*
	 * Started when the Observer receives a GetUserIdResponse. The Shared Preferences file for the returned user id is
	 * accessed.
	 */
	private class GetUserIdAsyncTask extends AsyncTask<GetUserIdResponse, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(final GetUserIdResponse... params)
		{
			GetUserIdResponse getUserIdResponse = params[0];

			if (getUserIdResponse.getUserIdRequestStatus() == GetUserIdRequestStatus.SUCCESSFUL)
			{
				return true;
			} else
			{
				Log.v(TAG, "onGetUserIdResponse: Unable to get user ID.");
				return false;
			}
		}

		/*
		 * Call initiatePurchaseUpdatesRequest for the returned user to sync purchases that are not yet fulfilled.
		 */
		@Override
		protected void onPostExecute(final Boolean result)
		{
			super.onPostExecute(result);
			if (result)
			{
				PurchasingManager.initiatePurchaseUpdatesRequest(Offset.fromString(activity.getPreferences(Context.MODE_PRIVATE).getString(OFFSET, Offset.BEGINNING.toString())));
			}
		}
	}

	/*
	 * Started when the observer receives an Item Data Response.
	 * Takes the items and display them in the logs. You can use this information to display an in game
	 * storefront for your IAP items.
	 */
	private class ItemDataAsyncTask extends AsyncTask<ItemDataResponse, Void, Void>
	{
		@Override
		protected Void doInBackground(final ItemDataResponse... params)
		{
			final ItemDataResponse itemDataResponse = params[0];

			switch (itemDataResponse.getItemDataRequestStatus())
			{
			case SUCCESSFUL_WITH_UNAVAILABLE_SKUS:
				// Skus that you can not purchase will be here.
				for (final String s : itemDataResponse.getUnavailableSkus())
				{
					Log.v(TAG, "Unavailable SKU:" + s);
				}
			case SUCCESSFUL:
				// Information you'll want to display about your IAP items is here
				// In this example we'll simply log them.
				final Map<String, Item> items = itemDataResponse.getItemData();
				for (final String key : items.keySet())
				{
					Item i = items.get(key);
					Log.v(TAG, String.format("Item: %s\n Type: %s\n SKU: %s\n Price: %s\n Description: %s\n", i.getTitle(), i.getItemType(), i.getSku(), i.getPrice(), i.getDescription()));
				}
				break;
			case FAILED:
				// On failed responses will fail gracefully.
				break;

			}

			return null;
		}
	}

	/*
	 * Started when the observer receives a Purchase Response
	 * Once the AsyncTask returns successfully, the UI is updated.
	 */
	private class PurchaseAsyncTask extends AsyncTask<PurchaseResponse, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(final PurchaseResponse... params)
		{
			final PurchaseResponse purchaseResponse = params[0];

			final SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
			final SharedPreferences.Editor editor = settings.edit();
			switch (purchaseResponse.getPurchaseRequestStatus())
			{
			case SUCCESSFUL:
				/*
				 * You can verify the receipt and fulfill the purchase on successful responses.
				 */
				final Receipt receipt = purchaseResponse.getReceipt();
				String key = "";
				switch (receipt.getItemType())
				{
				case CONSUMABLE:
					break;
				case ENTITLED:
					key = getKey(receipt.getSku());
					editor.putBoolean(key, true);
					Tileset.addPurchasedTileset(key);
					break;
				case SUBSCRIPTION:
					break;
				}
				editor.commit();

				printReceipt(purchaseResponse.getReceipt());
				return true;
			case ALREADY_ENTITLED:
				/*
				 * If the customer has already been entitled to the item, a receipt is not returned.
				 * Fulfillment is done unconditionally, we determine which item should be fulfilled by matching the
				 * request id returned from the initial request with the request id stored in the response.
				 */
				return true;
			case FAILED:
				/*
				 * If the purchase failed for some reason, (The customer canceled the order, or some other
				 * extraneous circumstance happens) the application ignores the request and logs the failure.
				 */
				Log.v(TAG, "Failed purchase for request" + purchaseResponse.getRequestId());
				return false;
			case INVALID_SKU:
				/*
				 * If the sku that was purchased was invalid, the application ignores the request and logs the failure.
				 * This can happen when there is a sku mismatch between what is sent from the application and what
				 * currently exists on the dev portal.
				 */
				Log.v(TAG, "Invalid Sku for request " + purchaseResponse.getRequestId());
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			super.onPostExecute(success);
			if (success)
			{
				TilesetSelectScene.getInstance().redoButtons();
			}
		}
	}

	/*
	 * Started when the observer receives a Purchase Updates Response Once the AsyncTask returns successfully, we'll
	 * update the UI.
	 */
	private class PurchaseUpdatesAsyncTask extends AsyncTask<PurchaseUpdatesResponse, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(final PurchaseUpdatesResponse... params)
		{
			final PurchaseUpdatesResponse purchaseUpdatesResponse = params[0];
			final SharedPreferences.Editor editor = activity.getPreferences(Context.MODE_PRIVATE).edit();
			/*
			 * If the customer for some reason had items revoked, the skus for these items will be contained in the
			 * revoked skus set.
			 */
			for (final String sku : purchaseUpdatesResponse.getRevokedSkus())
			{
				Log.v(TAG, "Revoked Sku:" + sku);
				final String key = getKey(sku);
				editor.putBoolean(key, false);
				editor.commit();
			}

			switch (purchaseUpdatesResponse.getPurchaseUpdatesRequestStatus())
			{
			case SUCCESSFUL:
				for (final Receipt receipt : purchaseUpdatesResponse.getReceipts())
				{
					final String sku = receipt.getSku();
					final String key = getKey(sku);
					switch (receipt.getItemType())
					{
					case ENTITLED:
						/*
						 * If the receipt is for an entitlement, the customer is re-entitled.
						 */
						Tileset.addPurchasedTileset(key);
						editor.putBoolean(key, true);
						editor.commit();
						break;
					case SUBSCRIPTION:
						break;
					case CONSUMABLE:
						break;
					default:
						break;

					}
					printReceipt(receipt);
				}

				/*
				 * Store the offset into shared preferences. If there has been more purchases since the
				 * last time our application updated, another initiatePurchaseUpdatesRequest is called with the new
				 * offset.
				 */
				final Offset newOffset = purchaseUpdatesResponse.getOffset();
				editor.putString(OFFSET, newOffset.toString());
				editor.commit();
				if (purchaseUpdatesResponse.isMore())
				{
					Log.v(TAG, "Initiating Another Purchase Updates with offset: " + newOffset.toString());
					PurchasingManager.initiatePurchaseUpdatesRequest(newOffset);
				}
				return true;
			case FAILED:
				/*
				 * On failed responses the application will ignore the request.
				 */
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			super.onPostExecute(success);
			if (success)
			{
				TilesetSelectScene.getInstance().redoButtons();
			}
		}
	}

}
