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

import lombok.Getter;
import lombok.ToString;
import ws.schild.jave.info.MultimediaInfo;

@ToString
public class AudioFile {

	@ToString.Exclude
	private final MultimediaInfo info;
	private final @Getter String filePath;
	
	AudioFile(String filePath, MultimediaInfo info){
		this.filePath = filePath;
		this.info = info;
	}
	
	@ToString.Include
	public long getDuration() {
		return info.getDuration();
	}
	
	@ToString.Include
	public String getFormat() {
		return info.getFormat();
	}
	
	@ToString.Include
	public int getBitRate() {
		return info.getAudio().getBitRate();
	}
	
	@ToString.Include
	public int getChannels() {
		return info.getAudio().getChannels();
	}
	
	@ToString.Include
	public String getDecoder() {
		return info.getAudio().getDecoder();
	}
	
	@ToString.Include
	public int getSamplingRate() {
		return info.getAudio().getSamplingRate();
	}
	
}
