/*
 * Copyright (C) 2021 Matthew Rosato
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
package com.t07m.musicautomate.system.monitors;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.musicautomate.MusicAutomate;

import kuusisto.tinysound.TinySound;

public class SkippedFrameMonitor extends SystemMonitor{

	private static Logger logger = LoggerFactory.getLogger(SkippedFrameMonitor.class);
	
	private long lastPrint = System.currentTimeMillis();
	
	private int trigger = 1000;
	private int reset = 500;
	private double volumeStep = 1.0/(5000/250);

	private boolean triggered = false;
	private boolean reseting = false;
	private double triggeredVolume = 0;
	private double expectedVolume = 0;

	public SkippedFrameMonitor(MusicAutomate app) {
		super(app, 250);
	}

	public void process() {
		if(!triggered) {
			if(TinySound.getSkippedFrameAverage() > trigger) {
				logger.info("SkippedFrameMonitor triggered at: " + TinySound.getSkippedFrameAverage());
				lastPrint = System.currentTimeMillis();
				triggered = true;
				if(!reseting)
					expectedVolume = triggeredVolume = TinySound.getGlobalVolume();
				reseting = false;
			}
		}else if(TinySound.getSkippedFrameAverage() < reset) {
			logger.info("SkippedFrameMonitor reseting at: " + TinySound.getSkippedFrameAverage());
			triggered = false;
			reseting = true;
		}
		double gv = TinySound.getGlobalVolume();
		if(triggered || reseting) {
			if(gv == expectedVolume) {
				if(triggered && gv > 0) {
					expectedVolume = Math.max(gv-volumeStep, 0);
					logger.debug("SkippedFrameMonitor: GlobalVolume = " + expectedVolume);
					TinySound.setGlobalVolume(expectedVolume);
				}
				if(reseting && gv < triggeredVolume) {
					expectedVolume = Math.min(gv+volumeStep, triggeredVolume);
					logger.debug("SkippedFrameMonitor: GlobalVolume = " + expectedVolume);
					TinySound.setGlobalVolume(expectedVolume);
					if(expectedVolume == triggeredVolume) {
						reseting = false;
						logger.info("SkippedFrameMonitor reset");
					}
				}
			}else {
				logger.info("Volume manually adjusted. Aborting SkippedFrameMonitor trigger.");
				triggered = false;
				reseting = false;
			}
		}
		if(triggered && System.currentTimeMillis()-lastPrint > TimeUnit.SECONDS.toMillis(10)) {
			logger.info("Average Skipped Frames = " + TinySound.getSkippedFrameAverage());
			lastPrint = System.currentTimeMillis();
		}else if(System.currentTimeMillis()-lastPrint > TimeUnit.MINUTES.toMillis(30)) {
			logger.info("Average Skipped Frames = " + TinySound.getSkippedFrameAverage());
			lastPrint = System.currentTimeMillis();
		}
	}

}
