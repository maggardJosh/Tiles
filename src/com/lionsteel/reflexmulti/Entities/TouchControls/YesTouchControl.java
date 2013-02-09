package com.lionsteel.reflexmulti.Entities.TouchControls;

import com.lionsteel.reflexmulti.SharedResources;

public class YesTouchControl extends TouchControl
{
	public YesTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().yesRegion);
	}
}
