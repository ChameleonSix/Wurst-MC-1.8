/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import tk.wurst_client.settings.SliderSetting;
import tk.wurst_client.settings.SliderSetting.ValueDisplay;

@Mod.Info(description = "Makes you jump much higher.",
	name = "HighJump",
	tags = "high jump",
	help = "Mods/HighJump")
@Mod.Bypasses
public class HighJumpMod extends Mod
{
	public int jumpHeight = 6;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Height", jumpHeight, 1, 100, 1,
			ValueDisplay.INTEGER)
		{
			@Override
			public void update()
			{
				jumpHeight = (int)getValue();
			}
		});
	}
}
