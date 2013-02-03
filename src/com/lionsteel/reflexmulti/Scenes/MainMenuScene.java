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

public class MainMenuScene extends Scene implements ReflexConstants
{
	ReflexActivity						activity;
	BitmapTextureAtlas					sceneAtlas;
	
	private MultiplayerModeSelectScene	multiplayerModeSelectScene;
	
	final int							BUTTON_SPACING	= 150;
	
	public MainMenuScene()
	{
		activity = ReflexActivity.getInstance();
		
		multiplayerModeSelectScene = new MultiplayerModeSelectScene();
		
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/MainMenuScene/");
		
		final TextureRegion background = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png", 0, (int) background.getHeight());
		final TextureRegion versusRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "versusButton.png", (int) background.getWidth(), 0);
		final TextureRegion practiceRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "practiceButton.png", (int) versusRegion.getTextureX(), (int) versusRegion.getHeight());
		final TextureRegion exitRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "exitButton.png", (int) versusRegion.getTextureX(), (int) (practiceRegion.getTextureY() + practiceRegion.getHeight()));
		
		sceneAtlas.load();
		
		this.setBackgroundEnabled(false);
		
		final Sprite backgroundSprite = new Sprite(0, 0, background, activity.getVertexBufferObjectManager());
		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		final Sprite versusButton = new Sprite((CAMERA_WIDTH - versusRegion.getWidth()) / 2, 230, versusRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						setChildScene(multiplayerModeSelectScene, false, false, true);
						multiplayerModeSelectScene.setX(CAMERA_WIDTH);
						transitionOff();
						multiplayerModeSelectScene.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, CAMERA_WIDTH, 0));
						
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		final Sprite practiceButton = new Sprite((CAMERA_WIDTH - practiceRegion.getWidth()) / 2, 230 + BUTTON_SPACING, practiceRegion, activity.getVertexBufferObjectManager());
		practiceButton.setAlpha(.5f);
		final Sprite exitButton = new Sprite((CAMERA_WIDTH - exitRegion.getWidth()) / 2, 230 + BUTTON_SPACING * 2, exitRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
					case TouchEvent.ACTION_UP:
						activity.finish();
						break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		this.attachChild(backgroundSprite);
		this.attachChild(titleSprite);
		this.attachChild(versusButton);
		this.attachChild(practiceButton);
		this.attachChild(exitButton);
		
		this.registerTouchArea(versusButton);
		this.registerTouchArea(exitButton);
	}
	
	private void transitionOff()
	{
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), -CAMERA_WIDTH));
	}
	
	private void setChildSceneNull()
	{
		super.clearChildScene();
	}
	
	@Override
	public void clearChildScene()
	{
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, getX(), 0));
		this.getChildScene().registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, this.getChildScene().getX(), CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				setChildSceneNull();
				super.onModifierFinished(pItem);
			}
		});
	}
}
