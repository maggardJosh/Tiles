package com.lionsteel.reflexmulti.Scenes;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public abstract class ReflexMenuScene extends Scene implements ReflexConstants
{
	final ReflexActivity				activity;
	final ReflexMenuButton				backButton;

	final ArrayList<ReflexMenuButton>	buttonList	= new ArrayList<ReflexMenuButton>();

	public ReflexMenuScene()
	{
		activity = ReflexActivity.getInstance();

		this.setTouchAreaBindingOnActionDownEnabled(true);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/ReflexMenuShared/");
		final BitmapTextureAtlas sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256);
		final TextureRegion backArrowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "backArrow.png", 0, 0);
		sceneAtlas.load();

		backButton = new ReflexMenuButton(backArrowRegion, new Runnable()
		{
			@Override
			public void run()
			{
				if (backButton.isVisible())
					mParentScene.clearChildScene();
			}
		});

		this.attachChild(backButton);
		backButton.setZIndex(FOREGROUND_Z);
		backButton.setVisible(false);
		backButton.registerOwnTouchArea(this);
		this.sortChildren(false);
	}

	private void registerButtonTouchAreas()
	{
		for (ReflexMenuButton button : buttonList)
			button.registerOwnTouchArea(this);
	}

	protected void registerTouchAreas()
	{
		registerButtonTouchAreas();
		backButton.registerOwnTouchArea(this);
	}

	public void showBackArrow()
	{
		backButton.setVisible(true);
	}

	public void transitionChildScene(final ReflexMenuScene childScene)
	{
		transitionChildScene(childScene, false);
	}

	protected void addButton(ReflexMenuButton button)
	{
		buttonList.add(button);
		this.attachChild(button);
	}

	protected void removeButton(ReflexMenuButton button)
	{
		buttonList.remove(button);
		this.detachChild(button);
	}

	public void transitionChildScene(final ReflexMenuScene childScene, final boolean overlay)
	{

		setChildScene(childScene, false, false, true);
		if (childScene.hasChildScene())
			childScene.clearChildScene();
		if (overlay)
		{
			childScene.setX(0);
			childScene.registerTouchAreas();

		} else
		{
			childScene.showBackArrow();
			ReflexActivity.getInstance().backEnabled = false;
			childScene.setX(CAMERA_WIDTH);
			transitionOff();
			childScene.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, CAMERA_WIDTH, 0)
			{
				@Override
				protected void onModifierFinished(IEntity pItem)
				{
					ReflexActivity.getInstance().backEnabled = true;
					childScene.registerTouchAreas();
					childScene.backButton.registerOwnTouchArea(childScene);
					super.onModifierFinished(pItem);
				}
			});
		}
	}

	protected void transitionOff()
	{
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), -CAMERA_WIDTH));
	}

	public void setChildSceneNull()
	{
		if (this.getChildScene() instanceof ReflexMenuScene)
			((ReflexMenuScene) this.getChildScene()).clearTouchAreas();
		super.clearChildScene();
	}

	@Override
	public void clearChildScene()
	{
		if (this.mChildScene instanceof LoadingScene)
		{

			this.mChildScene = null;
			return;
		}
		ReflexActivity.getInstance().backEnabled = false;
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), 0));

		this.getChildScene().registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, this.getChildScene().getX(), CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				ReflexActivity.getInstance().backEnabled = true;
				setChildSceneNull();
				super.onModifierFinished(pItem);
			}
		});
	}

}
