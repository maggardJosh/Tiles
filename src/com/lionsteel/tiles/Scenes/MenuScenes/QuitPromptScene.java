package com.lionsteel.tiles.Scenes.MenuScenes;

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

import com.flurry.android.FlurryAgent;
import com.lionsteel.tiles.TilesMainActivity;
import com.lionsteel.tiles.BaseClasses.TilesMenuScene;
import com.lionsteel.tiles.BaseClasses.TouchControl;
import com.lionsteel.tiles.Constants.FlurryAgentEventStrings;
import com.lionsteel.tiles.Constants.TilesConstants;

public class QuitPromptScene extends TilesMenuScene implements TilesConstants
{
	TilesMainActivity					activity;

	private final Sprite			areYouSureSprite;
	private final TouchControl[]	touchControls	= new TouchControl[2];

	private final Runnable			quitAction;

	@Override
	public void logFlurryEvent()
	{
		FlurryAgent.logEvent(FlurryAgentEventStrings.QUIT_PROMPT);
	}

	public QuitPromptScene(final Runnable quitAction)
	{
		activity = TilesMainActivity.getInstance();
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

		touchControls[0] = new TouchControl("Yes",new Runnable()
		{

			@Override
			public void run()
			{
				quitAction.run();
			}
		}, null);

		touchControls[1] = new TouchControl("No", new Runnable()
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
		final float TOUCH_CONTROL_WIDTH = touchControls[0].outerImage.getWidth();
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
		this.registerTouchArea(touchControls[0].outerImage);
		this.registerTouchArea(touchControls[1].outerImage);

	}

	@Override
	public void initScene()
	{
		for(TouchControl controls: touchControls)
			controls.initButton();
	}

	@Override
	protected void exitScene()
	{
		// TODO Auto-generated method stub
		
	}

}
