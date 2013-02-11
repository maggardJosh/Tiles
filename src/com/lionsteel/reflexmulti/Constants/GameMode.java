package com.lionsteel.reflexmulti;

public class GameMode
{
	public static final int	REFLEX		= 0;
	public static final int	NON_STOP	= REFLEX + 1;
	public static final int	RACE		= NON_STOP + 1;

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
		default:
			return "...None?";
		}
	}
}