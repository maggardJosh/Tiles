package com.lionsteel.tiles.Entities.TouchControls;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.TouchControl;

public class YesTouchControl extends TouchControl
{
	public YesTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().yesRegion);
	}
}
