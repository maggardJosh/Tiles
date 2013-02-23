package com.lionsteel.tiles;

import java.util.HashMap;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicLibrary;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Scenes.GameScenes.FreePlayGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.GameOverScreen;
import com.lionsteel.tiles.Scenes.GameScenes.LoadingScene;
import com.lionsteel.tiles.Scenes.GameScenes.NonStopGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.PauseScene;
import com.lionsteel.tiles.Scenes.GameScenes.PracticeGameOverScene;
import com.lionsteel.tiles.Scenes.GameScenes.RaceGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.ReflexGameScene;
import com.lionsteel.tiles.Scenes.GameScenes.TimeAttackGameScene;
import com.lionsteel.tiles.Scenes.MenuScenes.BackgroundMenuScene;
import com.lionsteel.tiles.Scenes.MenuScenes.MainMenuScene;
import com.lionsteel.tiles.Scenes.MenuScenes.QuitPromptScene;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;
import com.lionsteel.tiles.Scenes.MenuScenes.SplashScene;

public class TilesMainActivity extends BaseGameActivity implements TilesConstants
{

	private static TilesMainActivity	instance;

	private GameScene					gameScene;
	private MainMenuScene				mainMenuScene;
	private SplashScene					splashScene;
	private BackgroundMenuScene			backgroundScene;
	private QuitPromptScene				menuQuitPromptScene;
	private QuitPromptScene				gameQuitPromptScene;

	private LoadingScene				loadingScene;

	public boolean						backEnabled	= true;

	public SharedPreferences			sharedPrefs;

	private Music						currentMusic;

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
		if (currentMusic != null)
			currentMusic.resume();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		if (currentMusic != null)
			currentMusic.pause();
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
								playSong(SharedResources.getInstance().menuMusic);
								mEngine.setScene(backgroundScene);
							}
						});
					}
				}));

			}
		}));

		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);

	}

	public void playSong(final Music newSong)
	{
		
		if (currentMusic != null)
		{
			mEngine.registerUpdateHandler(new TimerHandler(.1f, true, new ITimerCallback()
			{

				@Override
				public void onTimePassed(TimerHandler pTimerHandler)
				{
					if (currentMusic.getVolume() > SONG_TRANSITION_SPEED)
						currentMusic.setVolume(currentMusic.getVolume() - SONG_TRANSITION_SPEED);
					else
					{
						currentMusic.setVolume(0);
						currentMusic = null;
						mEngine.unregisterUpdateHandler(pTimerHandler);
						playSong(newSong);
					}
				}
			}));
			return;
		}
		if (newSong == null)
			return;
		currentMusic = newSong;
		newSong.play();
		newSong.setVolume(0);
		newSong.setLooping(true);
		mEngine.registerUpdateHandler(new TimerHandler(.1f, true, new ITimerCallback()
		{

			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				if (newSong.getVolume() < 1 - SONG_TRANSITION_SPEED)
					newSong.setVolume(newSong.getVolume() + SONG_TRANSITION_SPEED);
				else
				{
					newSong.setVolume(1.0f);
					mEngine.unregisterUpdateHandler(pTimerHandler);
				}
			}
		}));
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	public void load(final Runnable loadAction)
	{
		Scene currentScene = mEngine.getScene();
		while (currentScene.hasChildScene())
			currentScene = currentScene.getChildScene();
		loadingScene.setPosition(0, 0);
		currentScene.setChildScene(loadingScene, false, false, true);

		mEngine.registerUpdateHandler(new TimerHandler(.2f, new ITimerCallback()
		{

			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				loadAction.run();
			}
		}));
	}

	@Override
	public void onDestroyResources() throws Exception
	{
		SharedResources.clear();
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
				}
				mEngine.setScene(gameScene);
			}
		});

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
		playSong(SharedResources.getInstance().menuMusic);
		//Clear all child scenes
		TilesMenuScene parentScene = mainMenuScene;
		while (parentScene.hasChildScene())
		{
			final TilesMenuScene childScene = (TilesMenuScene) parentScene.getChildScene();
			parentScene.setChildSceneNull();
			parentScene = childScene;
		}
		backgroundScene.moveBackground(true);
		backgroundScene.moveBackground(true);

		mainMenuScene.setX(0);
		mainMenuScene.logFlurryEvent();
		mEngine.setScene(backgroundScene);

	}

}
