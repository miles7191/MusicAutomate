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
package com.t07m.musicautomate;

import java.util.logging.Level;

import com.t07m.application.Application;
import com.t07m.musicautomate.command.SetCommand;
import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.music.MusicBuffer;
import com.t07m.musicautomate.music.MusicPlayer;
import com.t07m.swing.console.ConsoleWindow;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class MusicAutomate extends Application{

	public static void main(String[] args) {
		new MusicAutomate().start();
	}
	
	private @Getter MAConfig config;
	private @Getter ConsoleWindow console;
	private @Getter MusicBuffer musicBuffer;
	private @Getter MusicPlayer musicPlayer;
	
	@SuppressWarnings("serial")
	public void init() {
		this.config = new MAConfig();
		try {
			this.config.init();
			this.config.save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.err.println("Unable to load configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
		this.console = new ConsoleWindow("Music Automate") {
			public void close() {
				stop();
			}
		};
		this.console.getLogger().setLevel(Level.parse(config.getLogger()));
		this.console.setup();
		this.console.registerCommands(new SetCommand());
		this.console.setLocationRelativeTo(null);
		this.console.setVisible(true);
		this.console.getLogger().info("Launching Application.");
		this.musicBuffer = new MusicBuffer(this);
		this.musicPlayer = new MusicPlayer(this);
		this.registerService(musicBuffer);
		this.registerService(musicPlayer);
	}
}
