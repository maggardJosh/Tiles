package com.lionsteel.tiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Scenes.GameScenes.FreePlayGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.FrenzyGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.GameOverScreen;
import com.lionsteel.tiles.Scenes.GameScenes.NonStopGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.PauseScene;
import com.lionsteel.tiles.Scenes.GameScenes.PracticeGameOverScene;
import com.lionsteel.tiles.Scenes.GameScenes.RaceGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.ReflexGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.TimeAttackGameScene;
import com.lionsteel.tiles.Scenes.MenuScenes.BackgroundMenuScene;
import com.lionsteel.tiles.Scenes.MenuScenes.BuyTilesetSelectScene;
import com.lionsteel.tiles.Scenes.MenuScenes.MainMenuScene;
import com.lionsteel.tiles.Scenes.MenuScenes.QuitPromptScene;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;
import com.lionsteel.tiles.Scenes.MenuScenes.SplashScene;
import com.lionsteel.tiles.Scenes.MenuScenes.TilesetSelectScene;
import com.lionsteel.tiles.util.IabException;
import com.lionsteel.tiles.util.IabHelper;
import com.lionsteel.tiles.util.IabResult;
import com.lionsteel.tiles.util.Inventory;
import com.lionsteel.tiles.util.Purchase;

public class TilesMainActivity extends JifBaseGameActivity implements TilesConstants
{

	private static TilesMainActivity	instance;

	private GameScene					gameScene;
	private MainMenuScene				mainMenuScene;
	private SplashScene					splashScene;
	private BackgroundMenuScene			backgroundScene;
	private QuitPromptScene				menuQuitPromptScene;
	private QuitPromptScene				gameQuitPromptScene;

	public boolean						backEnabled			= false;

	public SharedPreferences			sharedPrefs;

	private IabHelper					mHelper;

	private Inventory					currentInventory;

	private boolean						arePurchasesLoaded	= false;

	public boolean getArePurchasesLoaded()
	{
		return arePurchasesLoaded;
	}

	public static TilesMainActivity getInstance()
	{
		if (instance == null)
			instance = new TilesMainActivity();
		return instance;
	}

