package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

import com.lionsteel.tiles.TilesScrollHud;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.TilesConstants;

public abstract class TilesScrollableScene extends TilesMenuScene implements TilesConstants, IScrollDetectorListener, IOnSceneTouchListener
{
	protected float							MAX_Y;
	final float								SCROLL_SPEED	= .8f;

	protected final SurfaceScrollDetector	scrollDetector;
	private TilesScrollHud					controlHud;

	public TilesScrollableScene()
	{
		scrollDetector = new SurfaceScrollDetector(this);
		setOnSceneTouchListener(this);
		controlHud = new TilesScrollHud(this);
	}

	@Override
	public void logFlurryEvent()
	{
	}

	@Override
	public void initScene()
	{
		scrollDetector.reset();
		setY(0);
		
	}
	
	@Override
	protected void enterScene()
	{
		activity.getEngine().getCamera().setHUD(controlHud);
		super.enterScene();
	}

	@Override
	public void transitionChildScene(TilesMenuScene childScene)
	{
		activity.getEngine().getCamera().setHUD(null);
		super.transitionChildScene(childScene);
	}

	@Override
	protected void exitScene()
	{
		activity.getEngine().getCamera().setHUD(null);
	}

	@Override
	protected void transitionOff()
	{

		super.transitionOff();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{

		scrollDetector.onSceneTouchEvent(pScene, pSceneTouchEvent);
		return true;
	}

	private void boundScene()
	{
		if (getY() - CAMERA_HEIGHT < -MAX_Y)
			setY(-MAX_Y + CAMERA_HEIGHT);
		if (getY() > 0)
			setY(0);
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		activity.backEnabled = false;
		clearEntityModifiers();
		unsetAllButtons();
		this.controlHud.unsetButtons();
		this.setY(getY() + pDistanceY * SCROLL_SPEED);
		boundScene();
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		clearEntityModifiers();
		this.controlHud.unsetButtons();
		this.setY(getY() + pDistanceY * SCROLL_SPEED);
		boundScene();
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		activity.backEnabled = true;
		clearEntityModifiers();
		this.controlHud.unsetButtons();
		this.setY(getY() + pDistanceY * SCROLL_SPEED);
		boundScene();
	}

	public float boundY(float targetY)
	{
		if (targetY - CAMERA_HEIGHT < -MAX_Y)
			targetY = -MAX_Y + CAMERA_HEIGHT;
		if (targetY > 0)
			targetY = 0;
		return targetY;
	}

}
