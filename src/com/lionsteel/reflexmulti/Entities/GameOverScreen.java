package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.BaseClasses.GameScene;
import com.lionsteel.reflexmulti.BaseClasses.ReflexMenuScene;
import com.lionsteel.reflexmulti.BaseClasses.TouchControl;
import com.lionsteel.reflexmulti.Constants.ReflexConstants;
import com.lionsteel.reflexmulti.Entities.TouchControls.ReadyTouchControl;

public class GameOverScreen extends ReflexMenuScene implements ReflexConstants
{
	private final ReflexActivity				activity;
	private final BuildableBitmapTextureAtlas	atlas;

	private final Sprite						winnerSprite;
	private final Sprite						loserSprite;
	private final Sprite[]						tieSprite				= new Sprite[2];

	private boolean								playerOneRematch;
	private boolean								playerTwoRematch;

	private final TouchControl[]				playerRematchControls	= new TouchControl[2];

	public GameOverScreen()
	{

		activity = ReflexActivity.getInstance();
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

		winnerSprite = new Sprite(0, 0, winnerRegion, activity.getVertexBufferObjectManager());
		winnerSprite.setRotationCenter(winnerSprite.getWidth() / 2, winnerSprite.getHeight() / 2);
		loserSprite = new Sprite(0, 0, loserRegion, activity.getVertexBufferObjectManager());
		for (int i = 0; i < 2; i++)
			tieSprite[i] = new Sprite(0, 0, tieRegion, activity.getVertexBufferObjectManager());

		tieSprite[PLAYER_ONE].setPosition(0, (CAMERA_HEIGHT - tieRegion.getHeight()));
		tieSprite[PLAYER_TWO].setRotationCenter(tieSprite[PLAYER_TWO].getWidth() / 2, tieSprite[PLAYER_TWO].getHeight() / 2);
		tieSprite[PLAYER_TWO].setRotation(180);

		Rectangle backgroundRect = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		backgroundRect.setColor(0, 0, 0, .8f);

		this.attachChild(backgroundRect);
		this.attachChild(winnerSprite);
		this.attachChild(loserSprite);
		this.attachChild(tieSprite[0]);
		this.attachChild(tieSprite[1]);

		prepareTouchControls();

	}

	private void prepareTouchControls()
	{
		playerRematchControls[PLAYER_ONE] = new ReadyTouchControl(new Runnable()
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
		playerRematchControls[PLAYER_TWO] = new ReadyTouchControl(new Runnable()
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
		final float TOUCH_WIDTH = playerRematchControls[0].touchImage.getWidth();
		playerRematchControls[PLAYER_ONE].setPosition((CAMERA_WIDTH - TOUCH_WIDTH) / 2, CAMERA_HEIGHT - TOUCH_WIDTH - REMATCH_TOUCH_PADDING);
		playerRematchControls[PLAYER_TWO].setPosition((CAMERA_WIDTH - TOUCH_WIDTH) / 2, REMATCH_TOUCH_PADDING);
		playerRematchControls[PLAYER_TWO].setRotation(180);

		this.attachChild(playerRematchControls[PLAYER_ONE]);
		this.attachChild(playerRematchControls[PLAYER_TWO]);
	}

	public boolean isRematchTrue()
	{
		return playerOneRematch && playerTwoRematch;
	}

	@Override
	public void registerTouchAreas()
	{
		registerTouchArea(playerRematchControls[PLAYER_ONE].touchImage);
		registerTouchArea(playerRematchControls[PLAYER_TWO].touchImage);

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
		case PLAYER_ONE:
			tieSprite[0].setVisible(false);
			tieSprite[1].setVisible(false);
			winnerSprite.setVisible(true);
			loserSprite.setVisible(true);
			winnerSprite.setPosition(0, 0);
			winnerSprite.setRotation(180);
			loserSprite.setPosition(0, 620);
			loserSprite.setRotation(0);
			break;
		case PLAYER_TWO:
			tieSprite[0].setVisible(false);
			tieSprite[1].setVisible(false);
			winnerSprite.setVisible(true);
			loserSprite.setVisible(true);
			winnerSprite.setPosition(0, 620);
			winnerSprite.setRotation(0);
			loserSprite.setPosition(0, 0);
			loserSprite.setRotation(180);
			break;
		case TIE:
			winnerSprite.setVisible(false);
			loserSprite.setVisible(false);
			tieSprite[0].setVisible(true);
			tieSprite[1].setVisible(true);
			break;
		}
		ReflexActivity.endGameEvent();

		playerOneRematch = false;
		playerTwoRematch = false;

	}

}
