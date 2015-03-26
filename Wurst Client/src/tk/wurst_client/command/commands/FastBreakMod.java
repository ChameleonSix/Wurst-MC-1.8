/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.command.commands;

import tk.wurst_client.Client;
import tk.wurst_client.command.Command;
import tk.wurst_client.command.Command.Info;

@Info(help = "Changes the settings of FastBreak.",
	name = "fastbreak",
	syntax = {"mode (normal|instant)"})
public class FastBreakMod extends Command
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 2)
			syntaxError();
		if(args[0].toLowerCase().equals("mode"))
		{// 0=normal, 1=instant
			if(args[1].toLowerCase().equals("normal"))
				Client.wurst.options.fastbreakMode = 0;
			else if(args[1].toLowerCase().equals("instant"))
				Client.wurst.options.fastbreakMode = 1;
			else
				syntaxError();
			Client.wurst.fileManager.saveOptions();
			Client.wurst.chat.message("FastBreak mode set to \"" + args[1]
				+ "\".");
		}else
			syntaxError();
	}
}
