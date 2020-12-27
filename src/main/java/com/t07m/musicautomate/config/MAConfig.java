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
package com.t07m.musicautomate.config;

import java.io.File;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.YamlConfig;

@ToString
public class MAConfig extends YamlConfig{
	
	@Comment("Required External Program paths")
	private @Getter @Setter String ffmpegPath = "lib/ffmpeg.exe";
	private @Getter @Setter String ffprobePath = "lib/ffprobe.exe";
	
	private @Getter @Setter SourceConfig musicSource = new SourceConfig();
	private @Getter @Setter String scratchPath = System.getenv("TEMP");
	private @Getter @Setter int musicBuffer = 2;
	
	@Comment("Fades between music. Available types are Linear & Expoential.")
	private @Getter @Setter FadeConfig fadeOut = new FadeConfig();
	private @Getter @Setter FadeConfig fadeIn = new FadeConfig();
	
	public MAConfig() {
		CONFIG_HEADER = new String[]{"MusicAutomate Configuration Data"};
		CONFIG_FILE = new File("config.yml");
	}
	
	@ToString
	public class SourceConfig extends YamlConfig{
		
		private @Getter @Setter String type = "Local";
		private @Getter @Setter String path = System.getProperty("user.home")+"\\Music";
		private @Getter @Setter String address;
		private @Getter @Setter String domain;
		private @Getter @Setter String username;
		@ToString.Exclude
		private @Getter @Setter String password;
		
	}
	
	@ToString
	@NoArgsConstructor
	public class FadeConfig extends YamlConfig{
		
		private @Getter @Setter String type = "Exponential";
		private @Getter @Setter double time = 5.0;
		
	}
	
}
