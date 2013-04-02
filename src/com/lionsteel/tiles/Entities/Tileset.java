package com.lionsteel.tiles.Entities;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.BaseParticleEmitter;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import android.opengl.GLES20;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;
import com.lionsteel.tiles.Scenes.MenuScenes.SetupScene;
import com.lionsteel.tiles.util.Inventory;

public class Tileset implements TilesConstants
{
	private TilesMainActivity				activity;

	public static String[]					tilesetList;

	//TODO: Purchaseable tilesets here
	public static final String[]			purchaseableTilesets		= {};									//{ "blocks", "dice" };
	public static final ArrayList<String>	purchasedTilesets			= new ArrayList<String>();

	private BuildableBitmapTextureAtlas		atlas;
	private BitmapTextureAtlas				backgroundAtlas;
	private BuildableBitmapTextureAtlas		animationAtlas;

	private final TextureRegion[]			buttonRegions				= new TextureRegion[NUM_BUTTONS];
	private final TiledTextureRegion[]		tiledButtonRegions			= new TiledTextureRegion[NUM_BUTTONS];

	private GameButton[]					playerOneGameButtons		= new GameButton[NUM_BUTTONS];
	private GameButton[]					playerTwoGameButtons		= new GameButton[NUM_BUTTONS];
	private GameButton[]					displayGameButtons			= new GameButton[NUM_BUTTONS * 3];

	private Sprite							background;

	private boolean							gameAssetsCreated			= false;

	private Sprite[]						displayIndicators			= new Sprite[3];
	private GameButton[]					currentStreamButtons		= new GameButton[3];
	private GameScene						currentScene;

	private int								numberOfButtonsToUse		= 3;
	private int								numberOfStreamTilesToSpawn	= 1;
	private final Random					rand;
	private final String					basePath;

	private DifficultyEntity				difficultyEntity[]			= new DifficultyEntity[4];
	private TilesetEntity					tilesetEntity;
	private TilesetParticleSystem			particleSystem;

	private Rectangle						tileBase;
	private Rectangle						playerOneTiles;
	private Rectangle						playerOneDisplay;
	private Rectangle						playerTwoTiles;
	private Rectangle						playerTwoDisplay;

