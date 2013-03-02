package com.lionsteel.tiles;

import java.util.ArrayList;
import java.util.HashMap;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.util.modifier.IModifier;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
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
import com.lionsteel.tiles.Scenes.GameScenes.LoadingScene;
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
import com.lionsteel.tiles.util.IabHelper;
import com.lionsteel.tiles.util.IabHelper.OnConsumeFinishedListener;
import com.lionsteel.tiles.util.IabHelper.QueryInventoryFinishedListener;
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

	private LoadingScene				loadingScene;

	public boolean						backEnabled			= true;

	public SharedPreferences			sharedPrefs;

	private IabHelper					mHelper;

	private Inventory					currentInventory;

	private boolean						isIABConnected		= false;
	private boolean						isQuerying			= false;
	private boolean						canQuery			= false;
	private boolean						tryingToConnectIAB	= false;
	private boolean						arePurchasesLoaded	= false;
	private boolean						scenesLoaded		= false;

	public boolean canQuery()
	{
		return canQuery;
	}

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

	private ArrayList<String>	additionalSkuList	= new ArrayList<String>();

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
							Toast.makeText(instance, "Problem setting up In-App Billing", Toast.LENGTH_SHORT).show();
						}
					});
					arePurchasesLoaded = true;
					return;
				}
				Log.d("IAB", "SUCCESS");

				additionalSkuList.clear();
				for (String s : Tileset.purchaseableTilesets)
					additionalSkuList.add(s);
				queryPurchases();
				isIABConnected = true;

			}
		});

	}

	private QueryInventoryFinishedListener	queryInvAsync	= new QueryInventoryFinishedListener()
	{

		@Override
		public void onQueryInventoryFinished(final IabResult result, Inventory inv)
		{

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
				arePurchasesLoaded = true;
				isQuerying = false;
				return;
			}

			Log.d("IAB", "Query Success");

			Purchase fakePurchase = inv.getPurchase("android.test.purchased");
			if (fakePurchase != null)
				mHelper.consumeAsync(fakePurchase, null);

			currentInventory = inv;

			canQuery = true;
			reloadTilesets();

			arePurchasesLoaded = true;
			isQuerying = false;

			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					Toast.makeText(instance, "Now connected to market", Toast.LENGTH_LONG).show();
				}
			});

			return;
		}
	};

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
		while (!TilesetSelectScene.isCreated())
			;
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

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		splashScene = new SplashScene();

		Tileset.tilesetList = getAssets().list("gfx/tilesets");

		mEngine.registerUpdateHandler(new TimerHandler(.1f, new ITimerCallback()
		{

			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				mEngine.registerUpdateHandler(SongManager.getInstance());
				SharedResources.getInstance(); //Make sure shared resources is initialized during splash screen.
				loadingScene = new LoadingScene();
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

				mainMenuScene = new MainMenuScene();
				backgroundScene = new BackgroundMenuScene(mainMenuScene);

				scenesLoaded = true;

				mEngine.registerUpdateHandler(new TimerHandler(2.0f, new ITimerCallback()
				{

					@Override
					public void onTimePassed(TimerHandler pTimerHandler)
					{
						splashScene.fadeOut(new IEntityModifierListener()
						{

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{

							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{

								mainMenuScene.logFlurryEvent();
								SongManager.getInstance().playSong(SharedResources.getInstance().menuMusic);
								mEngine.setScene(backgroundScene);
							}
						});
					}
				}));

			}
		}));

		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);

	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	public void load(final Runnable loadAction)
	{
		load(loadAction, true);
	}

	public void load(final Runnable loadAction, final boolean autoRemoveSelf)
	{
		Scene currentScene = mEngine.getScene();
		while (currentScene.hasChildScene())
			currentScene = currentScene.getChildScene();
		final TilesMenuScene lastScene = (TilesMenuScene) currentScene;
		loadingScene.setPosition(0, 0);
		currentScene.setChildScene(loadingScene, false, false, true);
		backEnabled = false;

		mEngine.registerUpdateHandler(new TimerHandler(.2f, new ITimerCallback()
		{

			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				loadAction.run();
				if (autoRemoveSelf)
				{
					lastScene.setChildSceneNull();
					backEnabled = true;
				}
			}
		}));
	}

	@Override
	public void onDestroyResources() throws Exception
	{
		SharedResources.clear();
		SongManager.clear();
		TilesetSelectScene.clear();
		BuyTilesetSelectScene.clear();
		super.onDestroyResources();
	}

	public void startGame()
	{
		load(new Runnable()
		{

			@Override
			public void run()
			{
				SetupScene.getTileset().createGameAssets();
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
			}
		});

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

		SharedResources.getInstance().menuBlip.play();

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
		if (isIABConnected)
			return mHelper;
		else
			return null;
	}

	public void setupIABHelper()
	{
		if (tryingToConnectIAB)
			return;
		tryingToConnectIAB = true;
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				Toast.makeText(instance, "Trying to connect...", Toast.LENGTH_SHORT).show();
			}
		});
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
							Toast.makeText(instance, "Cannot connect", Toast.LENGTH_SHORT).show();
						}
					});
					tryingToConnectIAB = false;
					return;
				}
				Log.d("IAB", "SUCCESS");

				ArrayList<String> additionalSkuList = new ArrayList<String>();
				for (String s : Tileset.purchaseableTilesets)
					additionalSkuList.add(s);
				mHelper.queryInventoryAsync(true, additionalSkuList, queryInvAsync);
				arePurchasesLoaded = false;
				tryingToConnectIAB = false;
			}
		});
	}

	public void clearLoadingScreen()
	{
		Scene currentScene = mEngine.getScene();
		while (currentScene.getChildScene().hasChildScene())
			currentScene = currentScene.getChildScene();
		if (currentScene.getChildScene() instanceof LoadingScene)
			((TilesMenuScene) currentScene).setChildSceneNull();
		backEnabled = true;
	}

	public void queryPurchases()
	{

		if (!isQuerying)
		{
			isQuerying = true;
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					mHelper.queryInventoryAsync(true, additionalSkuList, queryInvAsync);

				}
			});
		}
	}

}
