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
import com.t07m.console.Console;
import com.t07m.console.NativeConsole;
import com.t07m.musicautomate.command.SetCommand;
import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.file.source.MusicSource;
import com.t07m.musicautomate.music.MusicBuffer;
import com.t07m.musicautomate.music.MusicPlayer;
import com.t07m.swing.console.ConsoleWindow;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class MusicAutomate extends Application{

	public static void main(String[] args) {
		boolean gui = true;
		if(args.length > 0) {
			for(String arg : args) {
				if(arg.equalsIgnoreCase("-nogui")) {
					gui = false;
				}
			}
		}
		new MusicAutomate(gui).start();
	}

	private final boolean gui;

	private @Getter MAConfig config;
	private @Getter Console console;
	private @Getter MusicBuffer musicBuffer;
	private @Getter MusicPlayer musicPlayer;
	private @Getter MusicSource musicSource;

	public MusicAutomate(boolean gui) {
		this.gui = gui;
	}

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
		if(this.gui) {
			ConsoleWindow cw = new ConsoleWindow("Music Automate") {
				public void close() {
					stop();
				}
			};
			cw.setup();
			cw.setLocationRelativeTo(null);
			cw.setVisible(true);
		}else {
			this.console = new NativeConsole("MusicAutomate") {
				public void close() {
					stop();
				}
			};
			this.console.setup();
		}
		this.console.getLogger().setLevel(Level.parse(config.getLogger()));
		this.console.suppressSystemErr();
		this.console.suppressSystemOut();
		this.console.registerCommands(new SetCommand());
		this.console.getLogger().info("Launching Application.");
		this.musicBuffer = new MusicBuffer(this);
		this.musicPlayer = new MusicPlayer(this);
		this.musicSource = MusicSource.createSource(this.config);
		this.registerService(musicBuffer);
		this.registerService(musicPlayer);
	}
}
