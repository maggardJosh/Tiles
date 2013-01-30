package com.lionsteel.reflexmulti.Scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.Entities.GameButton;
import com.lionsteel.reflexmulti.Entities.Tileset;
import com.lionsteel.reflexmulti.Entities.WrongSelectionIndicator;

public class GameScene extends Scene implements ReflexConstants
{
	private static GameScene			instance;
	
	final BitmapTextureAtlas			sceneAtlas;
	final ReflexActivity				activity;
	private int							gameState				= GameState.INTRO;
	
	private float						secondsOnCurrentState	= 0;
	
	private final Sprite				barSprite;
	
	private final Sprite				playerOneIntro;
	private final Sprite				playerTwoIntro;
	
	private boolean						playerOneReady			= false;
	private boolean						playerTwoReady			= false;
	
	private boolean						playerOneDisabled		= false;
	private boolean						playerTwoDisabled		= false;
	
	private WrongSelectionIndicator[]	errorIndicators			= new WrongSelectionIndicator[2];
	
	private int							currentTilesetNum		= 0;
	
	private Tileset						currentTileset;
	
	public static GameScene getInstance()
	{
		if (instance == null) instance = new GameScene();
		return instance;
	}
	
	public GameScene()
	{
		instance = this;
		activity = ReflexActivity.getInstance();
		currentTileset = new Tileset("base");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		sceneAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 2048, 1024);
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "background.png", 0, 0);
		final TextureRegion barRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "bar.png", (int) backgroundRegion.getWidth(), 0);
		final TextureRegion playerOneIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerOneIntro.png", (int) (barRegion.getTextureX() + barRegion.getWidth()), 0);
		final TextureRegion playerTwoIntroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "playerTwoIntro.png", (int) (playerOneIntroRegion.getTextureX() + playerOneIntroRegion.getWidth()), 0);
		sceneAtlas.load();
		
		final Sprite backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		
		backgroundSprite.setZIndex(BACKGROUND_Z);
		
		barSprite = new Sprite(0, (CAMERA_HEIGHT - barRegion.getHeight()) / 2, barRegion, activity.getVertexBufferObjectManager());
		barSprite.setZIndex(FOREGROUND_Z);
		
		playerOneIntro = new Sprite(0, 0, playerOneIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerOneIntro.registerEntityModifier(new MoveYModifier(1.0f, playerOneIntro.getY(), -playerOneIntro.getHeight())
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							playerOneReady = true;
							super.onModifierFinished(pItem);
						}
					});
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		playerOneIntro.setZIndex(FOREGROUND_Z);
		playerTwoIntro = new Sprite(0, CAMERA_HEIGHT - playerTwoIntroRegion.getHeight(), playerTwoIntroRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
				{
					playerTwoIntro.registerEntityModifier(new MoveYModifier(1.0f, playerTwoIntro.getY(), CAMERA_HEIGHT)
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							playerTwoReady = true;
							super.onModifierFinished(pItem);
						}
					});
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		playerTwoIntro.setZIndex(FOREGROUND_Z);
		
		this.registerTouchArea(playerOneIntro);
		this.registerTouchArea(playerTwoIntro);
		
		this.attachChild(backgroundSprite);
		this.attachChild(barSprite);
		currentTileset.setupScene();
		this.registerUpdateHandler(new IUpdateHandler()
		{
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				Update(pSecondsElapsed);
			}
			
			@Override
			public void reset()
			{
				
			}
		});
		
		this.attachChild(playerOneIntro);
		this.attachChild(playerTwoIntro);
		
		for (int i = 0; i < 2; i++)
		{
			errorIndicators[i] = new WrongSelectionIndicator(i);
			errorIndicators[i].setScene(this);
			
		}
		
		this.sortChildren();
		
	}
	
	public void buttonPressed(final GameButton button)
	{
		switch (gameState)
		{
			case GameState.WAITING_FOR_BUTTON:
				if (checkPlayerDisabled(button.getPlayer())) return;
				if (button.getButtonNumber() == (currentTileset.getCurrentButtonNumber() + 1))
				{
					final GameButton displayButton = currentTileset.getDisplayButton();
					displayButton.buttonSprite.registerEntityModifier(new MoveModifier(WIN_MOVE_MOD_TIME, displayButton.buttonSprite.getX(), button.buttonSprite.getX(), displayButton.buttonSprite.getY(), button.buttonSprite.getY())
					{
						@Override
						protected void onModifierFinished(IEntity pItem)
						{
							currentTileset.resetDisplayButton(pItem);
							changeState(GameState.PICKING_NEW_BUTTON);
							switch (button.getPlayer())
							{
								case PLAYER_ONE:
									barSprite.registerEntityModifier(new MoveByModifier(WIN_MOVE_MOD_TIME, 0, -BAR_SPEED));
									barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, barSprite.getScaleX(), 1.5f, 1.0f, 1.0f), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.5f, 1.0f, 1.0f, 1.0f)));
									break;
								case PLAYER_TWO:
									barSprite.registerEntityModifier(new MoveByModifier(WIN_MOVE_MOD_TIME, 0, BAR_SPEED));
									barSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(WIN_MOVE_MOD_TIME / 2, barSprite.getScaleX(), 1.5f, 1.0f, 1.0f), new ScaleModifier(WIN_MOVE_MOD_TIME / 2, 1.5f, 1.0f, 1.0f, 1.0f)));
									break;
							}
							super.onModifierFinished(pItem);
						}
						
					});
					changeState(GameState.SHOWING_WIN);
				} else
				{
					disablePlayer(button);
				}
				break;
		}
	}
	
	private void disablePlayer(GameButton button)
	{
		this.errorIndicators[button.getPlayer()].startIndicator(button.buttonSprite.getX() + button.buttonSprite.getWidth() / 2, button.buttonSprite.getY() + button.buttonSprite.getHeight() / 2);
		switch (button.getPlayer())
		{
			case PLAYER_ONE:
				playerOneDisabled = true;
				break;
			case PLAYER_TWO:
				playerTwoDisabled = true;
				break;
		}
	}
	
	public void enablePlayer(int player)
	{
		switch (player)
		{
			case PLAYER_ONE:
				playerOneDisabled = false;
				break;
			case PLAYER_TWO:
				playerTwoDisabled = false;
				break;
		}
	}
	
	private boolean checkPlayerDisabled(int player)
	{
		switch (player)
		{
			case GameButton.PLAYER_ONE:
				if (playerOneDisabled) return true;
				break;
			case GameButton.PLAYER_TWO:
				if (playerTwoDisabled) return true;
				break;
		
		}
		return false;
	}
	
	private void Update(float pSecondsElapsed)
	{
		switch (gameState)
		{
			case GameState.INTRO:
				if (playerOneReady && playerTwoReady) changeState(GameState.PICKING_NEW_BUTTON);
				break;
			case GameState.PICKING_NEW_BUTTON:
				if (secondsOnCurrentState >= 1)
				{
					currentTileset.newButton();
					enablePlayer(PLAYER_ONE);
					enablePlayer(PLAYER_TWO);
					changeState(GameState.WAITING_FOR_BUTTON);
				}
				break;
			case GameState.WAITING_FOR_BUTTON:
				
				break;
		}
		
		secondsOnCurrentState += pSecondsElapsed;
	}
	
	private void changeState(int newState)
	{
		this.gameState = newState;
		secondsOnCurrentState = 0;
	}
	
	public class GameState
	{
		public static final int	INTRO				= 0;
		public static final int	WAITING_FOR_BUTTON	= INTRO + 1;
		public static final int	PICKING_NEW_BUTTON	= WAITING_FOR_BUTTON + 1;
		public static final int	SHOWING_WIN			= PICKING_NEW_BUTTON + 1;
	}
	
	public void nextTileset()
	{
		
		currentTileset.clearTileset();
		switch (currentTilesetNum)
		{
			case 0:
				currentTileset = new Tileset("second");
				currentTilesetNum++;
				break;
			case 1:
				
				currentTileset = new Tileset("base");
				currentTilesetNum = 0;
				break;
		}
		
		currentTileset.setupScene();
		changeState(GameState.PICKING_NEW_BUTTON);
		
	}
}
