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
import java.util.Random;
import java.util.logging.Level;

import com.t07m.application.Service;
import com.t07m.musicautomate.file.AudioFile;
import com.t07m.musicautomate.file.AudioFileHandler;
import com.t07m.musicautomate.music.AutoMusic;

import kuusisto.tinysound.TinySound;

public class MusicPlayer extends Service<MusicAutomate>{

	private Random r = new Random();
	private AutoMusic current, next;

	public MusicPlayer(MusicAutomate app) {
		super(app, 100);
	}

	public void init() {
		TinySound.init();
	}

	public void process() {
		if(current == null) {
			current = app.getMusicBuffer().getNext();
		}else if(next == null) {
			next = app.getMusicBuffer().getNext();
		}
		double tTime = app.getConfig().getTransitionTime();
		if(current != null) {
			if(current.getTinyMusic().playing()) {
				double durationLeft = (current.getTinyMusic().getDuration() - current.getTinyMusic().getCurrentPosition())/ 1000.0;
				if(durationLeft < tTime) {
					if(!next.getTinyMusic().playing()) {
						next.getTinyMusic().play(false, 0);
					}
					current.getTinyMusic().setVolume(durationLeft/tTime);
					next.getTinyMusic().setVolume(1 - current.getTinyMusic().getVolume());
				}
			}else if(!current.getTinyMusic().done()) {
				current.getTinyMusic().play(false);
				app.getConsole().getLogger().log(Level.INFO	, "Now Playing: " + new File(current.getAudioFile().filename()).getName());;
			}else {
				current.getTinyMusic().unload();
				app.getConsole().getLogger().log(Level.FINE	, "Unloaded: " + new File(current.getAudioFile().filename()).getName());
				current = next;
				next = null;
				app.getConsole().getLogger().log(Level.INFO	, "Now Playing: " + new File(current.getAudioFile().filename()).getName());
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
