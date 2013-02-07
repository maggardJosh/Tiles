package com.lionsteel.reflexmulti;

public class ReadyTouchControl extends TouchControl
{
	public ReadyTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action,resetAction, SharedResources.getInstance().readyRegion);
	}
}
