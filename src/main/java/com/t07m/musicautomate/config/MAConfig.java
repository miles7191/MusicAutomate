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
import lombok.Setter;
import net.cubespace.Yamler.Config.YamlConfig;

public class MAConfig extends YamlConfig{

	private @Getter @Setter String ffmpegPath = "lib/ffmpeg.exe";
	private @Getter @Setter String ffprobePath = "lib/ffprobe.exe";
	private @Getter @Setter String sourcePath = "";
	private @Getter @Setter String scratchPath = System.getenv("TEMP");
	private @Getter @Setter double transitionTime = 5.0;
	private @Getter @Setter int musicBuffer = 5;
	
	public MAConfig() {
		CONFIG_HEADER = new String[]{"MusicAutomate Configuration Data"};
		CONFIG_FILE = new File("config.yml");
	}
	
}
