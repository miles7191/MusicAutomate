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
package com.t07m.musicautomate.foobar2000;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.t07m.application.Service;
import com.t07m.musicautomate.FileHandler;
import com.t07m.musicautomate.MusicAutomate;

public class FoobarManager extends Service<MusicAutomate>{

	private String workingSource = "working";

	public FoobarManager(MusicAutomate app) {
		super(app, TimeUnit.SECONDS.toMillis(15));
	}

	public void init() {
		app.getConsole().getLogger().info("Initializing Foobar2000 Manager.");
		if(Foobar2000.getProcessHandle() != null) {
			app.getConsole().getLogger().warning("Found Existing Foobar2000 Process Running! Killing process.");
			Foobar2000.Destroy();
		}
		app.getConsole().getLogger().info("Setting Foobar2000 path: " + app.getConfig().getFooBar2000Path());
		Foobar2000.setExecutable(app.getConfig().getFooBar2000Path());
	}

	public void process() {
		if(Foobar2000.foobarValidExecutable()) {
			if(Foobar2000.getProcessHandle() == null) {
				if(Foobar2000.Clear()) {
					app.getConsole().getLogger().info("Clearing working directory.");
					FileHandler.clearDirectory(new File(workingSource));
					app.getConsole().getLogger().info("Starting file copy.");
					if(FileHandler.copyDirectory(new File(app.getConfig().getMusicSourcePath()), new File(workingSource))) {
						app.getConsole().getLogger().info("File copy finished.");
						app.getConsole().getLogger().info("Launching Foobar2000.");
						Foobar2000.AddLocation(new File(workingSource).getAbsolutePath());
						Foobar2000.Play();
						Foobar2000.Hide();
					}else {
						app.getConsole().getLogger().warning("File copy failed!");
					}
				}
			}else {
				Foobar2000.Hide();
			}
		}else {
			app.getConsole().getLogger().severe("Foobar2000 Executable is invalid!");
		}
	}
	
	public void cleanup() {
		Foobar2000.Destroy();
	}

}
