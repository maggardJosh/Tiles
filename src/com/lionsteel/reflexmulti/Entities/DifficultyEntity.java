package com.lionsteel.reflexmulti.Entities;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.AlphaModifier;

import com.lionsteel.reflexmulti.ReflexConstants;
import com.lionsteel.reflexmulti.SetupScene.Difficulty;

public class DifficultyEntity extends Entity implements ReflexConstants
{
	private float		alphaValues[]	= new float[NUM_BUTTONS];
	private final float	buttonScale		= .3f;
	final GameButton[]	difficultyDisplayButtons;

	public DifficultyEntity(final int difficulty)
	{
		difficultyDisplayButtons = new GameButton[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			difficultyDisplayButtons[i] = new GameButton(i + 1, null, -1);
			difficultyDisplayButtons[i].buttonSprite.setScale(buttonScale);
			if (i < 3 || (i < 5 && difficulty > Difficulty.EASY) || (difficulty > Difficulty.NORMAL))
				alphaValues[i] = 1.0f;
			else
				alphaValues[i] = .2f;
			difficultyDisplayButtons[i].buttonSprite.setAlpha(0);
		}

		final float buttonWidth = BUTTON_WIDTH * buttonScale;
		final int XPos = 170;
		final int YPos = -15;
		//Easy Buttons
		for (int x = 0; x < 3; x++)
		{
			difficultyDisplayButtons[x].buttonSprite.setPosition(XPos + (x % 3) * buttonWidth, YPos + buttonWidth);
		}

		//Medium Buttons
		{
			difficultyDisplayButtons[3].buttonSprite.setPosition(XPos + buttonWidth, YPos);
			difficultyDisplayButtons[4].buttonSprite.setPosition(XPos + buttonWidth, YPos + buttonWidth * 2);
		}

		//Hard Buttons
		{

			difficultyDisplayButtons[5].buttonSprite.setPosition(XPos, YPos);
			difficultyDisplayButtons[6].buttonSprite.setPosition(XPos + buttonWidth * 2, YPos);
			difficultyDisplayButtons[7].buttonSprite.setPosition(XPos, YPos + buttonWidth * 2);
			difficultyDisplayButtons[8].buttonSprite.setPosition(XPos + buttonWidth * 2, YPos + buttonWidth * 2);

		}
		for (int x = 0; x < NUM_BUTTONS; x++)
			this.attachChild(difficultyDisplayButtons[x].buttonSprite);
	}

	public void fadeIn()
	{
		for (int x = 0; x < NUM_BUTTONS; x++)
			difficultyDisplayButtons[x].buttonSprite.registerEntityModifier(new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, 0, alphaValues[x]));
	}

	public void fadeOut()
	{
		for (int x = 0; x < NUM_BUTTONS; x++)
			difficultyDisplayButtons[x].buttonSprite.registerEntityModifier(new AlphaModifier(SETUP_SCENE_BUTTON_TRANSITION, alphaValues[x], 0));
	}
}
