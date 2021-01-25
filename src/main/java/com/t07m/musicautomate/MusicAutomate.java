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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Application;
import com.t07m.musicautomate.command.DumpCommand;
import com.t07m.musicautomate.command.SetCommand;
import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.file.source.MusicSource;
import com.t07m.musicautomate.music.MusicBuffer;
import com.t07m.musicautomate.music.MusicPlayer;

import lombok.Getter;
import lombok.ToString;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

@ToString(callSuper = true)
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

	private static Logger logger = LoggerFactory.getLogger(MusicAutomate.class);

	private @Getter MAConfig config;
	@ToString.Exclude
	private @Getter MusicBuffer musicBuffer;
	@ToString.Exclude
	private @Getter MusicPlayer musicPlayer;
	private @Getter MusicSource musicSource;

	public MusicAutomate(boolean gui) {
		super(gui, "Music Automate");
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
		this.getConsole().registerCommands(new SetCommand(), new DumpCommand(this));
		logger.info("Launching Application.");
		this.musicBuffer = new MusicBuffer(this);
		this.musicPlayer = new MusicPlayer(this);
		this.musicSource = MusicSource.createSource(this.config);
		this.registerService(musicBuffer);
		this.registerService(musicPlayer);
	}
}
