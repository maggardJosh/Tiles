package com.lionsteel.reflexmulti;

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
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;

import com.flurry.android.FlurryAgent;
import com.lionsteel.reflexmulti.BaseClasses.GameScene;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuScene;
import com.lionsteel.reflexmulti.Scenes.GameScenes.LoadingScene;
import com.lionsteel.reflexmulti.Scenes.GameScenes.NonStopGameScene;
import com.lionsteel.reflexmulti.Scenes.GameScenes.ReflexGameScene;
import com.lionsteel.reflexmulti.Scenes.MenuScenes.BackgroundMenuScene;
import com.lionsteel.reflexmulti.Scenes.MenuScenes.MainMenuScene;
import com.lionsteel.reflexmulti.Scenes.MenuScenes.QuitPromptScene;
import com.lionsteel.reflexmulti.Scenes.MenuScenes.SetupScene;
import com.lionsteel.reflexmulti.Scenes.MenuScenes.SplashScene;

public class ReflexActivity extends BaseGameActivity implements ReflexConstants
{

	private static ReflexActivity	instance;

	private GameScene				gameScene;
	private MainMenuScene			mainMenuScene;
	private SplashScene				splashScene;
	private BackgroundMenuScene		backgroundScene;
	private QuitPromptScene			menuQuitPromptScene;
	private QuitPromptScene			gameQuitPromptScene;

	private LoadingScene			loadingScene;

	public boolean					backEnabled	= true;

	public static ReflexActivity getInstance()
	{
		if (instance == null)
			instance = new ReflexActivity();
		return instance;
	}

	@Override
	protected void onStart()
	{
		FlurryAgent.onStartSession(this, "test3ZV4J886JJR56QBBF9YX");
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
	public EngineOptions onCreateEngineOptions()
	{
		instance = this;
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		splashScene = new SplashScene();

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
							ReflexActivity.endGameEvent();
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
					//TODO: Race Mode
					gameScene = new ReflexGameScene();
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

		if (!parentScene.hasChildScene() && parentScene instanceof GameScene)
		{
			((GameScene) parentScene).transitionChildScene(gameQuitPromptScene);
			return;
		}

		while (parentScene.getChildScene().hasChildScene())
			parentScene = parentScene.getChildScene();

		if (parentScene instanceof BackgroundMenuScene)
		{
			showQuitPrompt((ReflexMenuScene) parentScene.getChildScene());
			return;
		}

		if (parentScene.getChildScene() instanceof QuitPromptScene)
		{
			((QuitPromptScene) parentScene.getChildScene()).callQuitAction();
			return;
		}

		parentScene.clearChildScene();
	}

	public void showQuitPrompt(ReflexMenuScene scene)
	{
		scene.transitionChildScene(menuQuitPromptScene, true);
	}

	public void backToMainMenu()
	{
		//Clear all child scenes
		ReflexMenuScene parentScene = mainMenuScene;
		while (parentScene.hasChildScene())
		{
			final ReflexMenuScene childScene = (ReflexMenuScene) parentScene.getChildScene();
			parentScene.setChildSceneNull();
			parentScene = childScene;
		}

		mainMenuScene.setX(0);
		mainMenuScene.logFlurryEvent();
		mEngine.setScene(backgroundScene);

	}

}
