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
package com.t07m.musicautomate.file.source;

import java.io.File;

import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.config.MAConfig.SourceConfig;

import lombok.Getter;

public abstract class MusicSource {

	private static @Getter String[] FileFormats = {"mp3", "m4a", "flac", "ogg", "mp4", "wav", "wma", "aac"};

	public abstract boolean exists();

	public abstract boolean canRead();

	public abstract String[] listFiles();

	public abstract File getFile(String name);

	public void complete(File file) {};

	public static MusicSource createSource(MAConfig maConfig) {
		SourceConfig config = maConfig.getMusicSource();
		switch(config.getType().toLowerCase()) {
		case "local":
			return new LocalSource(new File(config.getPath()));
		case "smb":
			return new SMBSource(config.getDomain(), config.getUsername(), config.getPassword(), config.getAddress(), config.getPath(), maConfig.getScratchPath());
		}
		return null;
	}
}