	private SpriteParticleSystem			displayButtonParticleSystem;

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
		activity = TilesMainActivity.getInstance();
		rand = new Random();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");

		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444);
		animationAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444);
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			buttonRegions[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, (i + 1) + ".png");
			int buttonColumn = (int) (buttonRegions[i].getWidth() / TILE_WIDTH);
			if (buttonColumn > 1)
				tiledButtonRegions[i] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(animationAtlas, activity, (i + 1) + ".png", buttonColumn, 1);
		}

		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
			animationAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			animationAtlas.load();
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
		if (gameAssetsCreated)
			return;

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tilesets/" + basePath + "/");
		final TextureRegion backgroundRegion;
		backgroundAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 1024, BitmapTextureFormat.RGB_565);
		backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundAtlas, activity, "background.png", 0, 0);
		backgroundAtlas.load();

		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i] = new GameButton(i, this, currentScene, PLAYER_ONE);
			playerTwoGameButtons[i] = new GameButton(i, this, currentScene, PLAYER_TWO);
		}
		for (int i = 0; i < NUM_BUTTONS * 3; i++)
			displayGameButtons[i] = new GameButton(i % NUM_BUTTONS, this, currentScene, DISPLAY_BUTTONS);
		for (int i = 0; i < 3; i++)
			displayIndicators[i] = new Sprite(0, 0, SharedResources.getInstance().displayIndicatorRegion, activity.getVertexBufferObjectManager());
		background = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		background.setZIndex(BACKGROUND_Z);
		particleSystem = new TilesetParticleSystem(basePath);

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
		reset();
		createButtons(PLAYER_TWO);
		createButtons(PLAYER_ONE);
		createButtons(DISPLAY_BUTTONS);

		tileBase = new Rectangle(playerOneGameButtons[0].getX() - TILE_BASE_PADDING, 0, TILE_WIDTH * 3 + TILE_BASE_PADDING * 2, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		tileBase.setColor(0, 0, 0, 0);

		currentScene.attachChild(background);
		currentScene.attachChild(particleSystem);
		currentScene.attachChild(tileBase);
		setupIndicators();

		setupParticleSystems();

		if (SetupScene.getGameMode() == GameMode.RACE)
		{
			numberOfStreamTilesToSpawn = 2;
			{//Player One Tiles
				playerOneTiles = new Rectangle(TILE_BASE_PADDING - PLAYER_RACE_TILE_PADDING, playerOneGameButtons[3].getY() - PLAYER_RACE_TILE_PADDING, TILE_WIDTH * 3 + PLAYER_RACE_TILE_PADDING * 2, TILE_WIDTH * 3 + PLAYER_RACE_TILE_PADDING * 2, activity.getVertexBufferObjectManager());
				playerOneDisplay = new Rectangle(displayIndicators[1].getX() - tileBase.getX() - PLAYER_RACE_TILE_PADDING, displayIndicators[1].getY() - PLAYER_RACE_TILE_PADDING, TILE_WIDTH + PLAYER_RACE_TILE_PADDING * 2, 1, activity.getVertexBufferObjectManager());
				playerOneDisplay.setHeight(playerOneTiles.getY() - playerOneDisplay.getY());
				playerOneTiles.setColor(1.0f, 0, 0, PLAYER_TILES_ALPHA);
				playerOneDisplay.setColor(1.0f, 0, 0, PLAYER_TILES_ALPHA);
				playerOneTiles.setAlpha(0);
				playerOneDisplay.setAlpha(0);
				tileBase.attachChild(playerOneTiles);
				tileBase.attachChild(playerOneDisplay);
			}
			{//Player Two Tiles
				playerTwoTiles = new Rectangle(TILE_BASE_PADDING - PLAYER_RACE_TILE_PADDING, playerTwoGameButtons[4].getY() - PLAYER_RACE_TILE_PADDING, TILE_WIDTH * 3 + PLAYER_RACE_TILE_PADDING * 2, TILE_WIDTH * 3 + PLAYER_RACE_TILE_PADDING * 2, activity.getVertexBufferObjectManager());
				playerTwoDisplay = new Rectangle(displayIndicators[0].getX() - tileBase.getX() - PLAYER_RACE_TILE_PADDING, displayIndicators[0].getY() - PLAYER_RACE_TILE_PADDING, TILE_WIDTH + PLAYER_RACE_TILE_PADDING * 2, TILE_WIDTH + PLAYER_RACE_TILE_PADDING * 2, activity.getVertexBufferObjectManager());
				playerTwoDisplay.setHeight((playerTwoDisplay.getY() + playerTwoDisplay.getHeight()) - (playerTwoTiles.getY() + playerTwoTiles.getHeight()));
				playerTwoDisplay.setY(playerTwoTiles.getY() + playerTwoTiles.getHeight());
				playerTwoTiles.setColor(0, 0, 1.0f, PLAYER_TILES_ALPHA);
				playerTwoDisplay.setColor(0, 0, 1.0f, PLAYER_TILES_ALPHA);
				playerTwoTiles.setAlpha(0);
				playerTwoDisplay.setAlpha(0);
				tileBase.attachChild(playerTwoTiles);
				tileBase.attachChild(playerTwoDisplay);
			}

		}
		currentScene.sortChildren();
	}

	private void setupParticleSystems()
	{
		final float minYStartVel = -10;
		final float maxYStartVel = 10;
		final float maxXAccel = 20;
		final float minYAccel = -20;
		final float maxYAccel = 20;
		final float expireTime = 1.0f;
		final float minScale = 1.0f;
		final float maxScale = .1f;
		final float startAlpha = 1.0f;

		final int minSpawn = 20;
		final int maxSpawn = 30;
		final int maxParticles = 100;

		displayButtonParticleSystem = new SpriteParticleSystem(new RectangleParticleEmitter(0, 0, TILE_WIDTH/2, TILE_WIDTH/2), minSpawn, maxSpawn, maxParticles, SharedResources.getInstance().particlePointRegion, activity.getVertexBufferObjectManager());

		displayButtonParticleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA));
		displayButtonParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(minYStartVel, maxYStartVel, minYStartVel, maxYStartVel));
		displayButtonParticleSystem.addParticleInitializer(new AccelerationParticleInitializer<Sprite>(-maxXAccel, maxXAccel, minYAccel, maxYAccel));
		displayButtonParticleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(0, 1.0f, 0.0f));
		displayButtonParticleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(startAlpha));
		displayButtonParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(expireTime));

		displayButtonParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, expireTime * .7f, minScale, maxScale));
		displayButtonParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(expireTime * .7f, expireTime, maxScale, 0));

		displayButtonParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(expireTime * .9f, expireTime, startAlpha, 0.0f));
		
		displayButtonParticleSystem.setParticlesSpawnEnabled(false);
		displayButtonParticleSystem.setZIndex(FOREGROUND_Z-1);

		currentScene.attachChild(displayButtonParticleSystem);
	}

	private void setupIndicators()
	{
		switch (SetupScene.getGameMode())
		{
		case GameMode.NON_STOP:
		case GameMode.REFLEX:
		case GameMode.FREE_PLAY:
		case GameMode.FRENZY:
		case GameMode.TIME_ATTACK:
			switch (SetupScene.getDifficulty())
			{
			case Difficulty.EASY:
				displayIndicators[0].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[0].getWidth()) / 2, (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
				break;
			case Difficulty.NORMAL:
				displayIndicators[0].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[0].getWidth()) / 2 - displayIndicators[0].getWidth() / 2, (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
				displayIndicators[1].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[1].getWidth()) / 2 + displayIndicators[1].getWidth() / 2, (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
				break;
			case Difficulty.HARD:
			case Difficulty.INSANE:
				displayIndicators[0].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[0].getWidth()) / 2 - displayIndicators[0].getWidth(), (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
				displayIndicators[1].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[1].getWidth()) / 2, (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
				displayIndicators[2].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[2].getWidth()) / 2 + displayIndicators[2].getWidth(), (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
				break;
			}

			for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
			{
				displayIndicators[i].clearEntityModifiers();
				currentScene.attachChild(displayIndicators[i]);
				displayIndicators[i].setAlpha(0);
			}
			break;
		case GameMode.RACE:
			displayIndicators[0].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[0].getWidth()) / 2 - displayIndicators[0].getWidth(), (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
			displayIndicators[1].setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - displayIndicators[1].getWidth()) / 2 + displayIndicators[1].getWidth(), (CAMERA_HEIGHT - displayIndicators[0].getHeight()) / 2);
			for (int i = 0; i < 2; i++)
			{
				displayIndicators[i].clearEntityModifiers();
				currentScene.attachChild(displayIndicators[i]);
				displayIndicators[i].setAlpha(0);
			}
			break;
		}
	}

	public void animateTileBaseIn()
	{
		tileBase.setColor(0, 0, 0, 0);
		tileBase.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, TILE_BASE_ALPHA));
		if (SetupScene.getGameMode() == GameMode.RACE)
		{
			playerOneTiles.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, PLAYER_TILES_ALPHA));
			playerOneDisplay.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, PLAYER_TILES_ALPHA));
			playerTwoTiles.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, PLAYER_TILES_ALPHA));
			playerTwoDisplay.registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, PLAYER_TILES_ALPHA));
		}
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
			displayIndicators[i].registerEntityModifier(new AlphaModifier(TILE_BASE_ANIMATE_IN, 0, 1));
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
		for (int x = 0; x < 3; x++)
			displayIndicators[x].detachSelf();
		particleSystem.detachSelf();

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
					background.dispose();
					particleSystem.clear();
				}

				if (tilesetEntity != null)
					tilesetEntity.clear();
				for (DifficultyEntity d : difficultyEntity)
				{
					if (difficultyEntity != null)
						d.clear();
				}

				atlas.unload();
				if (backgroundAtlas != null)
					backgroundAtlas.unload();
				animationAtlas.unload();

				System.gc();

			}
		});

	}

	private void createButtons(int player)
	{
		switch (player)
		{
		case PLAYER_TWO:

			//Easy Buttons
			for (int x = 0; x < 3; x++)
			{
				playerTwoGameButtons[x].buttonSprite.setPosition(PLAYER_TILE_START_X + ((2 - x) % 3) * TILE_WIDTH, TILE_WIDTH);
			}

			//Medium Buttons
			{
				playerTwoGameButtons[3].buttonSprite.setPosition(PLAYER_TILE_START_X + TILE_WIDTH, TILE_WIDTH * 2);
				playerTwoGameButtons[4].buttonSprite.setPosition(PLAYER_TILE_START_X + TILE_WIDTH, 0);
			}

			//Hard Buttons
			{

				playerTwoGameButtons[5].buttonSprite.setPosition(PLAYER_TILE_START_X + TILE_WIDTH * 2, TILE_WIDTH * 2);
				playerTwoGameButtons[6].buttonSprite.setPosition(PLAYER_TILE_START_X, TILE_WIDTH * 2);
				playerTwoGameButtons[7].buttonSprite.setPosition(PLAYER_TILE_START_X + TILE_WIDTH * 2, 0);
				playerTwoGameButtons[8].buttonSprite.setPosition(PLAYER_TILE_START_X, 0);

			}

			for (int x = 0; x < numberOfButtonsToUse; x++)
			{
				playerTwoGameButtons[x].buttonSprite.setRotation(180);
				playerTwoGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				playerTwoGameButtons[x].buttonSprite.setAlpha(0);
				currentScene.attachChild(playerTwoGameButtons[x].buttonSprite);
			}

			break;
		case PLAYER_ONE:

			//Easy Buttons
			for (int x = 0; x < 3; x++)
			{
				playerOneGameButtons[x].buttonSprite.setPosition(90 + (x % 3) * TILE_WIDTH, 470 + TILE_WIDTH);
			}

			//Medium Buttons
			{
				playerOneGameButtons[3].buttonSprite.setPosition(90 + TILE_WIDTH, 470 + 0);
				playerOneGameButtons[4].buttonSprite.setPosition(90 + TILE_WIDTH, 470 + TILE_WIDTH * 2);
			}

			//Hard Buttons
			{

				playerOneGameButtons[5].buttonSprite.setPosition(90, 470 + 0);
				playerOneGameButtons[6].buttonSprite.setPosition(90 + TILE_WIDTH * 2, 470 + 0);
				playerOneGameButtons[7].buttonSprite.setPosition(90, 470 + TILE_WIDTH * 2);
				playerOneGameButtons[8].buttonSprite.setPosition(90 + TILE_WIDTH * 2, 470 + TILE_WIDTH * 2);

			}

			for (int x = 0; x < numberOfButtonsToUse; x++)
			{
				playerOneGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				playerOneGameButtons[x].buttonSprite.setAlpha(0);
				currentScene.attachChild(playerOneGameButtons[x].buttonSprite);
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

	public void animateReflexDisplayButton(GameButton displayButton, GameButton playerButton, IEntityModifierListener listener)
	{
		int i = 0;
		for (; i < numberOfStreamTilesToSpawn; i++)
			if (displayButton == currentStreamButtons[i])
				break;

		currentStreamButtons[i] = null;
		checkAllButtonsGone();
		animateDisplayButton(displayButton, playerButton, listener);
	}

	public void animateNonStopDisplayButton(GameButton displayButton, GameButton playerButton, IEntityModifierListener listener)
	{
		int i = 0;
		for (; i < numberOfStreamTilesToSpawn; i++)
			if (displayButton == currentStreamButtons[i])
				break;

		currentStreamButtons[i] = newStreamButton();
		currentStreamButtons[i].buttonSprite.setPosition(displayButton.buttonSprite);
		currentStreamButtons[i].buttonSprite.setVisible(true);
		currentStreamButtons[i].buttonSprite.registerEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME, 0, 1.0f));
		currentStreamButtons[i].buttonSprite.setZIndex(BUTTON_Z);
		animateDisplayButton(displayButton, playerButton, listener);
	}

	public void animateTimeAttackDisplayButton(GameButton displayButton, GameButton playerButton, IEntityModifierListener listener, final int tilesLeft)
	{
		int i = 0;
		for (; i < numberOfStreamTilesToSpawn; i++)
			if (displayButton == currentStreamButtons[i])
				break;

		if (tilesLeft - this.numberOfStreamTilesToSpawn >= 0)
		{
			currentStreamButtons[i] = newStreamButton();
			currentStreamButtons[i].buttonSprite.setPosition(displayButton.buttonSprite);
			currentStreamButtons[i].buttonSprite.setVisible(true);
			currentStreamButtons[i].buttonSprite.registerEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME, 0, 1.0f));
			currentStreamButtons[i].buttonSprite.setZIndex(BUTTON_Z);
		} else
		{
			currentStreamButtons[i] = null;
		}
		animateDisplayButton(displayButton, playerButton, listener);
	}

	private void animateDisplayButton(GameButton displayButton, final GameButton playerButton, IEntityModifierListener listener)
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.WON_TILE);
		currentScene.playTileCollectSound();
		displayButton.buttonSprite.setZIndex(FOREGROUND_Z);
		displayButton.buttonSprite.clearEntityModifiers();
		displayButton.buttonSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.0f, 2.0f, EaseCubicOut.getInstance()), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 2.0f, 1.0f, EaseCubicIn.getInstance())));
		displayButton.buttonSprite.registerEntityModifier(new MoveModifier(WIN_MOVE_MOD_TIME, displayButton.buttonSprite.getX(), playerButton.buttonSprite.getX(), displayButton.buttonSprite.getY(), playerButton.buttonSprite.getY(), listener)
		{
			@Override
			protected void onManagedUpdate(float pSecondsElapsed, IEntity pItem)
			{
				((BaseParticleEmitter) displayButtonParticleSystem.getParticleEmitter()).setCenter(pItem.getX(), pItem.getY());
				displayButtonParticleSystem.setParticlesSpawnEnabled(true);
				super.onManagedUpdate(pSecondsElapsed, pItem);
			}

			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				playTileCrash();
				displayButtonParticleSystem.setParticlesSpawnEnabled(false);
				super.onModifierFinished(pItem);
			}
		});
		displayButton.buttonSprite.registerEntityModifier(new RotationModifier(WIN_MOVE_MOD_TIME * 2 / 3, displayButton.buttonSprite.getRotation(), playerButton.buttonSprite.getRotation()));
		displayButton.buttonSprite.setZIndex(BUTTON_Z + 1);
		currentScene.sortChildren();

	}

	private void checkAllButtonsGone()
	{
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
			if (currentStreamButtons[i] != null)
				return;

		activity.getEngine().registerUpdateHandler(new TimerHandler(REFLEX_MIN_TIME + rand.nextFloat() * (REFLEX_MAX_TIME - REFLEX_MIN_TIME), new ITimerCallback()
		{

			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				activity.getEngine().unregisterUpdateHandler(pTimerHandler);
				startNonStop();
			}
		}));
	}

	public void resetDisplayButton(final GameButton pItem)
	{
		pItem.buttonSprite.setScale(0.01f);
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				pItem.buttonSprite.clearEntityModifiers();
				pItem.buttonSprite.setRotation(90);
				pItem.buttonSprite.setPosition(((CAMERA_WIDTH - TILE_WIDTH + PAUSE_BAR_WIDTH) / 2), (CAMERA_HEIGHT - TILE_WIDTH) / 2);
				pItem.buttonSprite.setVisible(false);

			}
		});
	}

	private final int	SHAKE_ANGLE	= 5;
	private final float	COLOR_VALUE	= .4f;

	public void disablePlayer(int player)
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.WRONG_TILE);
		switch (player)
		{
		case PLAYER_TWO:
			for (int i = 0; i < NUM_BUTTONS; i++)
			{
				playerTwoGameButtons[i].buttonSprite.setColor(COLOR_VALUE, COLOR_VALUE, COLOR_VALUE, 1.0f);
				playerTwoGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(DISABLE_TIME / 12, 180, 180 - SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, 180 - SHAKE_ANGLE, 180 + SHAKE_ANGLE), new RotationModifier(DISABLE_TIME / 12, 180 + SHAKE_ANGLE, 180)), 4)
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
		case PLAYER_ONE:
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

	private void animateMove(final GameButton buttonToMove, GameButton buttonToMoveTo)
	{
		buttonToMove.buttonSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(INSANE_RANDOMIZE_DURATION / 2, 1.0f, 2.0f, EaseCubicOut.getInstance()), new ScaleModifier(INSANE_RANDOMIZE_DURATION / 2, 2.0f, 1.0f, EaseCubicIn.getInstance())));
		currentScene.unregisterTouchArea(buttonToMove.buttonSprite);
		buttonToMove.buttonSprite.registerEntityModifier(new MoveModifier(INSANE_RANDOMIZE_DURATION, buttonToMove.buttonSprite.getX(), buttonToMoveTo.buttonSprite.getX(), buttonToMove.buttonSprite.getY(), buttonToMoveTo.buttonSprite.getY())
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				currentScene.registerTouchArea(buttonToMove.buttonSprite);
				playTileCrash();
				super.onModifierFinished(pItem);
			}
		});

	}

	public void resetPlayerTiles()
	{
		if(displayButtonParticleSystem != null)
			displayButtonParticleSystem.setParticlesSpawnEnabled(false);
		{ //Player Two Buttons
			for (int x = 0; x < 3; x++)
				playerTwoGameButtons[x].buttonSprite.setPosition(90 + ((2 - x) % 3) * TILE_WIDTH, TILE_WIDTH);

			//Medium Buttons
			{
				playerTwoGameButtons[3].buttonSprite.setPosition(90 + TILE_WIDTH, TILE_WIDTH * 2);
				playerTwoGameButtons[4].buttonSprite.setPosition(90 + TILE_WIDTH, 0);
			}

			//Hard Buttons
			{

				playerTwoGameButtons[5].buttonSprite.setPosition(90 + TILE_WIDTH * 2, TILE_WIDTH * 2);
				playerTwoGameButtons[6].buttonSprite.setPosition(90, TILE_WIDTH * 2);
				playerTwoGameButtons[7].buttonSprite.setPosition(90 + TILE_WIDTH * 2, 0);
				playerTwoGameButtons[8].buttonSprite.setPosition(90, 0);

			}

			for (int x = 0; x < numberOfButtonsToUse; x++)
			{
				playerTwoGameButtons[x].buttonSprite.clearEntityModifiers();
				playerTwoGameButtons[x].buttonSprite.setRotation(180);
				playerTwoGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				playerTwoGameButtons[x].buttonSprite.setColor(1, 1, 1, 1);
				playerTwoGameButtons[x].buttonSprite.setScale(1.0f);
				while (currentScene.unregisterTouchArea(playerTwoGameButtons[x].buttonSprite))
					;
				currentScene.registerTouchArea(playerTwoGameButtons[x].buttonSprite);
			}
		}

		{ // Player One
			//Easy Buttons
			for (int x = 0; x < 3; x++)
			{
				playerOneGameButtons[x].buttonSprite.setPosition(90 + (x % 3) * TILE_WIDTH, 470 + TILE_WIDTH);
			}

			//Medium Buttons
			{
				playerOneGameButtons[3].buttonSprite.setPosition(90 + TILE_WIDTH, 470 + 0);
				playerOneGameButtons[4].buttonSprite.setPosition(90 + TILE_WIDTH, 470 + TILE_WIDTH * 2);
			}

			//Hard Buttons
			{

				playerOneGameButtons[5].buttonSprite.setPosition(90, 470 + 0);
				playerOneGameButtons[6].buttonSprite.setPosition(90 + TILE_WIDTH * 2, 470 + 0);
				playerOneGameButtons[7].buttonSprite.setPosition(90, 470 + TILE_WIDTH * 2);
				playerOneGameButtons[8].buttonSprite.setPosition(90 + TILE_WIDTH * 2, 470 + TILE_WIDTH * 2);

			}

			for (int x = 0; x < numberOfButtonsToUse; x++)
			{
				playerOneGameButtons[x].buttonSprite.clearEntityModifiers();
				playerOneGameButtons[x].buttonSprite.setRotation(0);
				playerOneGameButtons[x].buttonSprite.setColor(1, 1, 1, 1);
				playerOneGameButtons[x].buttonSprite.setZIndex(BUTTON_Z);
				playerOneGameButtons[x].buttonSprite.setScale(1.0f);
				while (currentScene.unregisterTouchArea(playerOneGameButtons[x].buttonSprite))
					;
				currentScene.registerTouchArea(playerOneGameButtons[x].buttonSprite);
			}
		}
	}

	private void insaneRandomize()
	{
		final float SHAKE_ANGLE = 10.0f;
		final float SHAKE_DURATION = 2.0f;
		final int[] buttons = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final int NUM_SWITCHES = 12;
		for (int i = 0; i < NUM_SWITCHES; i++)
		{
			int firstTile = rand.nextInt(NUM_BUTTONS);
			int secondTile = rand.nextInt(NUM_BUTTONS);
			while (secondTile == firstTile)
				secondTile = rand.nextInt(NUM_BUTTONS);
			int temp = buttons[firstTile];
			buttons[firstTile] = buttons[secondTile];
			buttons[secondTile] = temp;
		}
		SharedResources.getInstance().insaneSound.play();
		for (int i = 0; i < NUM_BUTTONS - 1; i++)
			playerOneGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(SHAKE_DURATION / 12, 0, -SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, SHAKE_ANGLE, 0)), 4));
		playerOneGameButtons[NUM_BUTTONS - 1].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(SHAKE_DURATION / 12, 0, -SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, SHAKE_ANGLE, 0)), 4)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				SharedResources.getInstance().insaneSound.stop();
				SharedResources.getInstance().insaneJump.play();
				for (int x = 0; x < NUM_BUTTONS; x++)
				{
					if (x != buttons[x])
					{
						animateMove(playerOneGameButtons[x], playerOneGameButtons[buttons[x]]);
						playerOneGameButtons[x].buttonSprite.setZIndex(FOREGROUND_Z + 1);
					} else
					{
						playerOneGameButtons[x].buttonSprite.setZIndex(FOREGROUND_Z);
					}
					if (x == NUM_BUTTONS - 1)
						playerOneGameButtons[x].buttonSprite.registerEntityModifier(new DelayModifier(INSANE_RANDOMIZE_DURATION)
						{
							protected void onModifierFinished(IEntity pItem)
							{
								startInsaneDelay();
								super.onModifierFinished(pItem);

							};
						});
				}
				currentScene.sortChildren();
				super.onModifierFinished(pItem);
			}
		});
		for (int i = 0; i < NUM_BUTTONS - 1; i++)
			playerTwoGameButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(SHAKE_DURATION / 12, 180, 180 - SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, 180 - SHAKE_ANGLE, 180 + SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, 180 + SHAKE_ANGLE, 180)), 4));
		playerTwoGameButtons[NUM_BUTTONS - 1].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(SHAKE_DURATION / 12, 180, 180 - SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, 180 - SHAKE_ANGLE, 180 + SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, 180 + SHAKE_ANGLE, 180)), 4)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				for (int x = 0; x < NUM_BUTTONS; x++)
				{
					if (x != buttons[x])
					{
						animateMove(playerTwoGameButtons[x], playerTwoGameButtons[buttons[x]]);
						playerTwoGameButtons[x].buttonSprite.setZIndex(FOREGROUND_Z + 1);
					} else
					{
						playerTwoGameButtons[x].buttonSprite.setZIndex(FOREGROUND_Z);
					}
					if (x == NUM_BUTTONS - 1)
						playerTwoGameButtons[x].buttonSprite.registerEntityModifier(new DelayModifier(INSANE_PREVIEW_MOVE_DURATION)
						{
							protected void onModifierFinished(IEntity pItem)
							{
								super.onModifierFinished(pItem);
							};
						});
				}
				currentScene.sortChildren();
				super.onModifierFinished(pItem);
			}
		});
	}

	public void startInsaneDelay()
	{
		resetPlayerButtonsZIndex();
		playerOneGameButtons[0].buttonSprite.registerEntityModifier(new DelayModifier(INSANE_RANDOMIZE_DELAY)
		{
			protected void onModifierFinished(IEntity pItem)
			{
				insaneRandomize();
			};
		});
	}

	private void resetPlayerButtonsZIndex()
	{
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			playerOneGameButtons[i].buttonSprite.setZIndex(BUTTON_Z);
			playerTwoGameButtons[i].buttonSprite.setZIndex(BUTTON_Z);
		}
		currentScene.sortChildren();
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

	public void startNonStop()
	{
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
		{
			currentStreamButtons[i] = newStreamButton();
			currentStreamButtons[i].buttonSprite.setScale(0.1f);
			currentStreamButtons[i].buttonSprite.setZIndex(BUTTON_Z);
			currentStreamButtons[i].buttonSprite.setVisible(true);
			currentStreamButtons[i].buttonSprite.registerEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME, 0, 1.0f));
		}
		switch (SetupScene.getDifficulty())
		{
		case Difficulty.EASY:
			currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
			break;
		case Difficulty.NORMAL:
			currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2 - TILE_WIDTH / 2, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
			currentStreamButtons[1].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2 + TILE_WIDTH / 2, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
			break;
		case Difficulty.HARD:
		case Difficulty.INSANE:
			currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2 - TILE_WIDTH, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
			currentStreamButtons[1].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
			currentStreamButtons[2].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2 + TILE_WIDTH, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
			break;
		}
	}

	public void startRace()
	{
		for (int i = 0; i < 2; i++)
		{
			currentStreamButtons[i] = newStreamButton();
			currentStreamButtons[i].buttonSprite.setScale(.1f);
			currentStreamButtons[i].buttonSprite.setVisible(true);
			currentStreamButtons[i].buttonSprite.registerEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME, 0, 1.0f));
		}
		currentStreamButtons[0].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2 - TILE_WIDTH, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
		currentStreamButtons[1].buttonSprite.setPosition((CAMERA_WIDTH + PAUSE_BAR_WIDTH - TILE_WIDTH) / 2 + TILE_WIDTH, (CAMERA_HEIGHT - TILE_WIDTH) / 2);
	}

	public GameButton isButtonCurrentlyActive(int buttonNumber)
	{
		int buttonIndex = -1;
		float lastButtonScale = 0;
		for (int i = 0; i < numberOfStreamTilesToSpawn; i++)
		{
			if (currentStreamButtons[i] != null)
				if (currentStreamButtons[i].getButtonNumber() == buttonNumber && currentStreamButtons[i].buttonSprite.getScaleX() > MIN_BUTTON_ACTIVE_SCALE && currentStreamButtons[i].buttonSprite.getScaleX() > lastButtonScale)
				{
					buttonIndex = i;
					lastButtonScale = currentStreamButtons[i].buttonSprite.getScaleX();
				}
		}
		if (buttonIndex == -1)
			return null;
		else
			return currentStreamButtons[buttonIndex];
	}

	public GameButton isRaceButtonCurrentlyActive(GameButton button)
	{

		if (currentStreamButtons[button.getPlayer()].getButtonNumber() == button.getButtonNumber())
			return currentStreamButtons[button.getPlayer()];
		return null;
	}

	public void reset()
	{
		resetPlayerTiles();
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

	public ITiledTextureRegion getTiledButtonRegion(int buttonNumber)
	{
		return tiledButtonRegions[buttonNumber];
	}

	public ITextureRegion getButtonRegion(int buttonNumber)
	{
		return buttonRegions[buttonNumber];
	}

	final float	BUTTON_DELAY	= .6f;

	public void animatePlayerTilesIn(final Runnable onFinishedAction)
	{
		animateTileBaseIn();
		for (int i = 0; i < numberOfButtonsToUse; i++)
		{
			if (i == numberOfButtonsToUse - 1) //If last button the use the onFinishedAction
				playerOneGameButtons[i].buttonSprite.registerEntityModifier(new DelayModifier(BUTTON_ANIMATE_IN_TIME * (i + 1) * BUTTON_DELAY)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.registerEntityModifier(new ScaleModifier(BUTTON_ANIMATE_IN_TIME, BUTTON_ANIMATE_IN_START_SCALE, 1.0f, EaseCubicIn.getInstance())
						{
							@Override
							protected void onModifierFinished(IEntity pItem)
							{
								playTileCrash();
								onFinishedAction.run();
								super.onModifierFinished(pItem);
							}

						});
						pItem.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME / 2, 0, 1.0f));

					};
				});
			else
				playerOneGameButtons[i].buttonSprite.registerEntityModifier(new DelayModifier(BUTTON_ANIMATE_IN_TIME * (i + 1) * BUTTON_DELAY)
				{
					protected void onModifierFinished(IEntity pItem)
					{
						pItem.registerEntityModifier(new ScaleModifier(BUTTON_ANIMATE_IN_TIME, BUTTON_ANIMATE_IN_START_SCALE, 1.0f, EaseCubicIn.getInstance())
						{
							@Override
							protected void onModifierFinished(IEntity pItem)
							{
								playTileCrash();
								super.onModifierFinished(pItem);
							}
						});
						pItem.registerEntityModifier(new AlphaModifier(BUTTON_ANIMATE_IN_TIME / 2, 0, 1.0f));

					};
				});

			//If not in 2 player mode
			if (playerTwoGameButtons[i].buttonSprite.hasParent())
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

	private void playTileCrash()
	{
		SharedResources.getInstance().tileCrashSound.play();

	}

	public boolean isButtonVisible(final int buttonNumber)
	{
		for (int i = 0; i < 3; i++)
			if (displayGameButtons[buttonNumber + NUM_BUTTONS * i].buttonSprite.isVisible())
				return true;
		return false;
	}

	public void detachPlayerTwo()
	{
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			currentScene.unregisterTouchArea(playerTwoGameButtons[i].buttonSprite);
			playerTwoGameButtons[i].buttonSprite.detachSelf();
		}
	}

	public static boolean isPurchasable(String string)
	{
		for (int ind = 0; ind < purchaseableTilesets.length; ind++)
			if (purchaseableTilesets[ind].compareTo(string) == 0)
				return true;
		return false;
	}

	public static void getPurchasedTilesets(Inventory inv)
	{
		purchasedTilesets.clear();
		for (int x = 0; x < purchaseableTilesets.length; x++)
			if (inv.getPurchase(purchaseableTilesets[x]) != null)
				purchasedTilesets.add(purchaseableTilesets[x]);
	}

	public static boolean isPurchased(String tileset)
	{
		for (String purchasedTileset : purchasedTilesets)
			if (purchasedTileset.compareTo(tileset) == 0)
				return true;
		return false;
	}

}
