package com.lionsteel.reflexmulti;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.os.Bundle;
import android.view.Menu;

public class ReflexActivity extends BaseGameActivity implements ReflexConstants
{
	
	private static ReflexActivity instance;
	
	public static ReflexActivity getInstance()
	{
		if(instance == null)
			instance = new ReflexActivity();
		return instance;
	}
	
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		instance = this;
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		
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
		pOnCreateSceneCallback.onCreateSceneFinished(new GameScene());
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}
