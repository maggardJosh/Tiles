package com.lionsteel.tiles;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import com.lionsteel.tiles.Constants.Difficulty;
import com.lionsteel.tiles.Constants.GameMode;
import com.lionsteel.tiles.Constants.TilesConstants;

public class SharedResources implements TilesConstants
{
	private TilesMainActivity		activity;

	private static SharedResources	instance;

	public final TextureRegion		backgroundRegion;
	public final TextureRegion		touchImageRegion;
	public final TextureRegion		modeRegion[]		= new TextureRegion[6];
	public final TextureRegion		difficultyRegion[]	= new TextureRegion[4];
	public final TextureRegion		displayIndicatorRegion;
	public final TextureRegion		pauseButtonRegion;
	public final TextureRegion		exitGameButtonRegion;
	public final TextureRegion		cancelImageRegion;
	public final Font				mFont;
	public final Font				headingFont;
	public final ITexture			fontTexture;
	public final TextureRegion		musicNoteRegion;
	public final TextureRegion		soundEffectImageRegion;
	public final TextureRegion		buyTilesetButtonRegion;
	public final TextureRegion		backArrowRegion;
	public final TextureRegion		upArrowRegion;

	public Sound					tileCollectSound;

	public Sound					wrongTileSound;
	public Sound					tileCrashSound;
	public Sound					insaneSound;
	public Sound					insaneJump;
	public Sound					countdownSound;
	public Sound					menuBlip;
	public Sound					pauseSound;
	public Sound					countdownHit;
	public Sound					countdownFinalHit;

	public Music					menuMusic;
	public Music					versusMusic;
	public Music					freePlayMusic;

	public static SharedResources getInstance()
	{
		if (instance == null)
			instance = new SharedResources();
		return instance;
	}

	public SharedResources()
	{
		instance = this;
		activity = TilesMainActivity.getInstance();

		FontFactory.setAssetBasePath("fonts/");
		fontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		mFont = FontFactory.createFromAsset(activity.getFontManager(), fontTexture, activity.getAssets(), "gameFont.ttf", 18f, true, android.graphics.Color.WHITE);
		fontTexture.load();
		mFont.load();

		final BitmapTextureAtlas headingFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		headingFont = FontFactory.createFromAsset(activity.getFontManager(), headingFontTexture, activity.getAssets(), "gameFont.ttf", 45f, true, android.graphics.Color.WHITE);
		headingFontTexture.load();
		headingFont.load();

		final BuildableBitmapTextureAtlas buildableAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/SharedResources/");
		touchImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "touchImage.png");
		modeRegion[GameMode.REFLEX] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "reflex.png");
		modeRegion[GameMode.NON_STOP] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "nonStop.png");
		modeRegion[GameMode.RACE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "race.png");
		modeRegion[GameMode.FREE_PLAY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "freePlay.png");
		modeRegion[GameMode.FRENZY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "frenzy.png");
		modeRegion[GameMode.TIME_ATTACK] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "timeAttack.png");
		pauseButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "pauseButton.png");
		exitGameButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "exitButton.png");
		cancelImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "cancelImage.png");
		soundEffectImageRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "soundEffectImage.png");
		musicNoteRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "musicNote.png");
		displayIndicatorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "displayIndicator.png");
		backArrowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "backArrow.png");
		upArrowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableAtlas, activity, "upArrow.png");
		try
		{
			buildableAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			buildableAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		final BuildableBitmapTextureAtlas secondAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024);
		backgroundRegion = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(secondAtlas, activity, "background.png");

		try
		{
			secondAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			secondAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		final BuildableBitmapTextureAtlas difficultyAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 1024);
		difficultyRegion[Difficulty.EASY] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(difficultyAtlas, activity, "easy.png");
		difficultyRegion[Difficulty.NORMAL] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(difficultyAtlas, activity, "normal.png");
		difficultyRegion[Difficulty.HARD] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(difficultyAtlas, activity, "hard.png");
		difficultyRegion[Difficulty.INSANE] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(difficultyAtlas, activity, "insane.png");
		buyTilesetButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(difficultyAtlas, activity, "buyTilesets.png");
		try
		{
			difficultyAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 2, 4));
			difficultyAtlas.load();
		} catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}

		SoundFactory.setAssetBasePath("sfx/");
		MusicFactory.setAssetBasePath("sfx/");
		try
		{

			tileCollectSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "collectTile1.wav");
			tileCollectSound.setVolume(SOUND_EFFECT_VOLUME * .7f);
			tileCollectSound.setRate(MIN_TILE_COLLECT_RATE);
			wrongTileSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "wrongTile.ogg");
			wrongTileSound.setVolume(SOUND_EFFECT_VOLUME);
			tileCrashSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "crashTwo.ogg");
			tileCrashSound.setVolume(SOUND_EFFECT_VOLUME);
			insaneSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "insaneSound.wav");
			insaneSound.setVolume(SOUND_EFFECT_VOLUME);
			insaneJump = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "insaneJump.wav");
			insaneJump.setVolume(SOUND_EFFECT_VOLUME);
			countdownSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "countdown.wav");
			countdownSound.setVolume(SOUND_EFFECT_VOLUME);
			menuBlip = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "menuBlip.wav");
			menuBlip.setVolume(SOUND_EFFECT_VOLUME);
			pauseSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "pause.ogg");
			pauseSound.setVolume(SOUND_EFFECT_VOLUME);
			countdownHit = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "countdownHit.ogg");
			countdownHit.setVolume(SOUND_EFFECT_VOLUME);
			countdownFinalHit = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "countdownFinalHit.ogg");
			countdownFinalHit.setVolume(SOUND_EFFECT_VOLUME);

			menuMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "TilesMenuSong.ogg");
			versusMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "TilesVersusSong.ogg");
			freePlayMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "TilesFreePlaySong.ogg");
		} catch (IOException e)
		{
			Debug.e(e);
		}

	}

	public static void clear()
	{
		instance = null;
	}
}
