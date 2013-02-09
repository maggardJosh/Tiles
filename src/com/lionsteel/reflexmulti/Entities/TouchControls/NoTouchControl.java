package com.lionsteel.reflexmulti.Entities.TouchControls;

import com.lionsteel.reflexmulti.SharedResources;
import com.lionsteel.reflexmulti.BaseClasses.TouchControl;

public class NoTouchControl extends TouchControl
{
	public NoTouchControl(Runnable action, Runnable resetAction)
	{
		super(action, resetAction, SharedResources.getInstance().noRegion);
	}
}
