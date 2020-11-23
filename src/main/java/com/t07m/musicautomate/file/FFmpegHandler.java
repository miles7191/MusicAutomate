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

import java.io.IOException;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;

public class FFmpegHandler {

	private static String ffmpegPath;
	private static FFmpeg staticFFmpeg;

	public static boolean convert(String input, String output, boolean override, String format, int channels, int sampleRate, int bitRate) {
		FFmpeg ffmpeg = getFFmpeg();
		if(ffmpeg != null) {
			try {
				FFmpegBuilder builder = new FFmpegBuilder()

						.setInput(input)
						.overrideOutputFiles(override)
						.addOutput(output)
						.setFormat(format)
						.setAudioChannels(channels)
						.setAudioSampleRate(sampleRate)
						.setAudioBitRate(bitRate)
						.done();

				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
				FFmpegJob job = executor.createJob(builder);
				job.run();
				if(job.getState() == FFmpegJob.State.FINISHED) {
					return true;
				}			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static FFmpeg getFFmpeg() {
		try {
			if(ffmpegPath != null) {
				if(staticFFmpeg == null) {
					staticFFmpeg = new FFmpeg(ffmpegPath);
				}
				if(staticFFmpeg != null && staticFFmpeg.isFFmpeg()) {
					return staticFFmpeg;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setPath(String path) {
		if(!path.equals(ffmpegPath)){
			staticFFmpeg = null;
			ffmpegPath = path;
		}
	}

}
