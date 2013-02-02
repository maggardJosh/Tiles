package com.lionsteel.reflexmulti;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.lionsteel.reflexmulti.Scenes.GameScene;
import com.lionsteel.reflexmulti.Scenes.OneTileGameScene;
import com.lionsteel.reflexmulti.Scenes.PrototypeScene;
import com.lionsteel.reflexmulti.Scenes.StreamGameScene;

public class ReflexActivity extends BaseGameActivity implements ReflexConstants
{

	private static ReflexActivity	instance;

	private GameScene		gameScene;

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
		gameScene = new OneTileGameScene();
		pOnCreateSceneCallback.onCreateSceneFinished(new PrototypeScene());
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}
