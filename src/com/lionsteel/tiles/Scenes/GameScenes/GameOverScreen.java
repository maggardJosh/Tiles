package com.lionsteel.tiles.Scenes.GameScenes;

import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import android.opengl.GLES20;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.SongManager;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.GameScene;
import com.lionsteel.tiles.BaseClasses.TilesMenuButton;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.BaseClasses.TouchControl;
import com.lionsteel.tiles.Constants.TilesConstants;

public class GameOverScreen extends TilesMenuScene implements TilesConstants
{
	private final TilesMainActivity				activity;
	private final BuildableBitmapTextureAtlas	atlas;

	private final Sprite						winnerSprite;
	private final Sprite						loserSprite;
	private final Sprite[]						tieSprite				= new Sprite[2];

	private boolean								playerOneRematch;
	private boolean								playerTwoRematch;

	private final Text[]						labelOne				= new Text[2];
	private final Text[]						valueOne				= new Text[2];
	private final Text[]						labelTwo				= new Text[2];
	private final Text[]						valueTwo				= new Text[2];

	private TilesMenuButton						quitButton;

	private final TouchControl[]				playerRematchControls	= new TouchControl[2];

	private final SpriteParticleSystem			playerOneParticleSystem;
	private final SpriteParticleSystem			playerTwoParticleSystem;

	private final ColorParticleModifier<Sprite>	winColorMod;
	private final ColorParticleModifier<Sprite>	loseColorMod;
	private final ColorParticleModifier<Sprite>	tieColorMod;

	@Override
	public void dispose()
	{
		atlas.unload();
		quitButton.dispose();
		activity.runOnUpdateThread(new Runnable()
		{

			@Override
			public void run()
			{
				detachChildren();
			}
		});
		super.dispose();
	}

