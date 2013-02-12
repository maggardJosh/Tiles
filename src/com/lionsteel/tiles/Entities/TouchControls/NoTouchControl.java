package com.lionsteel.tiles.Entities.TouchControls;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.TouchControl;

public class NoTouchControl extends TouchControl
{
	public NoTouchControl(Runnable action, Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().noRegion);
	}
}
