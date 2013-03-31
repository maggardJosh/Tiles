package com.lionsteel.tiles.Entities;

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
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
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
	private final Sprite[]				raceArrowSprite			= new Sprite[2];
	private final Sprite[]				yourTilesAreaSprite		= new Sprite[2];
	private final Sprite[]				yourTilesWordsSprite	= new Sprite[2];
	private final Sprite[]				matchWordsSprite		= new Sprite[2];
	private final Sprite[]				raceAreaSprite			= new Sprite[2];
	private final Sprite				matchAreaSprite;

	@Override
	public void dispose()
	{
		tutorialAtlas.unload();
		super.dispose();
	}

	private TilesTutorial()
	{
		activity = TilesMainActivity.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/Tutorial/");
		tutorialAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 1024, BitmapTextureFormat.RGBA_4444);
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
			raceArrowSprite[i] = new Sprite(0, 0, arrowRegion, activity.getVertexBufferObjectManager());
			matchWordsSprite[i] = new Sprite(0, 0, matchWordsRegion, activity.getVertexBufferObjectManager());
			raceAreaSprite[i] = new Sprite(0, 0, raceAreaRegion, activity.getVertexBufferObjectManager());
			yourTilesAreaSprite[i] = new Sprite(0, 0, yourTilesAreaRegion, activity.getVertexBufferObjectManager());
			yourTilesWordsSprite[i] = new Sprite(0, 0, yourTilesWordsRegion, activity.getVertexBufferObjectManager());

		}
		matchAreaSprite = new Sprite(0, 0, matchAreaRegion, activity.getVertexBufferObjectManager());

		yourTilesWordsSprite[PLAYER_TWO].setRotation(180);
		matchWordsSprite[PLAYER_TWO].setRotation(180);
		arrowSprite[PLAYER_TWO].setRotation(180);
		raceArrowSprite[PLAYER_ONE].setRotation(45);
		raceArrowSprite[PLAYER_TWO].setRotation(180 + 45);

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

				yourTilesAreaSprite[i].setPosition(82, 461);
				yourTilesWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - yourTilesWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - yourTilesWordsSprite[i].getHeight()) / 2);
				matchWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - matchWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - matchWordsSprite[i].getHeight()) / 2);

				break;
			case PLAYER_TWO:
				yourTilesAreaSprite[PLAYER_TWO].setPosition(82, 0);
				yourTilesWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - yourTilesWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - yourTilesWordsSprite[i].getHeight()) / 2);
				matchWordsSprite[i].setPosition(yourTilesAreaSprite[i].getX() + (yourTilesAreaSprite[i].getWidth() - matchWordsSprite[i].getWidth()) / 2, yourTilesAreaSprite[i].getY() + (yourTilesAreaSprite[i].getHeight() - matchWordsSprite[i].getHeight()) / 2);
				break;
			}
		}

		matchAreaSprite.setPosition(yourTilesAreaSprite[PLAYER_ONE].getX(), (yourTilesAreaSprite[PLAYER_ONE].getY() + yourTilesAreaSprite[PLAYER_TWO].getHeight() - matchAreaSprite.getHeight()) / 2);
		for (int i = 0; i < 2; i++)
			arrowSprite[i].setX(matchAreaSprite.getX() + (matchAreaSprite.getWidth() - arrowSprite[i].getWidth()) / 2);
		final int RACE_AREA_PADDING = 4;
		raceAreaSprite[PLAYER_ONE].setPosition(matchAreaSprite.getX() + matchAreaSprite.getWidth() - raceAreaSprite[PLAYER_ONE].getWidth() - RACE_AREA_PADDING, matchAreaSprite.getY());
		raceAreaSprite[PLAYER_TWO].setPosition(matchAreaSprite.getX() + RACE_AREA_PADDING, matchAreaSprite.getY());

		arrowSprite[PLAYER_ONE].setY(matchAreaSprite.getY() + matchAreaSprite.getHeight() + ARROW_SPACING);
		arrowSprite[PLAYER_TWO].setY(matchAreaSprite.getY() - arrowSprite[PLAYER_TWO].getHeight() - ARROW_SPACING);

		raceArrowSprite[PLAYER_ONE].setPosition(arrowSprite[PLAYER_ONE]);
		raceArrowSprite[PLAYER_TWO].setPosition(arrowSprite[PLAYER_TWO]);

		attachChild(matchAreaSprite);
		for (int i = 0; i < 2; i++)
		{
			attachChild(arrowSprite[i]);
			attachChild(matchWordsSprite[i]);
			attachChild(yourTilesAreaSprite[i]);
			attachChild(raceAreaSprite[i]);
			attachChild(yourTilesWordsSprite[i]);
			attachChild(raceArrowSprite[i]);

			arrowSprite[i].setVisible(false);
			matchWordsSprite[i].setVisible(false);
			yourTilesWordsSprite[i].setVisible(false);
			yourTilesAreaSprite[i].setVisible(false);
			raceAreaSprite[i].setVisible(false);
			raceArrowSprite[i].setVisible(false);
		}
		matchAreaSprite.setVisible(false);
	}

	public void startTutorial(final int gameMode, final Runnable endAction)
	{
		switch (gameMode)
		{
		case GameMode.REFLEX:
		case GameMode.NON_STOP:
			startNormalVersus(endAction);
			break;
		case GameMode.RACE:
			startRace(endAction);
			break;
		case GameMode.FREE_PLAY:
		case GameMode.FRENZY:
		case GameMode.TIME_ATTACK:
			startPractice(endAction);
			break;
		}

	}

	public void cancelTutorial()
	{
		arrowSprite[0].clearEntityModifiers();
		for (int i = 0; i < 2; i++)
		{
			arrowSprite[i].setVisible(false);
			matchWordsSprite[i].setVisible(false);
			yourTilesWordsSprite[i].setVisible(false);
			yourTilesAreaSprite[i].setVisible(false);
			raceAreaSprite[i].setVisible(false);
			raceArrowSprite[i].setVisible(false);
		}
		matchAreaSprite.setVisible(false);
	}

	private void startRace(final Runnable endAction)
	{
		for (int i = 0; i < 2; i++)
		{
			yourTilesAreaSprite[i].setVisible(true);
			yourTilesWordsSprite[i].setVisible(true);
		}
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
					raceArrowSprite[i].setVisible(true);
					matchWordsSprite[i].setVisible(true);
					raceAreaSprite[i].setVisible(true);
					yourTilesAreaSprite[i].setVisible(false);
					yourTilesWordsSprite[i].setVisible(false);
				}

				arrowSprite[0].registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						for (int i = 0; i < 2; i++)
						{
							raceArrowSprite[i].setVisible(false);
							matchWordsSprite[i].setVisible(false);
							raceAreaSprite[i].setVisible(false);
						}
						super.onModifierFinished(pItem);
					}
				}, new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						for (int i = 0; i < 2; i++)
						{
							raceArrowSprite[i].setVisible(true);
							matchWordsSprite[i].setVisible(true);
							raceAreaSprite[i].setVisible(true);
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
							raceArrowSprite[i].setVisible(false);
							matchWordsSprite[i].setVisible(false);
							raceAreaSprite[i].setVisible(false);
						}
						detachSelf();
						super.onModifierFinished(pItem);
					}
				});
				super.onModifierFinished(pItem);
			}
		});
	}

	private void startPractice(final Runnable endAction)
	{

		yourTilesAreaSprite[PLAYER_ONE].setVisible(true);
		yourTilesWordsSprite[PLAYER_ONE].setVisible(true);

		arrowSprite[0].registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				yourTilesAreaSprite[PLAYER_ONE].setVisible(false);
				yourTilesWordsSprite[PLAYER_ONE].setVisible(false);
				super.onModifierFinished(pItem);
			}
		}, new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
		{
			protected void onModifierFinished(IEntity pItem)
			{
				yourTilesAreaSprite[PLAYER_ONE].setVisible(true);
				yourTilesWordsSprite[PLAYER_ONE].setVisible(true);
				super.onModifierFinished(pItem);
			};
		}), 4)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				arrowSprite[PLAYER_ONE].setVisible(true);
				matchWordsSprite[PLAYER_ONE].setVisible(true);
				yourTilesAreaSprite[PLAYER_ONE].setVisible(false);
				yourTilesWordsSprite[PLAYER_ONE].setVisible(false);
				matchAreaSprite.setVisible(true);
				arrowSprite[0].registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
				{
					@Override
					protected void onModifierFinished(IEntity pItem)
					{
						arrowSprite[PLAYER_ONE].setVisible(false);
						matchWordsSprite[PLAYER_ONE].setVisible(false);
						matchAreaSprite.setVisible(false);
						super.onModifierFinished(pItem);
					}
				}, new DelayModifier(TUTORIAL_SEGMENT_LENGTH / 8)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						arrowSprite[PLAYER_ONE].setVisible(true);
						matchWordsSprite[PLAYER_ONE].setVisible(true);
						matchAreaSprite.setVisible(true);
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
							arrowSprite[PLAYER_ONE].setVisible(false);
							matchWordsSprite[PLAYER_ONE].setVisible(false);
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

	private void startNormalVersus(final Runnable endAction)
	{
		for (int i = 0; i < 2; i++)
		{
			yourTilesAreaSprite[i].setVisible(true);
			yourTilesWordsSprite[i].setVisible(true);
		}
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