	public void savePreference(String key, String value)
	{
		final Editor editor = sharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void saveBooleanPref(String key, boolean value)
	{
		final Editor editor = sharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void saveInt(String key, int value)
	{
		final Editor editor = sharedPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void saveFloat(String key, float value)
	{
		final Editor editor = sharedPrefs.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	@Override
	protected void onStart()
	{
		FlurryAgent.onStartSession(this, "3ZV4J886JJR56QBBF9YX");
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	public static void startGameEvent()
	{
		HashMap<String, String> gameParams = new HashMap<String, String>();
		gameParams.put("Tileset", SetupScene.getTileset().getBasePath());
		gameParams.put("Game_Difficulty", Difficulty.getName(SetupScene.getDifficulty()));
		gameParams.put("Game_Mode", GameMode.getName(SetupScene.getGameMode()));
		FlurryAgent.logEvent(FlurryAgentEventStrings.GAME_PLAYED, gameParams, true);
		GameScene.isGameEventStarted = true;
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState)
	{
		super.onCreate(pSavedInstanceState);

		int[] valueOne = { 35, 58, 60, 51, 113, 15, 59, 125, 42, 87, 41, 72, 6, 88, 24, 12, 90, 1, 117, 109, 114, 39, 22, 35, 12, 47, 7, 27, 38, 28, 84, 24, 59, 39, 0, 112, 113, 51, 17, 36, 27, 32, 42, 47, 38, 6, 35, 63, 20, 50, 15, 27, 67, 15, 34, 37, 29, 27, 86, 22, 15, 66, 80, 85, 56, 10, 6, 3, 51, 114, 13, 69, 52, 100, 80, 0, 22, 74, 126, 24, 27, 61, 42, 35, 36, 1, 17, 35, 37, 107, 68, 58, 19, 85, 104, 37, 53, 9, 27, 33, 45, 36, 5, 8, 55, 28, 48, 25, 57, 0, 28, 43, 46, 34, 7, 24, 8, 26, 54, 34, 110, 11, 32, 11, 9, 116, 10, 91, 10, 127, 37, 114, 15, 56, 10, 124, 24, 69, 56, 92, 32, 15, 61, 33, 15, 21, 71, 12, 30, 88, 39, 10, 115, 55, 22, 41, 19, 19, 92, 88, 14, 53, 105, 36, 30, 28, 24, 59, 59, 46, 8, 50, 5, 1, 69, 42, 62, 19, 12, 31, 20, 86, 3, 74, 26, 117, 95, 85, 25, 35, 44, 6, 35, 66, 120, 1, 103, 93, 116, 52, 43, 57, 52, 102, 11, 32, 79, 69, 2, 123, 99, 16, 55, 5, 110, 63, 58, 37, 3, 2, 49, 30, 18, 9, 60, 46, 25, 25, 120, 15, 88, 27, 55, 32, 60, 7, 79, 47, 57, 120, 56, 93, 10, 112, 28, 92, 2, 30, 6, 0, 18, 121, 114, 1, 4, 52, 122, 57, 48, 111, 2, 28, 95, 96, 79, 10, 126, 101, 118, 38, 43, 41, 22, 0, 25, 36, 49, 5, 115, 115, 29, 33, 33, 27, 61, 14, 41, 17, 3, 12, 26, 43, 71, 64, 119, 61, 9, 66, 33, 121, 117, 109, 55, 66, 57, 30, 8, 32, 29, 22, 113, 3, 107, 21, 2, 9, 103, 50, 10, 47, 41, 52, 18, 42, 33, 74, 113, 103, 17, 8, 51, 31, 95, 3, 14, 5, 117, 27, 33, 40, 32, 24, 48, 117, 29, 39, 18, 9, 22, 69, 4, 57, 95, 31, 13, 107, 42, 118, 50, 99, 0, 75, 63, 13, 85, 56, 51, 120, 66, 1, 27, 39, 121, 92, 123, 18, 13, 23, 42, 9, 26, 0, 6, 72, 127, 35, 19, 35, 27, 32, 46, 44 };
		String key = "nsuq8ez3h0B9n3qKcvE/3vSeMnHXgMlYvnI22TZgZqonAPBJDXuVtALVhj";
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < valueOne.length; x++)
			sb.append((char) (valueOne[x] ^ key.charAt(x % key.length())));

		mHelper = new IabHelper(this, sb.toString());
		//		mHelper.enableDebugLogging(true, "IABDebug");

	}

	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
	{
		Log.d("Tiles", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data))
		{
			super.onActivityResult(requestCode, resultCode, data);
		} else
		{
			Log.d("Tiles", "onActivityResult handled by IABUtil.");
		}
	}

	private void reloadTilesets()
	{
		Tileset.getPurchasedTilesets(currentInventory);
		TilesetSelectScene.getInstance().redoButtons();

	}

	protected void onDestroy()
	{
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
		super.onDestroy();
	};

	public static void endGameEvent()
	{
		if (GameScene.isGameEventStarted)
		{
			GameScene.isGameEventStarted = false;
			FlurryAgent.endTimedEvent(FlurryAgentEventStrings.GAME_PLAYED);
		}
	}

	@Override
	protected synchronized void onResume()
	{
		SongManager.getInstance().unpause();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		SongManager.getInstance().pause();
		super.onPause();
	}

	@Override
	public EngineOptions onCreateEngineOptions()
	{
		instance = this;

		sharedPrefs = getPreferences(MODE_PRIVATE);
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		return engineOptions;
	}

	@Override
	public void onReloadResources()
	{
		super.onReloadResources();
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public void moveBackground(final boolean moveToLeft)
	{
		backgroundScene.moveBackground(moveToLeft);
	}

	private static InitialLoadTask	loadTaskInstance;

	public static InitialLoadTask getLoadTaskInstance()
	{
		if (loadTaskInstance == null)
			loadTaskInstance = getInstance().new InitialLoadTask();
		return loadTaskInstance;
	}

	private class InitialLoadTask extends AsyncTask<Void, String, Void>
	{
		ProgressDialog	progressDialog;

		@Override
		protected void onPreExecute()
		{
			splashScene = new SplashScene();

			try
			{
				Tileset.tilesetList = getAssets().list("gfx/tilesets");
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			mEngine.setScene(splashScene);

			progressDialog = new ProgressDialog(instance);
			progressDialog.setMessage("Loading Menu Assets");
			progressDialog.setCancelable(false);
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					progressDialog.show();
				}
			});

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(final String... values)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (values.length > 0)
						progressDialog.setMessage(values[0]);
				}
			});
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			instance.getEngine().registerUpdateHandler(SongManager.getInstance());
			publishProgress("Loading Shared Assets");
			SharedResources.getInstance(); //Make sure shared resources is initialized during splash screen.
			menuQuitPromptScene = new QuitPromptScene(new Runnable()
			{
				@Override
				public void run()
				{
					finish();
				}
			});
			gameQuitPromptScene = new QuitPromptScene(new Runnable()
			{
				@Override
				public void run()
				{
					if (GameScene.isGameEventStarted)
						TilesMainActivity.endGameEvent();
					backToMainMenu();
				}
			});
			publishProgress("Loading Pause Screen");

			PauseScene.getInstance();
			publishProgress("Loading Menu Assets");
			mainMenuScene = new MainMenuScene();
			backgroundScene = new BackgroundMenuScene(mainMenuScene);

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			progressDialog.dismiss();
			instance.getEngine().registerUpdateHandler(new TimerHandler(.5f, new ITimerCallback()
			{

				@Override
				public void onTimePassed(TimerHandler pTimerHandler)
				{
					instance.getEngine().unregisterUpdateHandler(pTimerHandler);
					splashScene.fadeOut(new Runnable()
					{
						@Override
						public void run()
						{
							mainMenuScene.logFlurryEvent();
							SongManager.getInstance().playSong(SharedResources.getInstance().menuMusic);
							mEngine.setScene(backgroundScene);
							backEnabled = true;
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									AppRater.app_launched(instance);
								}
							});
						}
					});
				}
			}));
			super.onPostExecute(result);
		}
	}

	public void updateLoadProgress(String progressString)
	{
		getLoadTaskInstance().onProgressUpdate(progressString);
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		pOnCreateSceneCallback.onCreateSceneFinished(new Scene());
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				loadTaskInstance = new InitialLoadTask();
				loadTaskInstance.execute();
			}
		});

	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	private void clearAllSingletons()
	{
		SharedResources.clear();
		SongManager.clear();
		TilesetSelectScene.clear();
		BuyTilesetSelectScene.clear();
		SetupScene.clear();
		PauseScene.clear();
	}

	@Override
	public void onDestroyResources() throws Exception
	{
		clearAllSingletons();
		super.onDestroyResources();
	}

	private class StartGameTask extends AsyncTask<Void, Void, Void>
	{

		private ProgressDialog	progressDialog;

		@Override
		protected void onPreExecute()
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					progressDialog = new ProgressDialog(instance);
					progressDialog.setCancelable(false);
					progressDialog.setMessage("Loading Game");
					progressDialog.show();
				}
			});
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			SetupScene.getTileset().createGameAssets();
			SharedResources.getInstance().loadGameAssets();
			switch (SetupScene.getGameMode())
			{
			case GameMode.REFLEX:
				gameScene = new ReflexGameScene();
				break;
			case GameMode.NON_STOP:
				gameScene = new NonStopGameScene();
				break;
			case GameMode.RACE:
				gameScene = new RaceGameScene();
				break;
			case GameMode.FREE_PLAY:
				gameScene = new FreePlayGameScene();
				break;
			case GameMode.TIME_ATTACK:
				gameScene = new TimeAttackGameScene();
				break;
			case GameMode.FRENZY:
				gameScene = new FrenzyGameScene();
				break;
			}
			mEngine.setScene(gameScene);

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			mEngine.registerUpdateHandler(new TimerHandler(.5f, new ITimerCallback()
			{

				@Override
				public void onTimePassed(TimerHandler pTimerHandler)
				{
					progressDialog.dismiss();
				}
			}));
			super.onPostExecute(result);
		}
	}

	public void startGame()
	{
		new StartGameTask().execute();
	}

	public void backToTilesetSelect()
	{
		TilesetSelectScene.getInstance().clearChildScene();
	}

	public void backToSetupScene()
	{
		SetupScene.getInstance().clearChildScene();
	}

	public void superOnBackPressed()
	{
		super.onBackPressed();
	}

	@Override
	public void onBackPressed()
	{
		if (!backEnabled)
			return;
		Scene parentScene = this.mEngine.getScene();

		if (!parentScene.hasChildScene() && parentScene instanceof GameScene)
		{
			((GameScene) parentScene).showPauseScene();
			((PauseScene) parentScene.getChildScene()).transitionChildScene(gameQuitPromptScene);
			return;
		}

		while (parentScene.getChildScene().hasChildScene())
			parentScene = parentScene.getChildScene();

		if (parentScene instanceof BackgroundMenuScene)
		{
			showQuitPrompt((TilesMenuScene) parentScene.getChildScene());
			return;
		}

		if (parentScene.getChildScene() instanceof QuitPromptScene)
		{
			((QuitPromptScene) parentScene.getChildScene()).callQuitAction();
			return;
		} else if (parentScene.getChildScene() instanceof GameOverScreen)
		{
			((GameOverScreen) parentScene.getChildScene()).transitionChildScene(gameQuitPromptScene);
			return;
		} else if (parentScene.getChildScene() instanceof PracticeGameOverScene)
		{
			((PracticeGameOverScene) parentScene.getChildScene()).transitionChildScene(gameQuitPromptScene);
			return;
		} else if (parentScene.getChildScene() instanceof PauseScene)
		{
			((PauseScene) parentScene.getChildScene()).transitionChildScene(gameQuitPromptScene);
			return;
		}

		parentScene.clearChildScene();
	}

	public void showQuitPrompt(TilesMenuScene scene)
	{
		scene.transitionChildScene(menuQuitPromptScene, true);
	}

	public void backToMainMenu()
	{
		SongManager.getInstance().setVolumeMultiplier(1.0f);
		SongManager.getInstance().setCurrentVolume(0);
		SongManager.getInstance().playSong(SharedResources.getInstance().menuMusic);
		SetupScene.getInstance().logFlurryEvent();
		SetupScene.getInstance().initScene();
		mEngine.setScene(backgroundScene);
	}

	public IabHelper getIABHelper()
	{
		return mHelper;
	}

	//----Inner AsyncTask Classes

	private boolean	doneSettingUpIAB	= false;
	private boolean	doneQuerying		= false;

	public boolean isReadyToFadeOut()
	{
		return doneSettingUpIAB && doneQuerying;
	}

	private class QueryIABInventory extends AsyncTask<Runnable, Void, Void>
	{
		ProgressDialog	queryDialog;

		@Override
		protected void onPreExecute()
		{
			queryDialog = new ProgressDialog(instance);
			queryDialog.setMessage("Querying Purchases");
			queryDialog.setCancelable(true);
			queryDialog.show();
			final QueryIABInventory thisThread = this;
			queryDialog.setOnCancelListener(new OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					thisThread.cancel(true);
				}
			});
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Runnable... params)
		{
			final ArrayList<String> additionalSkuList = new ArrayList<String>();
			for (String s : Tileset.purchaseableTilesets)
				additionalSkuList.add(s);

			IabResult result = new IabResult(IabHelper.BILLING_RESPONSE_RESULT_OK, "Inventory refresh successful.");
			Inventory inventory = null;
			try
			{
				inventory = mHelper.queryInventory(false, additionalSkuList);
			} catch (IabException e)
			{
				result = e.getResult();
				e.printStackTrace();
			}
			if (result.isFailure())
			{
				Log.d("IAB", "Query Failure");
				Log.d("IAB", result.getMessage());
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(instance, "Unable to connect to market", Toast.LENGTH_SHORT).show();
					}
				});
				arePurchasesLoaded = false;

				return null;
			}

			Log.d("IAB", "Query Success");

			final Purchase fakePurchase = inventory.getPurchase("android.test.purchased");
			if (fakePurchase != null)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						mHelper.consumeAsync(fakePurchase, null);
					};
				});
			}

			currentInventory = inventory;

			reloadTilesets();

			arePurchasesLoaded = true;

			for (Runnable r : params)
				r.run();

			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(instance, "Now connected to market", Toast.LENGTH_LONG).show();
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			doneQuerying = true;
			queryDialog.dismiss();
			super.onPostExecute(result);
		}
	}

	private class SetupIABTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	IABSetupProgressDialog;

		@Override
		protected void onPreExecute()
		{

			IABSetupProgressDialog = new ProgressDialog(instance);
			IABSetupProgressDialog.setMessage("Trying to Connect");
			IABSetupProgressDialog.setCancelable(true);
			final SetupIABTask thisThread = this;
			IABSetupProgressDialog.setOnCancelListener(new OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					thisThread.cancel(true);
				}
			});

			IABSetupProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
			{

				public void onIabSetupFinished(IabResult result)
				{
					if (result.isFailure())
					{
						// Oh noes, there was a problem.
						Log.d("IAB", "Problem setting up In-app Billing: " + result);
						runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								Toast.makeText(instance, "Cannot connect to Google Play service", Toast.LENGTH_SHORT).show();
							}
						});
						doneQuerying = true;
						return;
					}
					Log.d("IAB", "SUCCESS");
					if (mHelper.isSetup())
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								new QueryIABInventory().execute();
							}
						});
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			doneSettingUpIAB = true;
			IABSetupProgressDialog.dismiss();

			super.onPostExecute(result);
		}
	}

	//---- End of Inner AsyncTask Classes

	public void setupIABHelper()
	{
		if (mHelper.isSetup())
			return;

		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				new SetupIABTask().execute();

			}
		});

	}

	public void startGetPurchasesTask(final Runnable[] endAction)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				new QueryIABInventory().execute(endAction);
			}
		});
	}

}
