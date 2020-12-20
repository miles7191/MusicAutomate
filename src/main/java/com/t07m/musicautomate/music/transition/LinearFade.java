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

import lombok.ToString;

@ToString
public class LinearFade extends Fade{

	public LinearFade(double length) {
		super(length);
	}

	public void update(AutoMusic music, Direction direction, double remaining) {
		double percentage = remaining/length;
		if(direction == Direction.OUT) {
			music.getTinyMusic().setVolume(percentage);
		}else if(direction == Direction.IN) {
			music.getTinyMusic().setVolume(1-percentage);
		}
	}

}
