package com.lionsteel.reflexmulti;

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

import com.lionsteel.reflexmulti.Scenes.ReflexMenuScene;

public class QuitPromptScene extends ReflexMenuScene implements ReflexConstants
{
	ReflexActivity					activity;

	private final Sprite			areYouSureSprite;
	private final YesTouchControl	touchControl;

	public QuitPromptScene()
	{
		activity = ReflexActivity.getInstance();
		this.setBackgroundEnabled(false);
		final BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 128);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/QuitPromptScene/");
		final TextureRegion areYouSureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "areYouSure.png");//, pTextureY)

		try
		{
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			atlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		touchControl = new YesTouchControl(new Runnable()
		{

			@Override
			public void run()
			{
				activity.finish();
			}
		}, null);

		final Rectangle background = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		background.setColor(0, 0, 0, .7f);
		final float TOUCH_CONTROL_WIDTH = touchControl.touchImage.getWidth();
		areYouSureSprite = new Sprite((CAMERA_WIDTH - areYouSureRegion.getWidth()) / 2, (CAMERA_HEIGHT - areYouSureRegion.getHeight()) / 2, areYouSureRegion, activity.getVertexBufferObjectManager());
		touchControl.setPosition((CAMERA_WIDTH - TOUCH_CONTROL_WIDTH) / 2, areYouSureSprite.getY() + 140);

		this.attachChild(background);
		this.attachChild(areYouSureSprite);
		this.attachChild(touchControl);

	}

	@Override
	protected void registerTouchAreas()
	{
		this.registerTouchArea(touchControl.touchImage);

	}

}
