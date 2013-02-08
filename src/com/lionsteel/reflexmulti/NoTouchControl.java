package com.lionsteel.reflexmulti;

public class NoTouchControl extends TouchControl
{
	public NoTouchControl(Runnable action, Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().noRegion);
	}
}
