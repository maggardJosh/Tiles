package com.lionsteel.reflexmulti.Constants;

public class GameMode
{
	public static final int	REFLEX		= 0;
	public static final int	NON_STOP	= REFLEX + 1;
	public static final int	RACE		= NON_STOP + 1;
	public static final int	FREE_PLAY	= RACE + 1;
	public static final int	FRENZY		= FREE_PLAY + 1;
	public static final int	TIME_ATTACK	= FRENZY + 1;

	public static String getName(int gameMode)
	{
		switch (gameMode)
		{
		case REFLEX:
			return "Reflex";
		case NON_STOP:
			return "Non-Stop";
		case RACE:
			return "Race";
		case FREE_PLAY:
			return "Free_Play";
		case FRENZY:
			return "Frenzy";
		case TIME_ATTACK:
			return "Time_Attack";
		default:
			return "...None?";
		}
	}
}