package com.lionsteel.reflexmulti.Scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.SetupScene.Difficulty;
import com.lionsteel.reflexmulti.Entities.DifficultyEntity;

public class SkillSelectScene extends ReflexMenuScene
{
	ReflexActivity		activity;

	final Sprite		easyButton;
	final Sprite		normalButton;
	final Sprite		hardButton;

	DifficultyEntity[]	diffEntities	= new DifficultyEntity[3];

	public SkillSelectScene()
	{
		super();

		activity = ReflexActivity.getInstance();
		final BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);

		this.setBackgroundEnabled(false);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SkillSelectScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "title.png", 0, 0);
		final TextureRegion easyButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "easy.png", (int) titleRegion.getWidth(), 0);
		final TextureRegion normalButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "normal.png", (int) easyButtonRegion.getTextureX(), (int) easyButtonRegion.getHeight());
		final TextureRegion hardButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "hard.png", (int) easyButtonRegion.getTextureX(), (int) (normalButtonRegion.getTextureY() + normalButtonRegion.getHeight()));

		atlas.load();

		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		diffEntities = new DifficultyEntity[3];
		for (int x = 0; x < 3; x++)
		{
			diffEntities[x] = new DifficultyEntity(x, SetupScene.getTileset());
			diffEntities[x].fadeIn();
		}

		easyButton = new Sprite((CAMERA_WIDTH - easyButtonRegion.getWidth()) / 2, 200, easyButtonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
				case TouchEvent.ACTION_UP:
					SetupScene.setDifficulty(Difficulty.EASY);
					mParentScene.clearChildScene();

					break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		easyButton.attachChild(diffEntities[Difficulty.EASY]);
		normalButton = new Sprite((CAMERA_WIDTH - normalButtonRegion.getWidth()) / 2, easyButton.getY() + easyButton.getHeight(), normalButtonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
				case TouchEvent.ACTION_UP:
					SetupScene.setDifficulty(Difficulty.NORMAL);
					mParentScene.clearChildScene();

					break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		normalButton.attachChild(diffEntities[Difficulty.NORMAL]);
		hardButton = new Sprite((CAMERA_WIDTH - hardButtonRegion.getWidth()) / 2, normalButton.getY() + normalButton.getHeight(), hardButtonRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				switch (pSceneTouchEvent.getAction())
				{
				case TouchEvent.ACTION_UP:
					SetupScene.setDifficulty(Difficulty.HARD);
					mParentScene.clearChildScene();

					break;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		hardButton.attachChild(diffEntities[Difficulty.HARD]);

		this.attachChild(titleSprite);
		this.attachChild(easyButton);
		this.attachChild(normalButton);
		this.attachChild(hardButton);

	}

	public void resetGraphics()
	{
		easyButton.detachChildren();
		normalButton.detachChildren();
		hardButton.detachChildren();
		
		for (DifficultyEntity dEntities : diffEntities)
			dEntities.clear();
		diffEntities = new DifficultyEntity[3];
		for (int x = 0; x < 3; x++)
		{
			diffEntities[x] = new DifficultyEntity(x, SetupScene.getTileset());
			diffEntities[x].fadeIn();
		}
		
		easyButton.attachChild(diffEntities[Difficulty.EASY]);
		normalButton.attachChild(diffEntities[Difficulty.NORMAL]);
		hardButton.attachChild(diffEntities[Difficulty.HARD]);
	}

	@Override
	protected void registerTouchAreas()
	{
		registerTouchArea(easyButton);
		registerTouchArea(normalButton);
		registerTouchArea(hardButton);

	}
}
