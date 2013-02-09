package com.lionsteel.reflexmulti.Scenes;

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
import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.Entities.TouchControls.NoTouchControl;
import com.lionsteel.reflexmulti.Entities.TouchControls.TouchControl;
import com.lionsteel.reflexmulti.Entities.TouchControls.YesTouchControl;

public class QuitPromptScene extends ReflexMenuScene implements ReflexConstants
{
	ReflexActivity					activity;

	private final Sprite			areYouSureSprite;
	private final TouchControl[]	touchControls	= new TouchControl[2];

	private final Runnable			quitAction;

	public QuitPromptScene(final Runnable quitAction)
	{
		activity = ReflexActivity.getInstance();
		this.quitAction = quitAction;
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

		touchControls[0] = new YesTouchControl(new Runnable()
		{

			@Override
			public void run()
			{
				quitAction.run();
			}
		}, null);

		touchControls[1] = new NoTouchControl(new Runnable()
		{
			@Override
			public void run()
			{
				touchControls[1].resetButton();
				mParentScene.clearChildScene();
			}
		}, null);

		final Rectangle background = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
		background.setColor(0, 0, 0, .9f);
		final float TOUCH_CONTROL_WIDTH = touchControls[0].touchImage.getWidth();
		areYouSureSprite = new Sprite((CAMERA_WIDTH - areYouSureRegion.getWidth()) / 2, (CAMERA_HEIGHT - areYouSureRegion.getHeight()) / 2, areYouSureRegion, activity.getVertexBufferObjectManager());
		touchControls[0].setPosition((CAMERA_WIDTH - TOUCH_CONTROL_WIDTH) / 3, areYouSureSprite.getY() + 140);
		touchControls[1].setPosition((CAMERA_WIDTH - TOUCH_CONTROL_WIDTH) * 2 / 3, areYouSureSprite.getY() + 140);

		this.attachChild(background);
		this.attachChild(areYouSureSprite);
		this.attachChild(touchControls[0]);
		this.attachChild(touchControls[1]);

	}

	public void callQuitAction()
	{
		quitAction.run();
	}

	@Override
	public void registerTouchAreas()
	{
		super.registerTouchAreas();
		touchControls[0].initButton();
		touchControls[1].initButton();
		this.registerTouchArea(touchControls[0].touchImage);
		this.registerTouchArea(touchControls[1].touchImage);

	}

}
