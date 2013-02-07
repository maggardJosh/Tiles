package com.lionsteel.reflexmulti.Entities;

import java.util.Random;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.SetupScene;
import com.lionsteel.reflexmulti.SetupScene.Difficulty;
import com.lionsteel.reflexmulti.SetupScene.GameMode;
import com.lionsteel.reflexmulti.Scenes.GameScene;

public class Tileset implements ReflexConstants
{
	private ReflexActivity			activity;
	
	private BuildableBitmapTextureAtlas atlas;
	
	private final TextureRegion[]	buttonRegions				= new TextureRegion[NUM_BUTTONS];
	private final TextureRegion		backgroundRegion;
	
	private GameButton[]			playerOneGameButtons		= new GameButton[NUM_BUTTONS];
	private GameButton[]			playerTwoGameButtons		= new GameButton[NUM_BUTTONS];
	private GameButton[]			displayGameButtons			= new GameButton[NUM_BUTTONS * 3];
	
	private Sprite					background;
	
	private boolean					gameAssetsCreated			= false;
	
	private GameButton[]			currentStreamButtons		= new GameButton[3];
	private GameScene				currentScene;
	
	private int						numberOfButtonsToUse		= 3;
	private int						numberOfStreamTilesToSpawn	= 1;
	private final Random			rand;
	private final String			basePath;
	
	private DifficultyEntity		difficultyEntity[]			= new DifficultyEntity[4];
	private TilesetEntity			tilesetEntity;
	
	/**
	 * 
	 * @param basePath
	 *            Name of folder under /gfx/tilesets/ that contains tile images
	 * @param onlyLoadTextureRegions
	 *            Use this when loading in tileset previews. It only loads the parts needed for tileset previews
	 */
	public Tileset(final String basePath, final boolean onlyLoadTextureRegions)
	{
		this.basePath = basePath;
		activity = ReflexActivity.getInstance();
		rand = new Random();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		
		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		for (int i = 0; i < NUM_BUTTONS; i++)
			buttonRegions[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, (i + 1) + ".png");//, (i % 3) * BUTTON_WIDTH, (i / 3) * BUTTON_WIDTH);
		backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "background.png");//, BUTTON_WIDTH * 3, 0);
		try
		{	
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();// load(activity.getTextureManager());
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		if (!onlyLoadTextureRegions)
		{
			
			createDifficultyEntities();
			createTilesetEntity();
			
		}
	}
	
