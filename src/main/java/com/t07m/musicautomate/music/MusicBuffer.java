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
package com.t07m.musicautomate.music;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.musicautomate.MusicAutomate;
import com.t07m.musicautomate.file.AudioFile;
import com.t07m.musicautomate.file.AudioFileHandler;
import com.t07m.musicautomate.file.FFmpegHandler;
import com.t07m.musicautomate.file.FFprobeHandler;
import com.t07m.musicautomate.file.source.MusicSource;

import kuusisto.tinysound.TinySound;
import kuusisto.tinysound.internal.MemMusic;
import lombok.ToString;

@ToString(callSuper = true)
public class MusicBuffer extends Service<MusicAutomate>{

	private static Logger logger = LoggerFactory.getLogger(MusicBuffer.class);
	
	@ToString.Exclude
	private Random random = new Random();
	private int count = 0;
	private ArrayList<AutoMusic> buffer;

	public MusicBuffer(MusicAutomate app) {
		super(app, TimeUnit.SECONDS.toMillis(1));
	}

	public void init() {
		buffer = new ArrayList<AutoMusic>(getBufferSize());
		TinySound.init();
		FFprobeHandler.setPath(getApp().getConfig().getFfprobePath());
		FFmpegHandler.setPath(getApp().getConfig().getFfmpegPath());
	}

	public void process() {
		if(count < getBufferSize()) {
			MusicSource source = getApp().getMusicSource();
			if(source.exists() && source.canRead()) {
				File file = getRandomFile(source);
				if(file != null) {
					AudioFile audioFile = AudioFileHandler.getAudioFile(file.getAbsolutePath());
					if(audioFile != null) {
						boolean scratched = false;
						if(!audioFile.format_name().equalsIgnoreCase("wav")) {
							logger.debug("Unable to play AudioFile format: " + new File(audioFile.filename()).getName());
							File scratchPath = new File(getApp().getConfig().getScratchPath());
							if(scratchPath.exists() && scratchPath.canRead() && scratchPath.canWrite()) {
								logger.debug("Converting " + new File(audioFile.filename()).getName() + " to wav.");
								audioFile = AudioFileHandler.convertAudioFile(audioFile, scratchPath.getAbsolutePath());
								scratched = true;
							}else {
								logger.error("Unable to utilize scratch directory!");
							}
							if(audioFile != null) {
								MemMusic music = (MemMusic) TinySound.loadMusic(new File(audioFile.filename()));
								if(scratched) {
									try {
										Files.deleteIfExists(Paths.get(audioFile.filename()));
									} catch (IOException e) {
										logger.debug("Unable to delete scratch file: " + new File(audioFile.filename()).getName());
									}
								}
								if(music != null) {
									AutoMusic am = new AutoMusic(music, audioFile);
									buffer.add(am);
									count++;
									logger.debug("Loaded music: " + new File(audioFile.filename()).getName());
								}else {
									logger.warn("Failed to load music: " + new File(audioFile.filename()).getName());
									if(!TinySound.isInitialized()) {
										logger.debug("Found TinySound not initialized. Attempting to initialize TinySound.");
										TinySound.init();
									}
								}
							}
						}
					}else {
						logger.debug("Unable to create AudioFile from randomly selected file.");
					}
					source.complete(file);
				}else {
					logger.error("Unable to get files from music source!");
				}
			}else {
				logger.error("Unable to read source!");
			}
		}
	}

	private File getRandomFile(MusicSource source) {
		String[] files = source.listFiles();
		if(files.length > 0) {
			var temp = files[random.nextInt(files.length)];
			return source.getFile(temp);
		}
		return null;
	}

	public AutoMusic getNext() {
		if(buffer.size() > 0) {
			count--;
			return buffer.remove(0);
		}
		return null;
	}

	private int getBufferSize() {
		return Math.max(1, getApp().getConfig().getMusicBuffer());
	}

	public void cleanup() {
		for(AutoMusic music : buffer) {
			music.getTinyMusic().unload();
		}
		TinySound.shutdown();
	}

}
