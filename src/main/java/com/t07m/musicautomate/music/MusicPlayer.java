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
import java.util.logging.Level;

import com.t07m.application.Service;
import com.t07m.musicautomate.MusicAutomate;
import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.music.transition.ExponentialFade;
import com.t07m.musicautomate.music.transition.Fade;
import com.t07m.musicautomate.music.transition.LinearFade;
import com.t07m.musicautomate.music.transition.Transition;

import kuusisto.tinysound.TinySound;

public class MusicPlayer extends Service<MusicAutomate>{

	private AutoMusic current, next;
	private Transition transition;

	public MusicPlayer(MusicAutomate app) {
		super(app, 100);
	}

	public void init() {
		TinySound.init();
		Fade in, out;
		MAConfig config = app.getConfig();
		switch(config.getFadeInType().toLowerCase()) {
		case "linear":
			in = new LinearFade(config.getFadeInTime());
			break;
		case "exponential":
			in = new ExponentialFade(config.getFadeInTime());
			break;
			default:
				in = new LinearFade(0);
		}
		switch(config.getFadeOutType().toLowerCase()) {
		case "linear":
			out = new LinearFade(config.getFadeOutTime());
			break;
		case "exponential":
			out = new ExponentialFade(config.getFadeOutTime());
			break;
			default:
				out = new LinearFade(0);
		}
		transition = new Transition(in, out);
	}

	public void process() {
		if(current == null) {
			current = app.getMusicBuffer().getNext();
		}else if(next == null) {
			next = app.getMusicBuffer().getNext();
		}
		if(current != null) {
			if(current.getTinyMusic().playing()) {
				double durationLeft = (current.getTinyMusic().getDuration() - current.getTinyMusic().getCurrentPosition())/ 1000.0;
				if(durationLeft < transition.getLength()) {
					if(!transition.isTransitioning()) {
						app.getConsole().getLogger().log(Level.FINER, "Begining transition to " + new File(next.getAudioFile().filename()).getName());
						transition.start(current, next);
					}
				}
			}else if(!current.getTinyMusic().done()) {
				current.getTinyMusic().play(false);
				app.getConsole().getLogger().log(Level.INFO	, "Now Playing: " + new File(current.getAudioFile().filename()).getName());
			}else {
				current.getTinyMusic().unload();
				app.getConsole().getLogger().log(Level.FINE	, "Unloaded: " + new File(current.getAudioFile().filename()).getName());
				current = next;
				next = null;
				app.getConsole().getLogger().log(Level.INFO	, "Now Playing: " + new File(current.getAudioFile().filename()).getName());
			}
		}
	}

	public void cleanup() {
		TinySound.shutdown();
	}
}
