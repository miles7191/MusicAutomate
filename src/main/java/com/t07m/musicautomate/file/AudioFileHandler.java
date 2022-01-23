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
package com.t07m.musicautomate.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import ws.schild.jave.info.MultimediaInfo;

public class AudioFileHandler {

	public static AudioFile getAudioFile(String filePath) {
		if(Files.exists(Paths.get(filePath))) {
			MultimediaInfo format = FFprobeHandler.probe(filePath);
			if(format != null) {
				return new AudioFile(new File(filePath).getAbsolutePath(), format);
			}
		}
		return null;
	}

	public static AudioFile convertAudioFile(AudioFile source, String scratch) {
		String name = new File(source.getFilePath()).getName();
		name = name.split("\\.")[0];
		if(FFmpegHandler.convert(
				source.getFilePath(),
				scratch + File.separator + name + ".wav", 
				"pcm_s16le",
				"wav", 
				1, 
				48000, 
				16
				)) {
			return getAudioFile(scratch + File.separator + name + ".wav");
		}
		return null;
	}

}
