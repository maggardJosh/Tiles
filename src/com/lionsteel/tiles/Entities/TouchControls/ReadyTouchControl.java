package com.lionsteel.tiles.Entities.TouchControls;

import com.lionsteel.tiles.SharedResources;
import com.lionsteel.tiles.BaseClasses.TouchControl;

public class ReadyTouchControl extends TouchControl
{
	public ReadyTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action,resetAction, SharedResources.getInstance().readyRegion);
	}
}
