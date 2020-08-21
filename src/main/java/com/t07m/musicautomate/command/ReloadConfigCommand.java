/*
 * Copyright (C) 2020 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.musicautomate.command;

import com.t07m.musicautomate.MusicAutomate;
import com.t07m.musicautomate.foobar2000.Foobar2000;
import com.t07m.swing.console.Command;
import com.t07m.swing.console.ConsoleWindow;

import joptsimple.OptionSet;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class ReloadConfigCommand extends Command {

	private final MusicAutomate ma;

	public ReloadConfigCommand(MusicAutomate ma) {
		super("ReloadConfig");
		this.ma = ma;
	}


	public void process(OptionSet optionSet, ConsoleWindow console) {
		try {
			ma.getConfig().reload();
			Foobar2000.setExecutable(ma.getConfig().getFooBar2000Path());
			console.getLogger().info("Configuration Reloaded");
		} catch (InvalidConfigurationException e) {
			console.getLogger().info(e.getMessage());
			e.printStackTrace();
		}
	}
}