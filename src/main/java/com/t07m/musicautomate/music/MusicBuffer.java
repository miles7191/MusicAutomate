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
import java.util.logging.Level;

import com.t07m.application.Service;
import com.t07m.musicautomate.MusicAutomate;
import com.t07m.musicautomate.file.AudioFile;
import com.t07m.musicautomate.file.AudioFileHandler;
import com.t07m.musicautomate.file.FFmpegHandler;
import com.t07m.musicautomate.file.FFprobeHandler;
import com.t07m.musicautomate.file.source.MusicSource;

import kuusisto.tinysound.TinySound;
import kuusisto.tinysound.internal.MemMusic;

public class MusicBuffer extends Service<MusicAutomate>{

	private Random random = new Random();
	private int count = 0;
	private ArrayList<AutoMusic> buffer;

	public MusicBuffer(MusicAutomate app) {
		super(app, TimeUnit.SECONDS.toMillis(1));
	}

	public void init() {
		buffer = new ArrayList<AutoMusic>(getBufferSize());
		TinySound.init();
		FFprobeHandler.setPath(app.getConfig().getFfprobePath());
		FFmpegHandler.setPath(app.getConfig().getFfmpegPath());
	}

	public void process() {
		if(count < getBufferSize()) {
			MusicSource source = app.getMusicSource();
			if(source.exists() && source.canRead()) {
				File file = getRandomFile(source);
				if(file != null) {
					AudioFile audioFile = AudioFileHandler.getAudioFile(file.getAbsolutePath());
					if(audioFile != null) {
						boolean scratched = false;
						if(!audioFile.format_name().equalsIgnoreCase("wav")) {
							app.getConsole().getLogger().log(Level.FINEST, "Unable to play AudioFile format: " + new File(audioFile.filename()).getName());
							File scratch = new File(app.getConfig().getScratchPath());
							scratch.deleteOnExit();
							if(scratch.exists() && scratch.canRead() && scratch.canWrite()) {
								app.getConsole().getLogger().log(Level.FINER, "Converting " + new File(audioFile.filename()).getName() + " to wav.");
								audioFile = AudioFileHandler.convertAudioFile(audioFile, scratch.getAbsolutePath());
								scratched = true;
							}else {
								app.getConsole().getLogger().log(Level.SEVERE, "Unable to utilize scratch directory!");
							}
							if(audioFile != null) {
								MemMusic music = (MemMusic) TinySound.loadMusic(new File(audioFile.filename()));
								if(scratched) {
									try {
										Files.deleteIfExists(Paths.get(audioFile.filename()));
									} catch (IOException e) {
										app.getConsole().getLogger().log(Level.FINE, "Unable to delete scratch file: " + new File(audioFile.filename()).getName());
									}
								}
								if(music != null) {
									AutoMusic am = new AutoMusic(music, audioFile);
									buffer.add(am);
									count++;
									app.getConsole().getLogger().log(Level.FINE, "Loaded music: " + new File(audioFile.filename()).getName());
								}else {
									app.getConsole().getLogger().log(Level.WARNING	, "Failed to load music: " + new File(audioFile.filename()).getName());
								}
							}
						}
					}else {
						app.getConsole().getLogger().log(Level.FINER, "Unable to create AudioFile from randomly selected file.");
					}
					source.complete(file);
				}else {
					app.getConsole().getLogger().log(Level.WARNING, "Unable to get files from music source!");
				}
			}else {
				app.getConsole().getLogger().log(Level.WARNING, "Unable to read source!");
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
		return Math.max(1, app.getConfig().getMusicBuffer());
	}

	public void cleanup() {
		for(AutoMusic music : buffer) {
			music.getTinyMusic().unload();
		}
		TinySound.shutdown();
	}

}
