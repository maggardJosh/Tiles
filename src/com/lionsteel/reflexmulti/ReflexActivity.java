package com.lionsteel.reflexmulti;

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

import com.lionsteel.reflexmulti.SetupScene.GameMode;
import com.lionsteel.reflexmulti.Scenes.GameScene;
import com.lionsteel.reflexmulti.Scenes.LoadingScene;
import com.lionsteel.reflexmulti.Scenes.MainMenuScene;
import com.lionsteel.reflexmulti.Scenes.OneTileGameScene;
import com.lionsteel.reflexmulti.Scenes.ReflexMenuScene;
import com.lionsteel.reflexmulti.Scenes.StreamGameScene;

public class ReflexActivity extends BaseGameActivity implements ReflexConstants
{
	
	private static ReflexActivity	instance;
	
	private GameScene				gameScene;
	private MainMenuScene			mainMenuScene;
	private SplashScene				splashScene;
	private BackgroundMenuScene		backgroundScene;
	
	private LoadingScene			loadingScene;
	
	public boolean					backEnabled	= true;
	
	public static ReflexActivity getInstance()
	{
		if (instance == null)
			instance = new ReflexActivity();
		return instance;
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
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception
	{
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception
	{
		//	gameScene = new OneTileGameScene();
		
		splashScene = new SplashScene();
		
		mEngine.registerUpdateHandler(new TimerHandler(.1f, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				
				SharedResources.getInstance(); //Make sure shared resources is initialized during splash screen.
				loadingScene = new LoadingScene();
				
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
							public void onModifierStarted(
									IModifier<IEntity> pModifier, IEntity pItem)
							{
								
							}
							
							@Override
							public void onModifierFinished(
									IModifier<IEntity> pModifier, IEntity pItem)
							{
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
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
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
		
		mEngine.registerUpdateHandler(new TimerHandler(.1f, new ITimerCallback()
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
					case GameMode.ONE_TILE:
						gameScene = new OneTileGameScene();
						break;
					case GameMode.STREAM:
						gameScene = new StreamGameScene();
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
	
	@Override
	public void onBackPressed()
	{
		if (!backEnabled)
			return;
		Scene parentScene = this.mEngine.getScene();
		
		if (parentScene instanceof GameScene)
		{
			backToMainMenu();
			return;
		}
		
		
		while (parentScene.getChildScene().hasChildScene())
			parentScene = parentScene.getChildScene();
		

		if (parentScene instanceof BackgroundMenuScene)
		{
			super.onBackPressed();
			return;
		}
		
		parentScene.clearChildScene();
	}
	
	public void backToMainMenu()
	{
		//Clear all child scene's
		ReflexMenuScene parentScene = mainMenuScene;
		while (parentScene.hasChildScene())
		{
			final ReflexMenuScene childScene = (ReflexMenuScene) parentScene.getChildScene();
			parentScene.setChildSceneNull();
			parentScene = childScene;
		}
		
		mainMenuScene.setX(0);
		mEngine.setScene(backgroundScene);
	}
	
}
