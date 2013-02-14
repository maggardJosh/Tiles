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
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Entities.DifficultyEntity;

public class SkillSelectScene extends TilesMenuScene
{
	TilesMainActivity			activity;

	final TilesMenuButton	easyButton;
	final TilesMenuButton	normalButton;
	final TilesMenuButton	hardButton;
	final TilesMenuButton	insaneButton;

	DifficultyEntity[]		diffEntities	= new DifficultyEntity[3];

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.DIFFICULTY_MENU);
	}

	public SkillSelectScene()
	{
		super();

		activity = TilesMainActivity.getInstance();
		final BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);

		this.setBackgroundEnabled(false);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SkillSelectScene/");

		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "title.png");//, 0, 0);
		final TextureRegion easyButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "easy.png");//, (int) titleRegion.getWidth(), 0);
		final TextureRegion normalButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "normal.png");//, (int) easyButtonRegion.getTextureX(), (int) easyButtonRegion.getHeight());
		final TextureRegion hardButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "hard.png");//, (int) easyButtonRegion.getTextureX(), (int) (normalButtonRegion.getTextureY() + normalButtonRegion.getHeight()));
		final TextureRegion insaneButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "insane.png");//, (int) easyButtonRegion.getTextureX(), (int) (hardButtonRegion.getTextureY() + hardButtonRegion.getHeight()));

		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		final Sprite titleSprite = new Sprite(0, 0, titleRegion, activity.getVertexBufferObjectManager());
		diffEntities = new DifficultyEntity[4];
		for (int x = 0; x < 4; x++)
		{
			diffEntities[x] = new DifficultyEntity(x, SetupScene.getTileset());
			diffEntities[x].fadeIn();
		}

		easyButton = new TilesMenuButton(easyButtonRegion, new Runnable()
		{

			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.EASY);
				mParentScene.clearChildScene();
			}
		});
		easyButton.center(titleSprite.getHeight());
		easyButton.attachChild(diffEntities[Difficulty.EASY]);
		addButton(easyButton);

		normalButton = new TilesMenuButton(normalButtonRegion, new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.NORMAL);
				mParentScene.clearChildScene();

			}
		});
		normalButton.center(easyButton.getBottom());
		normalButton.attachChild(diffEntities[Difficulty.NORMAL]);
		addButton(normalButton);

		hardButton = new TilesMenuButton(hardButtonRegion, new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.HARD);
				mParentScene.clearChildScene();
			}
		});
		hardButton.center(normalButton.getBottom());
		hardButton.attachChild(diffEntities[Difficulty.HARD]);
		addButton(hardButton);

		insaneButton = new TilesMenuButton(insaneButtonRegion, new Runnable()
		{
			@Override
			public void run()
			{
				SetupScene.setDifficulty(Difficulty.INSANE);
				mParentScene.clearChildScene();
			}
		});
		insaneButton.center(hardButton.getBottom());
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

}
