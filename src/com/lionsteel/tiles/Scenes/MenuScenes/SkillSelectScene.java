package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Entities.DifficultyEntity;

public class SkillSelectScene extends TilesMenuScene
{
	TilesMainActivity		activity;

	final TilesMenuButton	easyButton;
	final TilesMenuButton	normalButton;
	final TilesMenuButton	hardButton;
	final TilesMenuButton	insaneButton;

	DifficultyEntity[]		diffEntities			= new DifficultyEntity[3];

	final int				TITLE_Y					= 50;
	final int				TITLE_BOTTOM_PADDING	= 20;
	final int				BUTTON_PADDING			= 5;

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.DIFFICULTY_MENU);
	}

	public SkillSelectScene()
	{
		super();

		activity = TilesMainActivity.getInstance();
		
		activity.updateLoadProgress("Loading Skill Menu");
		
		final BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256);

		this.setBackgroundEnabled(false);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SkillSelectScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "title.png");

		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		final Sprite titleSprite = new Sprite((CAMERA_WIDTH - titleRegion.getWidth()) / 2, TITLE_Y, titleRegion, activity.getVertexBufferObjectManager());
		diffEntities = new DifficultyEntity[4];
		for (int x = 0; x < 4; x++)
		{
			diffEntities[x] = new DifficultyEntity(x, SetupScene.getTileset());
			diffEntities[x].fadeIn();
		}

		easyButton = new TilesMenuButton(SharedResources.getInstance().difficultyRegion[Difficulty.EASY], new Runnable()
		{

			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.EASY);
				mParentScene.clearChildScene();
			}
		});
		easyButton.center(titleSprite.getHeight() + TITLE_Y + TITLE_BOTTOM_PADDING);
		easyButton.attachChild(diffEntities[Difficulty.EASY]);
		addButton(easyButton);

		normalButton = new TilesMenuButton(SharedResources.getInstance().difficultyRegion[Difficulty.NORMAL], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.NORMAL);
				mParentScene.clearChildScene();

			}
		});
		normalButton.center(easyButton.getBottom() + BUTTON_PADDING);
		normalButton.attachChild(diffEntities[Difficulty.NORMAL]);
		addButton(normalButton);

		hardButton = new TilesMenuButton(SharedResources.getInstance().difficultyRegion[Difficulty.HARD], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.HARD);
				mParentScene.clearChildScene();
			}
		});
		hardButton.center(normalButton.getBottom() + BUTTON_PADDING);
		hardButton.attachChild(diffEntities[Difficulty.HARD]);
		addButton(hardButton);

		insaneButton = new TilesMenuButton(SharedResources.getInstance().difficultyRegion[Difficulty.INSANE], new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.INSANE);
				mParentScene.clearChildScene();
			}
		});
		insaneButton.center(hardButton.getBottom() + BUTTON_PADDING);
		insaneButton.attachChild(diffEntities[Difficulty.INSANE]);
		addButton(insaneButton);

		this.attachChild(titleSprite);

	}

	public void resetGraphics()
	{
		easyButton.clearButtonChildren();
		normalButton.clearButtonChildren();
		hardButton.clearButtonChildren();
		insaneButton.clearButtonChildren();

		for (DifficultyEntity dEntities : diffEntities)
			dEntities.clear();
		diffEntities = new DifficultyEntity[4];
		for (int x = 0; x < 4; x++)
		{
			diffEntities[x] = new DifficultyEntity(x, SetupScene.getTileset());
			diffEntities[x].fadeIn();
		}

		easyButton.attachChild(diffEntities[Difficulty.EASY]);
		normalButton.attachChild(diffEntities[Difficulty.NORMAL]);
		hardButton.attachChild(diffEntities[Difficulty.HARD]);
		insaneButton.attachChild(diffEntities[Difficulty.INSANE]);
	}

	@Override
	public void initScene()
	{
		//Nothing to init
	}

	@Override
	protected void exitScene()
	{
		// TODO Auto-generated method stub

	}

}
