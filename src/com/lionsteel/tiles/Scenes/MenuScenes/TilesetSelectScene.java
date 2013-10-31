package com.lionsteel.tiles.Scenes.MenuScenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Entities.Tileset;
import com.lionsteel.tiles.Entities.TilesetPreviewButton;

public class TilesetSelectScene extends TilesScrollableScene
{
	
	final Sprite						titleSprite;
	
	final int							TITLE_Y					= 40;
	final int							TITLE_BOTTOM_PADDING	= 20;
	final int							BUTTON_PADDING			= 7;
	final int							BOTTOM_PADDING			= 10;
	
	final TilesetPreviewButton			buttons[]				= new TilesetPreviewButton[Tileset.tilesetList.length];
	
	private static TilesetSelectScene	instance;
	
	public static TilesetSelectScene getInstance()
	{
		if (instance == null)
			instance = new TilesetSelectScene();
		return instance;
	}
	
	private TilesetSelectScene()
	{
		super();
		
		instance = this;
		
		activity.updateLoadProgress("Loading Tileset Menu");
		
		this.setBackgroundEnabled(false);
		
		final BuildableBitmapTextureAtlas sceneAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/TilesetSelectScene/");
		
		final TextureRegion titleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sceneAtlas, activity, "title.png");
		
		try
		{
			sceneAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			sceneAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
		titleSprite = new Sprite((CAMERA_WIDTH - titleRegion.getWidth()) / 2, TITLE_Y, titleRegion, activity.getVertexBufferObjectManager());
		this.attachChild(titleSprite);
		float nextYPos = TITLE_Y + titleSprite.getHeight() + TITLE_BOTTOM_PADDING;
		for (int x = 0; x < buttons.length; x++)
		{
			
			buttons[x] = new TilesetPreviewButton(Tileset.tilesetList[x]);
			
			addButton(buttons[x].getButton());
			buttons[x].getButton().center(nextYPos);
			nextYPos = buttons[x].getButton().getBottom() + BUTTON_PADDING;
			
		}
		
		MAX_Y = nextYPos + BOTTOM_PADDING;
		
	}
	
	public void clearButtons()
	{
		for (TilesetPreviewButton button : buttons)
		{
			if (button == null)
				continue;
			removeButton(button.getButton());
			clearTouchAreas();
		}
	}
	
	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.TILESET_MENU);
	}
	
	public static void clear()
	{
		instance = null;
		
	}
	
}
