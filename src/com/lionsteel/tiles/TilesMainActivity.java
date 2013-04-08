package com.lionsteel.tiles;

import java.io.IOException;
import java.util.HashMap;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.BaseClasses.GameScene.GameState;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.TilesTutorial;
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
		FlurryAgent.onStartSession(this, "ZVDZY326DS57FS4KCXTS");
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

	}

	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
	{
		Log.d("Tiles", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onDestroy()
	{
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
			try
			{
				Tileset.tilesetList = getAssets().list("gfx/tilesets");
			} catch (IOException e)
			{
				e.printStackTrace();
			}

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
					if (gameScene.getGameState() == GameState.TUTORIAL_ANIM)
						TilesTutorial.getInstance().cancelTutorial();
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
	public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		splashScene = new SplashScene();
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);

		instance.getEngine().registerUpdateHandler(new TimerHandler(.5f, new ITimerCallback()
		{

			@Override
			public void onTimePassed(TimerHandler arg0)
			{
				instance.getEngine().unregisterUpdateHandler(arg0);
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

		}));

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
		TilesTutorial.clear();
		loadTaskInstance = null;
	}

	@Override
	public void onDestroyResources() throws Exception
	{
		clearAllSingletons();
		super.onDestroyResources();
	}

	private int	gameCount	= 0;

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
			gameCount++;
			Log.v("GameCount", "" + gameCount);

			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			SetupScene.getTileset().createGameAssets();
			SharedResources.getInstance().loadGameAssets();
			TilesTutorial.getInstance();
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
					mEngine.setScene(gameScene);
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

		GameScene gameScene = null;
		if (mEngine.getScene() instanceof GameScene)
			gameScene = (GameScene) mEngine.getScene();

		mEngine.setScene(backgroundScene);
		gameScene.dispose();
	}

}
