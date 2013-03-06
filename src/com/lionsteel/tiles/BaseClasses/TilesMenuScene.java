package com.lionsteel.tiles.BaseClasses;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.GameScenes.GameOverScreen;
import com.lionsteel.tiles.Scenes.GameScenes.PauseScene;
import com.lionsteel.tiles.Scenes.GameScenes.PracticeGameOverScene;

public abstract class TilesMenuScene extends Scene implements TilesConstants
{
	protected final TilesMainActivity	activity;
	final TilesMenuButton				backButton;

	final ArrayList<TilesMenuButton>	buttonList	= new ArrayList<TilesMenuButton>();

	public TilesMenuScene()
	{
		activity = TilesMainActivity.getInstance();

		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);

		backButton = new TilesMenuButton(SharedResources.getInstance().backArrowRegion, new Runnable()
		{
			@Override
			public void run()
			{
				if (backButton.isVisible() && activity.backEnabled)
					mParentScene.clearChildScene();
			}
		});

		this.attachChild(backButton);
		backButton.setZIndex(FOREGROUND_Z);
		backButton.setVisible(false);
		backButton.registerOwnTouchArea(this);
		backButton.setPosition(BACK_ARROW_PADDING, BACK_ARROW_PADDING);
		this.sortChildren(false);
	}

	public abstract void logFlurryEvent();

	public void unsetAllButtons()
	{
		for (TilesMenuButton button : buttonList)
			button.unsetButton();
	}

	private void registerButtonTouchAreas()
	{
		for (TilesMenuButton button : buttonList)
			button.registerOwnTouchArea(this);
	}

	public void registerTouchAreas()
	{
		registerButtonTouchAreas();
		backButton.registerOwnTouchArea(this);
	}

	public void showBackArrow()
	{
		backButton.setVisible(true);
	}

	public void transitionChildScene(final TilesMenuScene childScene)
	{

		transitionChildScene(childScene, false);
	}

	public void addButton(TilesMenuButton button)
	{
		buttonList.add(button);
		this.attachChild(button);
	}

	protected void removeButton(TilesMenuButton button)
	{
		buttonList.remove(button);
		this.detachChild(button);
	}

	public void transitionChildScene(final TilesMenuScene childScene, final boolean overlay)
	{
		childScene.logFlurryEvent();
		childScene.initScene();

		setChildScene(childScene, false, false, true);
		if (childScene.hasChildScene())
			childScene.clearChildScene();
		if (overlay)
		{
			childScene.enterScene();
			childScene.setX(0);
			childScene.registerTouchAreas();

		} else
		{
			childScene.showBackArrow();
			TilesMainActivity.getInstance().backEnabled = false;
			childScene.setX(CAMERA_WIDTH);
			transitionOff();
			childScene.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, CAMERA_WIDTH, 0)
			{
				@Override
				protected void onModifierFinished(IEntity pItem)
				{
					TilesMainActivity.getInstance().backEnabled = true;
					childScene.registerTouchAreas();
					childScene.enterScene();
					childScene.backButton.registerOwnTouchArea(childScene);
					super.onModifierFinished(pItem);
				}
			});
		}
	}

	public abstract void initScene();

	protected void enterScene()
	{
	}

	protected abstract void exitScene();

	protected void transitionOff()
	{
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), -CAMERA_WIDTH));
		if (!(this instanceof PauseScene || this instanceof GameOverScreen || this instanceof PracticeGameOverScene))
			activity.moveBackground(false);
	}

	public void setChildSceneNull()
	{
		if (this.getChildScene() instanceof TilesMenuScene)
		{
			((TilesMenuScene) this.getChildScene()).clearTouchAreas();
		}
		super.clearChildScene();
	}

	public void clearChildScene(final Runnable onFinished)
	{
		logFlurryEvent();
		if (mChildScene instanceof TilesMenuScene)
			((TilesMenuScene) mChildScene).exitScene();
		initScene();

		TilesMainActivity.getInstance().backEnabled = false;
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), 0));
		mChildScene.clearTouchAreas();
		this.getChildScene().registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, this.getChildScene().getX(), CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				TilesMainActivity.getInstance().backEnabled = true;
				setChildSceneNull();
				enterScene();
				if (onFinished != null)
					onFinished.run();
				super.onModifierFinished(pItem);
			}
		});
		if (!(this instanceof PauseScene || this instanceof GameOverScreen || this instanceof PracticeGameOverScene))
			activity.moveBackground(true);

	}

	@Override
	public void clearChildScene()
	{
		clearChildScene(null);
	}

}
