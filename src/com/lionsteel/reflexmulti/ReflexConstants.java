package com.lionsteel.reflexmulti;

public interface ReflexConstants
{
	public static final int		CAMERA_WIDTH				= 480;
	public static final int		CAMERA_HEIGHT				= 800;
	
	public static final int		BUTTON_WIDTH				= 110;
	public static final int		BAR_WIDTH					= 30;
	public static final int		BAR_SPEED					= 35;
	
	public static final int		DISPLAY_BUTTONS				= -1;
	public static final int		PLAYER_ONE					= 0;
	public static final int		PLAYER_TWO					= 1;
	
	//------ Entity Modifier Timing
	public static final float	WIN_MOVE_MOD_TIME			= .4f;
	public static final float	DISABLE_TIME				= .6f;
	
	public static final float	STREAM_ON_SCREEN_SECONDS	= .7f;
	public static final float	STREAM_OFF_SCREEN_SECONDS	= 1.8f;
	
	public static final float	COUNTDOWN_TIME				= 1.0f;
	public static final float	GAME_OVER_RESTART_DELAY		= 2.0f;
	
	public static final float	SCENE_TRANSITION_SECONDS	= .7f;
	
	//---- Z-Indexing
	public static final int		BACKGROUND_Z				= 0;
	public static final int		FOREGROUND_Z				= 5;
	public static final int		BUTTON_Z					= 3;
	public static final int		GAME_OVER_Z					= FOREGROUND_Z + 1;
}
