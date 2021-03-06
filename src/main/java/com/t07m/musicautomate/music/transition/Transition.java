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
package com.t07m.musicautomate.music.transition;

import com.t07m.musicautomate.music.AutoMusic;
import com.t07m.musicautomate.music.transition.Fade.Direction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class Transition {

	private final Fade fadeIn, fadeOut;
	private @Getter boolean transitioning;

	public double getLength() {
		return Math.max(fadeIn.getLength(), fadeOut.getLength());
	}

	public void start(AutoMusic current, AutoMusic next) {
		if(!transitioning) {
			next.getTinyMusic().play(false, 0);
			Thread t = new Thread() {
				public void run() {
					long currentEndPosition = 0; 
					if(current.getTinyMusic().loaded()) {
						currentEndPosition = Math.min(current.getTinyMusic().getCurrentPosition() + (long) (fadeOut.getLength() * 1000.0), current.getTinyMusic().getDuration());
					}
					while(transitioning) {
						double remaining = 0;
						if(current.getTinyMusic().loaded() && current.getTinyMusic().playing()) {
							remaining = (currentEndPosition - current.getTinyMusic().getCurrentPosition())/ 1000.0;
						}
						if(next.getTinyMusic().loaded() && remaining < fadeIn.getLength()) {
							fadeIn.update(next, Direction.IN, remaining);
						}
						if(current.getTinyMusic().loaded() && remaining < fadeOut.getLength()) {
							fadeOut.update(current, Direction.OUT, remaining);
						}
						if(remaining <= 0) {
							transitioning = false;
							current.getTinyMusic().skipToEnd();
							try {
								Thread.currentThread().join();
							} catch (InterruptedException e) {}
						}else {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {}
						}
					}
				}
			};
			transitioning = true;
			t.start();
		}
	}	
}
