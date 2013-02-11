package com.lionsteel.reflexmulti;

public class Difficulty
{
	public static final int	EASY	= 0;
	public static final int	NORMAL	= EASY + 1;
	public static final int	HARD	= NORMAL + 1;
	public static final int	INSANE	= HARD + 1;

	public static String getName(int difficulty)
	{
		switch (difficulty)
		{
		case EASY:
			return "Easy";
		case NORMAL:
			return "Normal";
		case HARD:
			return "Hard";
		case INSANE:
			return "Insane";
		default:
			return "..None?";
		}
	}
}