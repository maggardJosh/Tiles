package com.lionsteel.reflexmulti.Entities;

import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.lionsteel.reflexmulti.Constants.Difficulty;
import com.lionsteel.reflexmulti.Constants.ReflexConstants;

public class DifficultyEntity extends Entity implements ReflexConstants
{
	private float		alphaValues[]	= new float[NUM_BUTTONS];
	private final float	buttonScale		= .3f;
	final GameButton[]	difficultyDisplayButtons;
	
	public DifficultyEntity(final int difficulty, final Tileset tileset)
	{
		difficultyDisplayButtons = new GameButton[NUM_BUTTONS];
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			difficultyDisplayButtons[i] = new GameButton(i, tileset, null, -1);
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
		if (difficulty == Difficulty.INSANE)
		{
			difficultyDisplayButtons[0].buttonSprite.registerEntityModifier(new DelayModifier(INSANE_PREVIEW_DELAY)
			{
				@Override
				protected void onModifierFinished(IEntity pItem)
				{
					insaneRandomize();
					super.onModifierFinished(pItem);
				}
			});
		}
	}
	
	final Random	rand	= new Random();
	
	private void insaneRandomize()
	{
		final float SHAKE_ANGLE = 10.0f;
		final float SHAKE_DURATION = 2.0f;
		for (int i = 0; i < NUM_BUTTONS - 1; i++)
			difficultyDisplayButtons[i].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(SHAKE_DURATION / 12, 0, -SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, SHAKE_ANGLE, 0)), 4));
		difficultyDisplayButtons[NUM_BUTTONS - 1].buttonSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(SHAKE_DURATION / 12, 0, -SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, -SHAKE_ANGLE, SHAKE_ANGLE), new RotationModifier(SHAKE_DURATION / 12, SHAKE_ANGLE, 0)), 4)
		{
			@Override
			protected void onModifierFinished(IEntity pItem)
			{
				int[] buttons = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
				final int NUM_SWITCHES = 12;
				for (int i = 0; i < NUM_SWITCHES; i++)
				{
					int firstTile = rand.nextInt(NUM_BUTTONS);
					int secondTile = rand.nextInt(NUM_BUTTONS);
					while (secondTile == firstTile)
						secondTile = rand.nextInt(NUM_BUTTONS);
					int temp = buttons[firstTile];
					buttons[firstTile] = buttons[secondTile];
					buttons[secondTile] = temp;
				}
				for (int x = 0; x < NUM_BUTTONS; x++)
				{
					if (x != buttons[x])
					{
						animateMove(difficultyDisplayButtons[x], difficultyDisplayButtons[buttons[x]]);
						difficultyDisplayButtons[x].buttonSprite.setZIndex(FOREGROUND_Z + 1);
					} else
					{
						difficultyDisplayButtons[x].buttonSprite.setZIndex(FOREGROUND_Z);
					}
					if (x == NUM_BUTTONS - 1)
						difficultyDisplayButtons[x].buttonSprite.registerEntityModifier(new DelayModifier(INSANE_PREVIEW_MOVE_DURATION)
						{
							protected void onModifierFinished(IEntity pItem)
							{
								difficultyDisplayButtons[0].buttonSprite.registerEntityModifier(new DelayModifier(INSANE_PREVIEW_DELAY)
								{
									@Override
									protected void onModifierFinished(
											IEntity pItem)
									{
										insaneRandomize();
										super.onModifierFinished(pItem);
									}
								});
							};
						});
				}
				sortChildren();
				super.onModifierFinished(pItem);
			}
		});
	}
	
	private void animateMove(GameButton buttonToMove, GameButton buttonToMoveTo)
	{
		buttonToMove.buttonSprite.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(INSANE_PREVIEW_MOVE_DURATION / 2, buttonScale, buttonScale * 2.0f, EaseCubicOut.getInstance()), new ScaleModifier(INSANE_PREVIEW_MOVE_DURATION / 2, buttonScale * 2.0f, buttonScale, EaseCubicIn.getInstance())));
		buttonToMove.buttonSprite.registerEntityModifier(new MoveModifier(INSANE_PREVIEW_MOVE_DURATION, buttonToMove.buttonSprite.getX(), buttonToMoveTo.buttonSprite.getX(), buttonToMove.buttonSprite.getY(), buttonToMoveTo.buttonSprite.getY()));
		
	}
	
	public void clear()
	{
		this.detachChildren();
		for (GameButton b : difficultyDisplayButtons)
			b.clear();
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
