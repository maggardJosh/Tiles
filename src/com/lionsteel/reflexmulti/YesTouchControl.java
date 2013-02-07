package com.lionsteel.reflexmulti;

public class YesTouchControl extends TouchControl
{
	public YesTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().yesRegion);
	}
}
