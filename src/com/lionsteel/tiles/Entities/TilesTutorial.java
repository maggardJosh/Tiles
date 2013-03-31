package com.lionsteel.tiles.Entities;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;

public class TilesTutorial extends Entity implements TilesConstants
{
	private static TilesTutorial	instance;

	public synchronized static TilesTutorial getInstance()
	{
		if (instance == null)
			instance = new TilesTutorial();
		return instance;
	}

	public static void clear()
	{
		instance = null;
	}

	private TilesMainActivity			activity;
	private BuildableBitmapTextureAtlas	tutorialAtlas;

	private final Sprite[]				arrowSprite				= new Sprite[2];
	private final Sprite[]				yourTilesAreaSprite		= new Sprite[2];
	private final Sprite[]				yourTilesWordsSprite	= new Sprite[2];
	private final Sprite[]				matchWordsSprite		= new Sprite[2];
	private final Sprite[]				raceAreaSprite			= new Sprite[2];
	private final Sprite				matchAreaSprite;

	@Override
	public void dispose()
	{

		super.dispose();
	}

	private TilesTutorial()
	{
		activity = TilesMainActivity.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/Tutorial/");
		tutorialAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		final TextureRegion arrowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialAtlas, activity, "tutorialArrow.png");
		final TextureRegion matchWordsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialAtlas, activity, "tutorialMatchWords.png");
		final TextureRegion matchAreaRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialAtlas, activity, "tutorialMatchArea.png");
		final TextureRegion yourTilesWordsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialAtlas, activity, "tutorialYourTilesWords.png");
		final TextureRegion yourTilesAreaRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialAtlas, activity, "tutorialYourTilesArea.png");
		final TextureRegion raceAreaRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialAtlas, activity, "tutorialRaceArea.png");

		try
		{
			tutorialAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			tutorialAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		for (int i = 0; i < 2; i++)
		{
			arrowSprite[i] = new Sprite(0, 0, arrowRegion, activity.getVertexBufferObjectManager());
			matchWordsSprite[i] = new Sprite(0, 0, matchWordsRegion, activity.getVertexBufferObjectManager());
			raceAreaSprite[i] = new Sprite(0, 0, raceAreaRegion, activity.getVertexBufferObjectManager());
			yourTilesAreaSprite[i] = new Sprite(0, 0, yourTilesAreaRegion, activity.getVertexBufferObjectManager());
			yourTilesWordsSprite[i] = new Sprite(0, 0, yourTilesWordsRegion, activity.getVertexBufferObjectManager());

		}
		matchAreaSprite = new Sprite(0, 0, matchAreaRegion, activity.getVertexBufferObjectManager());

		yourTilesWordsSprite[PLAYER_TWO].setRotation(180);
		matchWordsSprite[PLAYER_TWO].setRotation(180);
		arrowSprite[PLAYER_TWO].setRotation(180);

		setPositions();

	}

	final int	ARROW_SPACING	= 15;

	private void setPositions()
	{
		for (int i = 0; i < 2; i++)
		{
			switch (i)
			{
			case PLAYER_ONE:

				yourTilesAreaSprite[i].setPosition(81, 461);
				yourTilesWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - yourTilesWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - yourTilesWordsSprite[i].getHeight()) / 2);
				matchWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - matchWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - matchWordsSprite[i].getHeight()) / 2);

				break;
			case PLAYER_TWO:
				yourTilesAreaSprite[PLAYER_TWO].setPosition(81, 0);
				yourTilesWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - yourTilesWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - yourTilesWordsSprite[i].getHeight()) / 2);
				matchWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - matchWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - matchWordsSprite[i].getHeight()) / 2);
				break;
			}
		}

		matchAreaSprite.setPosition(yourTilesAreaSprite[PLAYER_ONE].getX(), (yourTilesAreaSprite[PLAYER_ONE].getY() + yourTilesAreaSprite[PLAYER_TWO].getHeight() - matchAreaSprite.getHeight()) / 2);
		for (int i = 0; i < 2; i++)
			arrowSprite[i].setX(matchAreaSprite.getX() + (matchAreaSprite.getWidth() - arrowSprite[i].getWidth()) / 2);

		arrowSprite[PLAYER_ONE].setY(matchAreaSprite.getY() + matchAreaSprite.getHeight() + ARROW_SPACING);
		arrowSprite[PLAYER_TWO].setY(matchAreaSprite.getY() - arrowSprite[PLAYER_TWO].getHeight() - ARROW_SPACING);
	}

	public void startTutorial(final int gameMode, final Runnable endAction)
	{
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				detachChildren();
				switch (gameMode)
				{
				case GameMode.REFLEX:
				case GameMode.NON_STOP:

					startNormalVersus(endAction);

					break;
				case GameMode.RACE:
					//Race tutorial
					break;
				case GameMode.FREE_PLAY:
				case GameMode.FRENZY:
				case GameMode.TIME_ATTACK:
					//Single Player game tutorial
					break;
				}

			}
		});
	}

	

	private void startNormalVersus(final Runnable endAction)
	{
		for (int i = 0; i < 2; i++)
		{
			attachChild(arrowSprite[i]);
			attachChild(matchWordsSprite[i]);
			attachChild(yourTilesWordsSprite[i]);
			attachChild(yourTilesAreaSprite[i]);

			arrowSprite[i].setVisible(false);
			matchWordsSprite[i].setVisible(false);
			yourTilesAreaSprite[i].setVisible(true);
			yourTilesWordsSprite[i].setVisible(true);
		}
		attachChild(matchAreaSprite);
		matchAreaSprite.setVisible(false);
		arrowSprite[0].registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				for (int i = 0; i < 2; i++)
				{
					yourTilesAreaSprite[i].setVisible(false);
					yourTilesWordsSprite[i].setVisible(false);
				}
				super.onModifierFinished(pItem);
			}
		}, new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
		{
			protected void onModifierFinished(IEntity pItem)
			{
				for (int i = 0; i < 2; i++)
				{
					yourTilesAreaSprite[i].setVisible(true);
					yourTilesWordsSprite[i].setVisible(true);
				}
				super.onModifierFinished(pItem);
			};
		}), 4)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				for (int i = 0; i < 2; i++)
				{
					arrowSprite[i].setVisible(true);
					matchWordsSprite[i].setVisible(true);
					yourTilesAreaSprite[i].setVisible(false);
					yourTilesWordsSprite[i].setVisible(false);
				}
				matchAreaSprite.setVisible(true);
				arrowSprite[0].registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						for (int i = 0; i < 2; i++)
						{
							arrowSprite[i].setVisible(false);
							matchWordsSprite[i].setVisible(false);
							matchAreaSprite.setVisible(false);
						}
						super.onModifierFinished(pItem);
					}
				}, new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						for (int i = 0; i < 2; i++)
						{
							arrowSprite[i].setVisible(true);
							matchWordsSprite[i].setVisible(true);
							matchAreaSprite.setVisible(true);
						}
						super.onModifierFinished(pItem);
					};
				}), 4)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						endAction.run();
						for (int i = 0; i < 2; i++)
						{
							arrowSprite[i].setVisible(false);
							matchWordsSprite[i].setVisible(false);
							matchAreaSprite.setVisible(false);
						}
						detachSelf();
						super.onModifierFinished(pItem);
					}
				});
				super.onModifierFinished(pItem);
			}
		});
	}
}
