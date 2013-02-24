package com.lionsteel.tiles.Constants;

import org.andengine.util.color.Color;

import android.graphics.Point;

public interface TilesConstants
{
	public static final int		CAMERA_WIDTH					= 480;
	public static final int		CAMERA_HEIGHT					= 800;

	public static final int		NUM_BUTTONS						= 9;

	public static final int		BUTTON_WIDTH					= 110;
	public static final int		PAUSE_BAR_WIDTH					= 30;
	public static final int		BAR_SPEED						= 20;

	public static final int		DISPLAY_BUTTONS					= -1;
	public static final int		PLAYER_TWO						= 0;
	public static final int		PLAYER_ONE						= PLAYER_TWO + 1;
	public static final int		TIE								= PLAYER_ONE + 1;

	public static final int		BACK_ARROW_PADDING				= 10;
	public static final int		REMATCH_TOUCH_PADDING			= 200;

	public static final float	REFLEX_MIN_TIME					= 1.0f;
	public static final float	REFLEX_MAX_TIME					= 2.5f;

	public static final float	PLAYER_TILE_START_X				= 90;

	public static final float	TILE_BASE_PADDING				= 10;
	public static final float	TILE_BASE_RIGHT_SIDE			= PLAYER_TILE_START_X + BUTTON_WIDTH * 3 + TILE_BASE_PADDING;

	public static final float	PLAYER_TILES_ALPHA				= .3f;
	public static final int		PLAYER_RACE_TILE_PADDING		= 5;

	public static final float	RACE_SECONDS					= 30.0f;
	public static final int		BIG_PULSE_MOD					= 25;

	public static final Point	LABEL_ONE_CENTER				= new Point(CAMERA_WIDTH / 4, CAMERA_HEIGHT - 300);
	public static final Point	LABEL_TWO_CENTER				= new Point(CAMERA_WIDTH * 3 / 4, CAMERA_HEIGHT - 300);
	public static final Point	VALUE_ONE_CENTER				= new Point(CAMERA_WIDTH / 4, CAMERA_HEIGHT - 250);
	public static final Point	VALUE_TWO_CENTER				= new Point(CAMERA_WIDTH * 3 / 4, CAMERA_HEIGHT - 250);

	public static final float	MIN_TILE_COLLECT_RATE			= .7f;
	public static final float	MAX_TILE_COLLECT_RATE			= .9f;
	public static final float	TILE_COLLECT_RATE_INCREMENT		= .05f;

	public static final float	SOUND_EFFECT_VOLUME				= 1.0f;
	public static final float	MAX_NUM_MENU_SCENES				= 3;

	public static final float	SONG_TRANSITION_SPEED			= .5f;
	public static final float	MUFFLED_VOLUME					= .2f;

	public static final int		TIME_ATTACK_NUM_TILES			= 50;

	public static final float	OVERLAY_BACKGROUND_ALPHA		= .9f;
	public static final Color	VALUE_TEXT_COLOR				= new Color(.7f, .7f, .7f);

	public static final float	LABEL_SPACING					= 13;
	public static final float	GAME_MODE_SCALE					= 1.6f;

	//------ Entity Modifier Timing
	public static final float	WIN_MOVE_MOD_TIME				= .4f;
	public static final float	DISABLE_TIME					= .4f;

	public static final float	STREAM_ON_SCREEN_SECONDS		= .7f;
	public static final float	STREAM_OFF_SCREEN_SECONDS		= 1.8f;

	public static final float	COUNTDOWN_TIME					= 1.0f;
	public static final float	GAME_OVER_RESTART_DELAY			= 2.0f;

	public static final float	SCENE_TRANSITION_SECONDS		= .5f;
	public static final float	SETUP_SCENE_BUTTON_TRANSITION	= .6f;

	public static final float	BUTTON_ANIMATE_IN_TIME			= .6f;
	public static final float	BUTTON_ANIMATE_IN_START_SCALE	= 3.0f;

	public static final float	INTRO_OUT_DURATION				= .3f;

	public static final float	TOUCH_CONTROL_DURATION			= .5f;
	public static final float	TOUCH_CONTROL_RESET				= .5f;

	public static final float	INSANE_PREVIEW_DELAY			= 4.0f;
	public static final float	INSANE_PREVIEW_MOVE_DURATION	= 1.0f;

	public static final float	TILE_BASE_ANIMATE_IN			= 2.0f;
	public static final float	TILE_BASE_ALPHA					= .5f;

	public static final float	INSANE_RANDOMIZE_DELAY			= 5.0f;
	public static final float	INSANE_RANDOMIZE_DURATION		= .5f;

	public static final float	TEXT_PULSE_DURATION				= 1.0f;
	public static final float	TEXT_PULSE_START_SCALE			= 4.0f;

	public static final float	COMBO_SECONDS					= .6f;

	//---- Z-Indexing
	public static final int		BACKGROUND_Z					= 0;
	public static final int		FOREGROUND_Z					= 5;
	public static final int		BUTTON_Z						= 3;
	public static final int		GAME_OVER_Z						= FOREGROUND_Z + 1;

	//----- Tileset list
	//public static  String[]	tileset 							= { "three", "Rune" };
}
