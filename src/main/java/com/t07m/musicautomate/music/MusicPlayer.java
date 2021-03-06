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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.musicautomate.MusicAutomate;
import com.t07m.musicautomate.config.MAConfig;
import com.t07m.musicautomate.music.transition.ExponentialFade;
import com.t07m.musicautomate.music.transition.Fade;
import com.t07m.musicautomate.music.transition.LinearFade;
import com.t07m.musicautomate.music.transition.Transition;

import lombok.ToString;

@ToString(callSuper = true)
public class MusicPlayer extends Service<MusicAutomate>{

	private static Logger logger = LoggerFactory.getLogger(MusicPlayer.class);

	private AutoMusic current, next;
	private Transition transition;

	public MusicPlayer(MusicAutomate app) {
		super(app, 200);
	}

	public void init() {
		Fade in = null, out = null;
		MAConfig config = getApp().getConfig();
		if(config.getFadeIn() != null) {
			switch(config.getFadeIn().getType().toLowerCase()){
			case "linear":
				in = new LinearFade(config.getFadeIn().getTime());
				break;
			case "exponential":
				in = new ExponentialFade(config.getFadeIn().getTime());
				break;
			}
		}
		if(config.getFadeOut() != null) {
			switch(config.getFadeOut().getType().toLowerCase()){
			case "linear":
				out = new LinearFade(config.getFadeOut().getTime());
				break;
			case "exponential":
				out = new ExponentialFade(config.getFadeOut().getTime());
				break;
			}
		}
		if(in == null)
			in = new LinearFade(0);
		if(out == null)
			out = new LinearFade(0);
		transition = new Transition(in, out);
	}

	public void process() {
			if(current == null) {
				current = getApp().getMusicBuffer().getNext();
			}else if(next == null) {
				next = getApp().getMusicBuffer().getNext();
			}
			if(current != null) {
				if(current.getTinyMusic().playing()) {
					double durationLeft = (current.getTinyMusic().getDuration() - current.getTinyMusic().getCurrentPosition())/ 1000.0;
					if(transition != null && next != null) {
						if(durationLeft < transition.getLength()) {
							if(!transition.isTransitioning()) {
								logger.debug("Begining transition to " + new File(next.getAudioFile().getFilePath()).getName());
								transition.start(current, next);
							}
						}
					}
				}else if(!current.getTinyMusic().done()) {
					current.getTinyMusic().play(false);
					logger.info("Now Playing: " + new File(current.getAudioFile().getFilePath()).getName());
				}else {
					current.getTinyMusic().unload();
					logger.debug("Unloaded: " + new File(current.getAudioFile().getFilePath()).getName());
					current = next;
					next = null;
					if(current != null) {
						logger.info("Now Playing: " + new File(current.getAudioFile().getFilePath()).getName());
					}
				}
			}
	}
}
