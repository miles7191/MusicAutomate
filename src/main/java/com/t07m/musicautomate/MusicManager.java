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
package com.t07m.musicautomate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Level;

import com.t07m.application.Service;
import com.t07m.musicautomate.file.AudioFile;
import com.t07m.musicautomate.file.AudioFileHandler;
import com.t07m.musicautomate.file.FFmpegHandler;
import com.t07m.musicautomate.file.FFprobeHandler;

import kuusisto.tinysound.TinySound;
import kuusisto.tinysound.internal.MemMusic;

public class MusicManager extends Service<MusicAutomate>{

	private Random r = new Random();
	private MemMusic current, next;
	private AudioFile currentFile, nextFile;

	public MusicManager(MusicAutomate app) {
		super(app, 100);
	}

	public void init() {
		TinySound.init();
		FFprobeHandler.setPath(app.getConfig().getFfprobePath());
		FFmpegHandler.setPath(app.getConfig().getFfmpegPath());
	}

	public void process() {
		File source = new File(app.getConfig().getSourcePath());
		File scratch = new File(app.getConfig().getScratchPath());
		if(source.exists() && source.canRead() && scratch.exists() && scratch.canWrite() && scratch.canRead()) {
			if(current == null) {
				AudioFile af = getRandomAudioFile(source);
				if(af != null) {
					boolean scratched = false;
					if(!af.format_name().equalsIgnoreCase("wav")) {
						af = AudioFileHandler.convertAudioFile(af, scratch.getAbsolutePath());
						scratched = true;
					}
					if(af != null) {
						current = (MemMusic) TinySound.loadMusic(new File(af.filename()));
						if(current != null) {
							currentFile = af;
							app.getConsole().getLogger().log(Level.FINE	, "Loaded: " + new File(af.filename()).getName());
						}
						if(scratched)
							try {
								Files.deleteIfExists(Paths.get(af.filename()));
							} catch (IOException e) {}
					}
				}
			}else if(next == null) {
				AudioFile af = getRandomAudioFile(source);
				if(af != null) {
					boolean scratched = false;
					if(!af.format_name().equalsIgnoreCase("wav")) {
						af = AudioFileHandler.convertAudioFile(af, scratch.getAbsolutePath());
						scratched = true;
					}
					if(af != null) {
						next = (MemMusic) TinySound.loadMusic(new File(af.filename()));
						if(next != null) {
							nextFile = af;
							app.getConsole().getLogger().log(Level.FINE	, "Loaded: " + new File(af.filename()).getName());
						}
						if(scratched)
							try {
								Files.deleteIfExists(Paths.get(af.filename()));
							} catch (IOException e) {}
					}
				}
			}
			double tTime = app.getConfig().getTransitionTime();
			if(current != null) {
				if(current.playing()) {
					double durationLeft = (current.getDuration() - current.getCurrentPosition())/ 1000.0;
					if(durationLeft < tTime) {
						if(!next.playing()) {
							next.play(false, 0);
						}
						current.setVolume(durationLeft/tTime);
						next.setVolume(1 - current.getVolume());
					}
				}else if(!current.done()) {
					current.play(false);
					app.getConsole().getLogger().log(Level.INFO	, "Now Playing: " + new File(currentFile.filename()).getName());;
				}else {
					current.unload();
					app.getConsole().getLogger().log(Level.FINE	, "Unloaded: " + new File(currentFile.filename()).getName());
					current = next;
					currentFile = nextFile;
					next = null;
					nextFile = null;
					app.getConsole().getLogger().log(Level.INFO	, "Now Playing: " + new File(currentFile.filename()).getName());
				}
			}
		}
	}

	private AudioFile getRandomAudioFile(File source) {
		File[] files = source.listFiles();
		int attempt = 0;
		while(attempt < 5) {
			var temp = files[r.nextInt(files.length)];
			if(!temp.isDirectory()) {
				var af = AudioFileHandler.getAudioFile(temp.getAbsolutePath());
				if(af != null)
					return af;	
			}
			attempt++;
		}
		return null;
	}

	public void cleanup() {
		TinySound.shutdown();
	}
}