	public GameOverScreen()
	{

		activity = TilesMainActivity.getInstance();
		this.setBackgroundEnabled(false);

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameOverScene/");

		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 1024);
		final TextureRegion winnerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "winner.png");
		final TextureRegion loserRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "loser.png");
		final TextureRegion tieRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "tie.png");
		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		quitButton = new TilesMenuButton(SharedResources.getInstance().exitGameButtonRegion, new Runnable()
		{
			@Override
			public void run()
			{
				activity.onBackPressed();
			}
		});
		quitButton.setPosition(CAMERA_WIDTH - 3 - quitButton.getWidth(), (CAMERA_HEIGHT - quitButton.getHeight()) / 2);

		winnerSprite = new Sprite(0, 0, winnerRegion, activity.getVertexBufferObjectManager());
		winnerSprite.setRotationCenter(winnerSprite.getWidth() / 2, winnerSprite.getHeight() / 2);
		loserSprite = new Sprite(0, 0, loserRegion, activity.getVertexBufferObjectManager());
		for (int i = 0; i < 2; i++)
			tieSprite[i] = new Sprite(0, 0, tieRegion, activity.getVertexBufferObjectManager());

		tieSprite[PLAYER_TWO].setPosition(0, (CAMERA_HEIGHT - tieRegion.getHeight()));
		tieSprite[PLAYER_ONE].setRotationCenter(tieSprite[PLAYER_ONE].getWidth() / 2, tieSprite[PLAYER_ONE].getHeight() / 2);
		tieSprite[PLAYER_ONE].setRotation(180);

		Rectangle backgroundRect = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		backgroundRect.setColor(0, 0, 0, .9f);

		this.attachChild(backgroundRect);
		this.attachChild(winnerSprite);
		this.attachChild(loserSprite);
		this.attachChild(tieSprite[0]);
		this.attachChild(tieSprite[1]);
		addButton(quitButton);

		prepareTouchControls();

		final Font mFont = SharedResources.getInstance().mFont;
		for (int i = 0; i < 2; i++)
		{
			labelOne[i] = new Text(0, 0, mFont, "", 15, activity.getVertexBufferObjectManager());
			labelTwo[i] = new Text(0, 0, mFont, "", 15, activity.getVertexBufferObjectManager());
			valueOne[i] = new Text(0, 0, mFont, "", 15, activity.getVertexBufferObjectManager());
			valueTwo[i] = new Text(0, 0, mFont, "", 15, activity.getVertexBufferObjectManager());
			this.attachChild(labelOne[i]);
			this.attachChild(labelTwo[i]);
			this.attachChild(valueOne[i]);
			this.attachChild(valueTwo[i]);
			if (i == 1)
			{
				labelOne[i].setRotation(180);
				labelTwo[i].setRotation(180);
				valueOne[i].setRotation(180);
				valueTwo[i].setRotation(180);
				labelOne[i].setRotationCenterY(mFont.getLineHeight());
				labelTwo[i].setRotationCenterY(mFont.getLineHeight());
				valueOne[i].setRotationCenterY(mFont.getLineHeight());
				valueTwo[i].setRotationCenterY(mFont.getLineHeight());
			}
		}

		winColorMod = new ColorParticleModifier<Sprite>(0.0f, 5.5f, 0.0f, 0.0f, .7f, 1.0f, 0.0f, 0.0f);
		loseColorMod = new ColorParticleModifier<Sprite>(0.0f, 5.5f, 0.7f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
		tieColorMod = new ColorParticleModifier<Sprite>(0.0f, 5.5f, 0.7f, 1.0f, 0.7f, 1.0f, 0.7f, 1.0f);

		final float minYStartVel = 20;
		final float maxYStartVel = 60;
		final float maxXAccel = 20;
		final float minYAccel = 10;
		final float maxYAccel = 20;
		final float expireTime = 3.0f;
		final float minScale = .1f;
		final float maxScale = 2.0f;

		playerTwoParticleSystem = new SpriteParticleSystem(new RectangleParticleEmitter(CAMERA_WIDTH / 2, -50, CAMERA_WIDTH, 2), 1, 13, 40, SharedResources.getInstance().particlePointRegion, activity.getVertexBufferObjectManager());
		playerTwoParticleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE));
		playerTwoParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(0, 0, minYStartVel, maxYStartVel));
		playerTwoParticleSystem.addParticleInitializer(new AccelerationParticleInitializer<Sprite>(-maxXAccel, maxXAccel, minYAccel, maxYAccel));
		playerTwoParticleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(0, 1.0f, 0.0f));
		playerTwoParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(expireTime));

		playerTwoParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, expireTime*.7f, minScale, maxScale));
		playerTwoParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(expireTime*.7f, expireTime, maxScale, 0));
		playerTwoParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, .3f, 0.0f, 1.0f));

		playerTwoParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(expireTime * .9f, expireTime, 1.0f, 0.0f));

		attachChild(playerTwoParticleSystem);

		playerOneParticleSystem = new SpriteParticleSystem(new RectangleParticleEmitter(CAMERA_WIDTH / 2, CAMERA_HEIGHT, CAMERA_WIDTH, 2), 1, 13, 40, SharedResources.getInstance().particlePointRegion, activity.getVertexBufferObjectManager());
		playerOneParticleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE));
		playerOneParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(0, 0, -maxYStartVel, -minYStartVel));
		playerOneParticleSystem.addParticleInitializer(new AccelerationParticleInitializer<Sprite>(-maxXAccel, maxXAccel, -maxYAccel, -minYAccel));
		playerOneParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(expireTime));

		playerOneParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, expireTime*.7f, minScale, maxScale));  
		playerOneParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(expireTime*.7f, expireTime, maxScale, 0));
		playerOneParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, .3f, 0.0f, 1.0f));

		playerOneParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(expireTime * .9f, expireTime, 1.0f, 0.0f));

		attachChild(playerOneParticleSystem);

	}

	private void prepareTouchControls()
	{
		playerRematchControls[PLAYER_TWO] = new TouchControl("Ready", new Runnable()
		{
			@Override
			public void run()
			{
				playerOneRematch = true;
				if (playerTwoRematch && mParentScene instanceof GameScene)
					((GameScene) mParentScene).startRematch();
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				playerOneRematch = false;
			}
		});
		playerRematchControls[PLAYER_ONE] = new TouchControl("Ready", new Runnable()
		{
			@Override
			public void run()
			{

				playerTwoRematch = true;
				if (playerOneRematch && mParentScene instanceof GameScene)
					((GameScene) mParentScene).startRematch();

			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				playerTwoRematch = false;
			}
		});
		final float TOUCH_WIDTH = playerRematchControls[0].outerImage.getWidth();
		playerRematchControls[PLAYER_TWO].setPosition((CAMERA_WIDTH - TOUCH_WIDTH) / 2, CAMERA_HEIGHT - TOUCH_WIDTH - REMATCH_TOUCH_PADDING);
		playerRematchControls[PLAYER_ONE].setPosition((CAMERA_WIDTH - TOUCH_WIDTH) / 2, REMATCH_TOUCH_PADDING);
		playerRematchControls[PLAYER_ONE].setRotation(180);

		this.attachChild(playerRematchControls[PLAYER_TWO]);
		this.attachChild(playerRematchControls[PLAYER_ONE]);
	}

	public boolean isRematchTrue()
	{
		return playerOneRematch && playerTwoRematch;
	}

	@Override
	public void registerTouchAreas()
	{
		registerTouchArea(playerRematchControls[PLAYER_TWO].outerImage);
		registerTouchArea(playerRematchControls[PLAYER_ONE].outerImage);

		super.registerTouchAreas();
	}

	@Override
	public void logFlurryEvent()
	{

	}

	public void setWinner(int winningPlayer)
	{
		switch (winningPlayer)
		{
		case PLAYER_TWO:
			tieSprite[0].setVisible(false);
			tieSprite[1].setVisible(false);
			winnerSprite.setVisible(true);
			loserSprite.setVisible(true);
			winnerSprite.setPosition(0, 0);
			winnerSprite.setRotation(180);
			loserSprite.setPosition(0, 620);
			loserSprite.setRotation(0);
			removeParticleColor();
			playerTwoParticleSystem.addParticleModifier(winColorMod);
			playerOneParticleSystem.addParticleModifier(loseColorMod);

			break;
		case PLAYER_ONE:
			tieSprite[0].setVisible(false);
			tieSprite[1].setVisible(false);
			winnerSprite.setVisible(true);
			loserSprite.setVisible(true);
			winnerSprite.setPosition(0, 620);
			winnerSprite.setRotation(0);
			loserSprite.setPosition(0, 0);
			loserSprite.setRotation(180);
			removeParticleColor();
			playerOneParticleSystem.addParticleModifier(winColorMod);
			playerTwoParticleSystem.addParticleModifier(loseColorMod);
			break;
		case TIE:
			winnerSprite.setVisible(false);
			loserSprite.setVisible(false);
			tieSprite[0].setVisible(true);
			tieSprite[1].setVisible(true);

			removeParticleColor();
			playerOneParticleSystem.addParticleModifier(tieColorMod);
			playerTwoParticleSystem.addParticleModifier(tieColorMod);
			break;
		}
		TilesMainActivity.endGameEvent();

		playerOneRematch = false;
		playerTwoRematch = false;

	}

	private void removeParticleColor()
	{
		playerOneParticleSystem.removeParticleModifier(winColorMod);
		playerOneParticleSystem.removeParticleModifier(loseColorMod);
		playerTwoParticleSystem.removeParticleModifier(winColorMod);
		playerTwoParticleSystem.removeParticleModifier(loseColorMod);
		playerOneParticleSystem.removeParticleModifier(tieColorMod);
		playerTwoParticleSystem.removeParticleModifier(tieColorMod);
	}

	public void setLabels(final String labelOne, final String labelTwo)
	{
		for (int i = 0; i < 2; i++)
		{
			this.labelOne[i].setText(labelOne);
			this.labelTwo[i].setText(labelTwo);
		}
		this.labelOne[PLAYER_TWO].setPosition(LABEL_ONE_CENTER.x - this.labelOne[PLAYER_TWO].getWidth() / 2, LABEL_ONE_CENTER.y);
		this.labelTwo[PLAYER_TWO].setPosition(LABEL_TWO_CENTER.x - this.labelTwo[PLAYER_TWO].getWidth() / 2, LABEL_TWO_CENTER.y);
		this.labelOne[PLAYER_ONE].setPosition(LABEL_TWO_CENTER.x - this.labelOne[PLAYER_ONE].getWidth() / 2, CAMERA_HEIGHT - LABEL_ONE_CENTER.y - this.labelOne[PLAYER_ONE].getHeight());
		this.labelTwo[PLAYER_ONE].setPosition(LABEL_ONE_CENTER.x - this.labelTwo[PLAYER_ONE].getWidth() / 2, CAMERA_HEIGHT - LABEL_TWO_CENTER.y - this.labelTwo[PLAYER_ONE].getHeight());

	}

	public void setPlayerValues(final int player, final String valueOne, final String valueTwo)
	{
		this.valueOne[(player + 1) % 2].setText(valueOne);
		this.valueTwo[(player + 1) % 2].setText(valueTwo);
		centerPlayerValues((player + 1) % 2);
	}

	private void centerPlayerValues(final int player)
	{
		switch (player)
		{
		case PLAYER_TWO:
			this.valueOne[PLAYER_TWO].setPosition(VALUE_ONE_CENTER.x - this.valueOne[PLAYER_TWO].getWidth() / 2, VALUE_ONE_CENTER.y);
			this.valueTwo[PLAYER_TWO].setPosition(VALUE_TWO_CENTER.x - this.valueTwo[PLAYER_TWO].getWidth() / 2, VALUE_TWO_CENTER.y);
			break;
		case PLAYER_ONE:
			this.valueOne[PLAYER_ONE].setPosition(VALUE_TWO_CENTER.x - this.valueOne[PLAYER_ONE].getWidth() / 2, CAMERA_HEIGHT - VALUE_ONE_CENTER.y - this.labelOne[PLAYER_ONE].getHeight());
			this.valueTwo[PLAYER_ONE].setPosition(VALUE_ONE_CENTER.x - this.valueTwo[PLAYER_ONE].getWidth() / 2, CAMERA_HEIGHT - VALUE_TWO_CENTER.y - this.labelTwo[PLAYER_ONE].getHeight());
			break;
		}
	}

	@Override
	public void initScene()
	{
		for (TouchControl controls : playerRematchControls)
			controls.initButton();
		SongManager.getInstance().setVolumeMultiplier(MUFFLED_VOLUME);
	}

	@Override
	protected void exitScene()
	{
		// TODO Auto-generated method stub

	}

}
