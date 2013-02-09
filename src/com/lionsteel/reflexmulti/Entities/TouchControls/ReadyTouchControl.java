package com.lionsteel.reflexmulti.Entities.TouchControls;

import com.lionsteel.reflexmulti.SharedResources;

public class ReadyTouchControl extends TouchControl
{
	public ReadyTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action,resetAction, SharedResources.getInstance().readyRegion);
	}
}