	public void createGameAssets()
	{
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i] = new GameButton(i, this, currentScene, PLAYER_TWO);
			playerTwoGameButtons[i] = new GameButton(i, this, currentScene, PLAYER_ONE);
			
		}
		for (int i = 0; i < NUM_BUTTONS * 3; i++)
			displayGameButtons[i] = new GameButton(i % NUM_BUTTONS, this, currentScene, DISPLAY_BUTTONS);
		background = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		background.setZIndex(BACKGROUND_Z);
		gameAssetsCreated = true;
	}
	
	private void createDifficultyEntities()
	{
		for (int x = 0; x < 4; x++)
			difficultyEntity[x] = new DifficultyEntity(x, this);
	}
	
	private void createTilesetEntity()
	{
		tilesetEntity = new TilesetEntity(this);
	}
	
	public void setParent(GameScene parent)
	{
		this.currentScene = parent;
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i].setParent(parent);
			playerTwoGameButtons[i].setParent(parent);
			displayGameButtons[i].setParent(parent);
		}
	}
	
	public void setupScene()
	{
		checkForNoParent();
		switch (SetupScene.getDifficulty())
		{
			case Difficulty.EASY:
				numberOfButtonsToUse = 3;
				numberOfStreamTilesToSpawn = 1;
				break;
			case Difficulty.NORMAL:
				numberOfButtonsToUse = 5;
				numberOfStreamTilesToSpawn = 2;
				break;
			case Difficulty.HARD:
			case Difficulty.INSANE:
				numberOfButtonsToUse = 9;
				numberOfStreamTilesToSpawn = 3;
				break;
		}
		createButtons(PLAYER_ONE);
		createButtons(PLAYER_TWO);
		createButtons(DISPLAY_BUTTONS);
		
		currentScene.attachChild(background);
		
		currentScene.sortChildren();
	}
	
	private void checkForNoParent()
	{
		for (int x = 0; x < NUM_BUTTONS; x++)
		{
			playerOneGameButtons[x].buttonSprite.detachSelf();
			playerTwoGameButtons[x].buttonSprite.detachSelf();
			background.detachSelf();
		}
		for (int x = 0; x < NUM_BUTTONS * 3; x++)
			displayGameButtons[x].buttonSprite.detachSelf();
		
	}
	
	public void clearTileset()
	{
		activity.runOnUpdateThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				if (gameAssetsCreated)
				{
					for (int i = 0; i < playerTwoGameButtons.length; i++)
					{
						playerTwoGameButtons[i].clear();
						playerOneGameButtons[i].clear();
						displayGameButtons[i].clear();
					}
					background.detachSelf();
				}
				tilesetEntity.clear();
				for (DifficultyEntity d : difficultyEntity)
					d.clear();
				System.gc();
				
			}
		});
		
	}
	
	private void createButtons(int player)
	{
		switch (player)
		{
			case PLAYER_ONE:
				
				//Easy Buttons
				for (int x = 0; x < 3; x++)
				{
					playerTwoGameButtons[x].buttonSprite.setPosition(90 + ((2 - x) % 3) * BUTTON_WIDTH, BUTTON_WIDTH);
				}
				
				//Medium Buttons
				{
					playerTwoGameButtons[3].buttonSprite.setPosition(90 + BUTTON_WIDTH, BUTTON_WIDTH * 2);
					playerTwoGameButtons[4].buttonSprite.setPosition(90 + BUTTON_WIDTH, 0);
				}
				
				//Hard Buttons
				{
					
					playerTwoGameButtons[5].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, BUTTON_WIDTH * 2);
					playerTwoGameButtons[6].buttonSprite.setPosition(90, BUTTON_WIDTH * 2);
					playerTwoGameButtons[7].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, 0);
					playerTwoGameButtons[8].buttonSprite.setPosition(90, 0);
					
				}
				
				for (int x = 0; x < numberOfButtonsToUse; x++)
				{
					playerTwoGameButtons[x].buttonSprite.setRotation(180);
					playerTwoGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
					playerTwoGameButtons[x].buttonSprite.setAlpha(0);
					currentScene.attachChild(playerTwoGameButtons[x].buttonSprite);
					currentScene.registerTouchArea(playerTwoGameButtons[x].buttonSprite);
				}
				
				break;
			case PLAYER_TWO:
				
				//Easy Buttons
				for (int x = 0; x < 3; x++)
				{
					playerOneGameButtons[x].buttonSprite.setPosition(90 + (x % 3) * BUTTON_WIDTH, 470 + BUTTON_WIDTH);
				}
				
				//Medium Buttons
				{
					playerOneGameButtons[3].buttonSprite.setPosition(90 + BUTTON_WIDTH, 470 + 0);
					playerOneGameButtons[4].buttonSprite.setPosition(90 + BUTTON_WIDTH, 470 + BUTTON_WIDTH * 2);
				}
				
				//Hard Buttons
				{
					
					playerOneGameButtons[5].buttonSprite.setPosition(90, 470 + 0);
					playerOneGameButtons[6].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, 470 + 0);
					playerOneGameButtons[7].buttonSprite.setPosition(90, 470 + BUTTON_WIDTH * 2);
					playerOneGameButtons[8].buttonSprite.setPosition(90 + BUTTON_WIDTH * 2, 470 + BUTTON_WIDTH * 2);
					
				}
				
				for (int x = 0; x < numberOfButtonsToUse; x++)
				{
					playerOneGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
					playerOneGameButtons[x].buttonSprite.setAlpha(0);
					currentScene.attachChild(playerOneGameButtons[x].buttonSprite);
					currentScene.registerTouchArea(playerOneGameButtons[x].buttonSprite);
				}
				break;
			case DISPLAY_BUTTONS:
				for (int x = 0; x < NUM_BUTTONS * 3; x++)
				{
					resetDisplayButton(displayGameButtons[x]);
					displayGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
					currentScene.attachChild(displayGameButtons[x].buttonSprite);
				}
				break;
		}
	}
	
	public void animateDisplayButton(GameButton displayButton,
			GameButton playerButton, IEntityModifierListener listener)
	{
		if (SetupScene.getGameMode() == GameMode.NON_STOP)
		{
			int i = 0;
			for (; i < numberOfStreamTilesToSpawn; i++)
			{
				if (displayButton == currentStreamButtons[i])
					break;
			}
			
			currentStreamButtons[i] = newStreamButton();
			currentStreamButtons[i].buttonSprite.setPosition(displayButton.buttonSprite);
			currentStreamButtons[i].buttonSprite.setVisible(true);
			currentStreamButtons[i].buttonSprite.registerEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME, 0, 1.0f));
			currentStreamButtons[i].buttonSprite.setZIndex(BUTTON_Z);
		} else if (SetupScene.getGameMode() == GameMode.REFLEX)
		{
			int i = 0;
			for (; i < numberOfStreamTilesToSpawn; i++)
			{
				if (displayButton == currentStreamButtons[i])
					break;
			}
			currentStreamButtons[i] = null;
			checkAllButtonsGone();
		}
		displayButton.buttonSprite.setZIndex(FOREGROUND_Z);
		displayButton.buttonSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.0f, 2.0f, EaseCubicOut.getInstance()), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 2.0f, 1.0f, EaseCubicIn.getInstance())));
		displayButton.buttonSprite.registerEntityModifier(new MoveModifier(WIN_MOVE_MOD_TIME, displayButton.buttonSprite.getX(), playerButton.buttonSprite.getX(), displayButton.buttonSprite.getY(), playerButton.buttonSprite.getY(), listener));
		displayButton.buttonSprite.registerEntityModifier(new RotationModifier(WIN_MOVE_MOD_TIME * 2 / 3, displayButton.buttonSprite.getRotation(), playerButton.buttonSprite.getRotation()));
		
	}
	
	private void checkAllButtonsGone()
	{
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
			if (currentStreamButtons[i] != null)
				return;
		
		//TODO: Rigged delay. Maybe change this later.
		playerOneGameButtons[0].buttonSprite.registerEntityModifier(new DelayModifier(REFLEX_MIN_TIME + rand.nextFloat() * (REFLEX_MAX_TIME - REFLEX_MIN_TIME))
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				startStream();
				super.onModifierFinished(pItem);
			}
		});
	}
	
	public void resetDisplayButton(final GameButton pItem)
	{
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				pItem.buttonSprite.clearEntityModifiers();
			}
		});
		pItem.buttonSprite.setRotation(90);
		pItem.buttonSprite.setPosition(((CAMERA_WIDTH - BUTTON_WIDTH + BAR_WIDTH) / 2), (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
		pItem.buttonSprite.setVisible(false);
		
	}
	
	private final int	SHAKE_ANGLE	= 5;
	private final float	COLOR_VALUE	= .4f;
	
	public void disablePlayer(int player)
	{
		switch (player)
		{
			case PLAYER_ONE:
				for (int i = 0; i < NUM_BUTTONS; i++)
				{
					playerTwoGameButtons[i].buttonSprite.setColor(COLOR_VALUE, COLOR_VALUE, COLOR_VALUE, 1.0f);
					playerTwoGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(DISABLE_TIME / 12, 180, 180-SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, 180-SHAKE_ANGLE, 180+SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, 180+SHAKE_ANGLE, 180)), 4)
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							pItem.setColor(1.0f, 1.0f, 1.0f, 1.0f);
							super.onModifierFinished(pItem);
						}
					});
				}
				
				break;
			case PLAYER_TWO:
				for (int i = 0; i < NUM_BUTTONS; i++)
				{
					
					playerOneGameButtons[i].buttonSprite.setColor(COLOR_VALUE, COLOR_VALUE, COLOR_VALUE, 1.0f);
					playerOneGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(DISABLE_TIME / 12, 0, -SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, SHAKE_ANGLE, 0)), 4)
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							pItem.setColor(1.0f, 1.0f, 1.0f, 1.0f);
							super.onModifierFinished(pItem);
						}
					});
				}
				break;
		}
		
	}
	
	private GameButton newStreamButton()
	{
		GameButton newStreamButton = null;
		while (newStreamButton == null)
		{
			final int nextButtonValue = rand.nextInt(this.numberOfButtonsToUse);
			
			for (int x = 0; x < 3; x++)
			{
				if (!displayGameButtons[nextButtonValue + NUM_BUTTONS * x].buttonSprite.isVisible())
				{
					newStreamButton = displayGameButtons[nextButtonValue + NUM_BUTTONS * x];
					break;
				}
			}
		}
		
		return newStreamButton;
	}
	
	public void startStream()
	{
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
		{
			currentStreamButtons[i] = newStreamButton();
			currentStreamButtons[i].buttonSprite.setScale(0.1f);
			currentStreamButtons[i].buttonSprite.setVisible(true);
			currentStreamButtons[i].buttonSprite.registerEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME, 0, 1.0f));
		}
		switch (SetupScene.getDifficulty())
		{
			case Difficulty.EASY:
				currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + BAR_WIDTH - BUTTON_WIDTH) / 2, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
				break;
			case Difficulty.NORMAL:
				currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + BAR_WIDTH - BUTTON_WIDTH) / 2 - BUTTON_WIDTH / 2, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
				currentStreamButtons[1].buttonSprite.setPosition((CAMERA_WIDTH + BAR_WIDTH - BUTTON_WIDTH) / 2 + BUTTON_WIDTH / 2, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
				break;
			case Difficulty.HARD:
			case Difficulty.INSANE:
				currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + BAR_WIDTH - BUTTON_WIDTH) / 2 - BUTTON_WIDTH, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
				currentStreamButtons[1].buttonSprite.setPosition((CAMERA_WIDTH + BAR_WIDTH - BUTTON_WIDTH) / 2, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
				currentStreamButtons[2].buttonSprite.setPosition((CAMERA_WIDTH + BAR_WIDTH - BUTTON_WIDTH) / 2 + BUTTON_WIDTH, (CAMERA_HEIGHT - BUTTON_WIDTH) / 2);
				break;
		}
	}
	
	public GameButton isButtonDisplayed(int buttonNumber)
	{
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
		{
			if (currentStreamButtons[i] != null)
				if (currentStreamButtons[i].getButtonNumber() == buttonNumber)
					return currentStreamButtons[i];
		}
		return null;
	}
	
	public void reset()
	{
		for (int i = 0; i < NUM_BUTTONS * 3; i++)
			resetDisplayButton(displayGameButtons[i]);
		
	}
	
	public DifficultyEntity getDifficultySprite(int difficulty)
	{
		return difficultyEntity[difficulty];
	}
	
	public TilesetEntity getTilesetEntity()
	{
		return tilesetEntity;
	}
	
	public String getBasePath()
	{
		return basePath;
	}
	
	public ITextureRegion getButtonRegion(int buttonNumber)
	{
		return buttonRegions[buttonNumber];
	}
	
	final float	BUTTON_DELAY	= .6f;
	
	public void animatePlayerTilesIn(final Runnable onFinishedAction)
	{
		for (int i = 0; i < numberOfButtonsToUse; i++)
		{
			if (i == numberOfButtonsToUse - 1) //If last button the use the onFinishedAction
				playerOneGameButtons[i].buttonSprite.registerEntityModifier(new DelayModifier(BUTTON_ANIMATE_IN_TIME * (i + 1) * BUTTON_DELAY)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.registerEntityModifier(new ScaleModifier(BUTTON_ANIMATE_IN_TIME, BUTTON_ANIMATE_IN_START_SCALE, 1.0f, EaseCubicIn.getInstance()));
						pItem.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME / 2, 0, 1.0f));
						onFinishedAction.run();
						
					};
				});
			else
				playerOneGameButtons[i].buttonSprite.registerEntityModifier(new DelayModifier(BUTTON_ANIMATE_IN_TIME * (i + 1) * BUTTON_DELAY)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.registerEntityModifier(new ScaleModifier(BUTTON_ANIMATE_IN_TIME, BUTTON_ANIMATE_IN_START_SCALE, 1.0f, EaseCubicIn.getInstance()));
						pItem.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME / 2, 0, 1.0f));
						
					};
				});
			playerTwoGameButtons[i].buttonSprite.registerEntityModifier(new DelayModifier(BUTTON_ANIMATE_IN_TIME * (i + 1) * BUTTON_DELAY)
			{
				protected void onModifierFinished(IEntity pItem)
				{
					pItem.registerEntityModifier(new ScaleModifier(BUTTON_ANIMATE_IN_TIME, BUTTON_ANIMATE_IN_START_SCALE, 1.0f));
					pItem.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME / 2, 0, 1.0f));
				};
			});
		}
	}
}
