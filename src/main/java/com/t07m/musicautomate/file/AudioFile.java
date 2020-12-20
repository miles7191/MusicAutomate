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

import java.util.Map;

import lombok.ToString;
import net.bramp.ffmpeg.probe.FFmpegFormat;

@ToString
public class AudioFile {

	private final FFmpegFormat format;
	
	AudioFile(FFmpegFormat format){
		this.format = format;
	}
	
	public String filename() {
		return format.filename;
	}
	
	public int nb_streams() {
		return format.nb_streams;
	}
	
	public int nb_programs() {
		return format.nb_programs;
	}
	
	public String format_name() {
		return format.format_name;
	}
	
	public String format_long_name() {
		return format.format_long_name;
	}
	
	public double start_time() {
		return format.start_time;
	}
	
	public double duration() {
		return format.duration;
	}
	
	public long size() {
		return format.size;
	}
	
	public long bit_rate() {
		return format.bit_rate;
	}
	
	public int probe_score() {
		return format.probe_score;
	}
	
	public Map<String, String> tags(){
		return format.tags;
	}
	
}
