package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public abstract class ReflexMenuScene extends Scene implements ReflexConstants
{
	final ReflexActivity	activity;
	final Sprite			backArrow;

	public ReflexMenuScene()
	{
		activity = ReflexActivity.getInstance();

		this.setTouchAreaBindingOnActionDownEnabled(true);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/ReflexMenuShared/");
		final BitmapTextureAtlas sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256);
		final TextureRegion backArrowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "backArrow.png", 0, 0);
		sceneAtlas.load();

		backArrow = new Sprite(BACK_ARROW_PADDING, BACK_ARROW_PADDING, backArrowRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{

				if (this.isVisible() && pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
					mParentScene.clearChildScene();

				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};

		////final Sprite backgroundSprite = new Sprite(0,0,SharedResources.getInstance().backgroundRegion, activity.getVertexBufferObjectManager());
		//backgroundSprite.setZIndex(BACKGROUND_Z);
		//this.attachChild(backgroundSprite);
		this.attachChild(backArrow);
		backArrow.setZIndex(FOREGROUND_Z);
		backArrow.setVisible(false);
		this.registerTouchArea(backArrow);
		this.sortChildren(false);
	}

	protected abstract void registerTouchAreas();

	public void showBackArrow()
	{
		backArrow.setVisible(true);
	}
	
	public void transitionChildScene(final ReflexMenuScene childScene)
	{
		transitionChildScene(childScene, false);
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
					childScene.registerTouchArea(childScene.backArrow);
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
