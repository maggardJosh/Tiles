package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.reflexmulti.ReadyTouchControl;
import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.TouchControl;
import com.lionsteel.reflexmulti.YesTouchControl;

public class GameOverScreen extends Entity implements ReflexConstants
{
	private final ReflexActivity				activity;
	private final BuildableBitmapTextureAtlas	atlas;

	private final Sprite						winnerSprite;
	private final Sprite						loserSprite;

	private boolean								playerOneRematch;
	private boolean								playerTwoRematch;

	private Scene								parentScene;

	private final TouchControl[]				playerRematchControls	= new TouchControl[2];

	public GameOverScreen(Scene scene)
	{
		super(0, 0);

		activity = ReflexActivity.getInstance();

		parentScene = scene;

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameOverScene/");

		atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512);
		final TextureRegion winnerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "winner.png");
		final TextureRegion loserRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "loser.png");

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

		Rectangle backgroundRect = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		backgroundRect.setColor(0, 0, 0, .7f);

		this.attachChild(backgroundRect);
		this.attachChild(winnerSprite);
		this.attachChild(loserSprite);

		prepareTouchControls();

		this.setVisible(false);
	}

	private void prepareTouchControls()
	{
		playerRematchControls[PLAYER_ONE] = new ReadyTouchControl(new Runnable()
		{

			@Override
			public void run()
			{
				playerOneRematch = true;

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

	public void show(final int winningPlayer)
	{
		if (this.isVisible())
			return;
		this.setX(CAMERA_WIDTH);
		this.clearEntityModifiers();
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, CAMERA_WIDTH, 0));
		switch (winningPlayer)
		{
		case PLAYER_ONE:
			winnerSprite.setPosition(0, 0);
			winnerSprite.setRotation(180);
			loserSprite.setPosition(0, 620);
			loserSprite.setRotation(0);
			break;
		case PLAYER_TWO:
			winnerSprite.setPosition(0, 620);
			winnerSprite.setRotation(0);
			loserSprite.setPosition(0, 0);
			loserSprite.setRotation(180);
			break;
		}
		playerOneRematch = false;
		playerTwoRematch = false;

		parentScene.registerTouchArea(playerRematchControls[PLAYER_ONE].touchImage);
		parentScene.registerTouchArea(playerRematchControls[PLAYER_TWO].touchImage);

		this.setVisible(true);
	}

	public void hide()
	{
		if (!this.isVisible())
			return;
		this.registerEntityModifier(new MoveXModifier(SCENE_TRANSITION_SECONDS, 0, CAMERA_WIDTH)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				setVisible(false);
				super.onModifierFinished(pItem);
			}
		});
		parentScene.unregisterTouchArea(playerRematchControls[PLAYER_ONE].touchImage);
		parentScene.unregisterTouchArea(playerRematchControls[PLAYER_TWO].touchImage);
	}

	public boolean isRematchTrue()
	{
		return playerOneRematch && playerTwoRematch;
	}

}
