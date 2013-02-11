package com.lionsteel.reflexmulti;

public interface ReflexConstants
{
	public static final int			CAMERA_WIDTH					= 480;
	public static final int			CAMERA_HEIGHT					= 800;

	public static final int			NUM_BUTTONS						= 9;

	public static final int			BUTTON_WIDTH					= 110;
	public static final int			BAR_WIDTH						= 30;
	public static final int			BAR_SPEED						= 20;

	public static final int			DISPLAY_BUTTONS					= -1;
	public static final int			PLAYER_ONE						= 0;
	public static final int			PLAYER_TWO						= 1;

	public static final int			BACK_ARROW_PADDING				= 10;
	public static final int			REMATCH_TOUCH_PADDING			= 200;

	public static final float		REFLEX_MIN_TIME					= 1.0f;
	public static final float		REFLEX_MAX_TIME					= 2.5f;

	//------ Entity Modifier Timing
	public static final float		WIN_MOVE_MOD_TIME				= .4f;
	public static final float		DISABLE_TIME					= .4f;

	public static final float		STREAM_ON_SCREEN_SECONDS		= .7f;
	public static final float		STREAM_OFF_SCREEN_SECONDS		= 1.8f;

	public static final float		COUNTDOWN_TIME					= 1.0f;
	public static final float		GAME_OVER_RESTART_DELAY			= 2.0f;

	public static final float		SCENE_TRANSITION_SECONDS		= .5f;
	public static final float		SETUP_SCENE_BUTTON_TRANSITION	= .6f;

	public static final float		BUTTON_ANIMATE_IN_TIME			= .6f;
	public static final float		BUTTON_ANIMATE_IN_START_SCALE	= 3.0f;

	public static final float		INTRO_OUT_DURATION				= .3f;

	public static final float		TOUCH_CONTROL_DURATION			= .5f;
	public static final float		TOUCH_CONTROL_RESET				= .5f;

	public static final float		INSANE_PREVIEW_DELAY			= 4.0f;
	public static final float		INSANE_PREVIEW_MOVE_DURATION	= 1.0f;

	public static final float		TILE_BASE_ANIMATE_IN			= 2.0f;
	public static final float		TILE_BASE_ALPHA					= .5f;

	//---- Z-Indexing
	public static final int			BACKGROUND_Z					= 0;
	public static final int			FOREGROUND_Z					= 5;
	public static final int			BUTTON_Z						= 3;
	public static final int			GAME_OVER_Z						= FOREGROUND_Z + 1;

	//----- Tileset list
	public static final String[]	tileset							= { "three", "Rune" };
}
