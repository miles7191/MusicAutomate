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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

public class FFmpegHandler {

	private static Encoder staticEncoder;

	public static boolean convert(String input, String output, String codec, String format, int channels, int sampleRate, int bitRate) {
		File source = new File(input);
		File target = new File(output);
		if(source.exists()) {
			Encoder encoder = getEncoder();
			try {
				synchronized(encoder) {
					AudioAttributes audio = new AudioAttributes();
					audio.setCodec(codec);
					audio.setBitRate(bitRate);
					audio.setChannels(channels);
					audio.setSamplingRate(sampleRate);

					EncodingAttributes attributes = new EncodingAttributes();
					attributes.setOutputFormat(format);
					attributes.setAudioAttributes(audio);

					encoder.encode(new MultimediaObject(source), target, attributes);
					return true;
				}
			}catch (Exception e) {
				try {
					Files.deleteIfExists(Paths.get(output));
				} catch (IOException e1) {}
				for(String s : encoder.getUnhandledMessages()) {
					System.err.println(s);
				}
				e.printStackTrace();
			}
		}
		return false;
	}

	private static Encoder getEncoder() {
		if(staticEncoder == null) {
			staticEncoder = new Encoder();
		}
		return staticEncoder;
	}
}
