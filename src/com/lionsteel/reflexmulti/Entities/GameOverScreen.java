package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.lionsteel.reflexmulti.ReflexActivity;
import com.lionsteel.reflexmulti.ReflexConstants;

public class GameOverScreen extends Entity implements ReflexConstants
{
	private final ReflexActivity		activity;
	private final BitmapTextureAtlas	atlas;
	
	private final Sprite				backgroundSprite;
	private final Sprite				winnerSprite;
	private final Sprite				loserSprite;
	
	public GameOverScreen()
	{
		super(0, 0);
		
		activity = ReflexActivity.getInstance();
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/GameScene/");
		
		atlas = new BitmapTextureAtlas(activity.getTextureManager(), 2048, 1024);
		final TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "gameOverBackground.png", 0, 0);
		final TextureRegion winnerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "winner.png", (int) backgroundRegion.getWidth(), 0);
		final TextureRegion loserRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "loser.png", (int) (winnerRegion.getTextureX() + winnerRegion.getWidth()), 0);
		atlas.load();
		
		backgroundSprite = new Sprite(0, 0, backgroundRegion, activity.getVertexBufferObjectManager());
		winnerSprite = new Sprite(0, 0, winnerRegion, activity.getVertexBufferObjectManager());
		winnerSprite.setRotationCenter(winnerSprite.getWidth()/2, winnerSprite.getHeight()/2);
		loserSprite = new Sprite(0, 0, loserRegion, activity.getVertexBufferObjectManager());
		
		this.attachChild(backgroundSprite);
		this.attachChild(winnerSprite);
		this.attachChild(loserSprite);
		
		this.setVisible(false);
	}
	
	public void show(final int winningPlayer)
	{
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
		
		this.setVisible(true);
	}
	
}
