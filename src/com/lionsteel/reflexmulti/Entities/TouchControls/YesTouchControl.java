package com.lionsteel.reflexmulti.Entities.TouchControls;

import com.lionsteel.reflexmulti.SharedResources;
import com.lionsteel.reflexmulti.BaseClasses.TouchControl;

public class YesTouchControl extends TouchControl
{
	public YesTouchControl(final Runnable action, final Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().yesRegion);
	}
}
