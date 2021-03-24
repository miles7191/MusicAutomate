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

import com.github.zafarkhaja.semver.Version;
import com.t07m.application.Application;
import com.t07m.console.remote.server.RemoteServer;
import com.t07m.musicautomate.command.DumpCommand;
import com.t07m.musicautomate.command.SetCommand;
import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.file.source.MusicSource;
import com.t07m.musicautomate.music.MusicBuffer;
import com.t07m.musicautomate.music.MusicPlayer;
import com.t07m.musicautomate.system.monitors.SkippedFrameMonitor;

import lombok.Getter;
import lombok.ToString;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

@ToString(callSuper = true)
public class MusicAutomate extends Application{

	public static final Version VERSION = Version.valueOf("1.1.0");
	public static final String GITHUB_REPO = "miles7191/MusicAutomate";
	
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
	
	private @Getter SkippedFrameMonitor skippedFrameMonitor;

	private RemoteServer remoteConsole;
	
	public MusicAutomate(boolean gui) {
		super(gui, "Music Automate");
	}

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
		logger.info("Launching Application.");
		if(this.config.isAutoUpdate()) {
			this.initAutoUpdater(
					GITHUB_REPO,
					VERSION, 
					config.getStartupScript(),
					config.isUsePrereleases(),
					config.getCronSchedule());
		}
		remoteConsole = new RemoteServer(this.getConsole(), 13560);
		remoteConsole.init();
		remoteConsole.bind();
		this.musicBuffer = new MusicBuffer(this);
		this.musicPlayer = new MusicPlayer(this);
		this.musicSource = MusicSource.createSource(this.config);
		this.skippedFrameMonitor = new SkippedFrameMonitor(this);
		this.getConsole().registerCommands(new SetCommand(), new DumpCommand(this));
		this.registerService(musicBuffer);
		this.registerService(musicPlayer);
		this.registerService(skippedFrameMonitor);
	}
	
	public void cleanup() {
		remoteConsole.unbind();
	}
}
