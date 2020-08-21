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
import java.io.IOException;
import java.lang.ProcessHandle.Info;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Foobar2000 {

	private static File executable;

	public static void setExecutable(File file) {
		executable = file;
	}
	
	public static void setExecutable(String string) {
		setExecutable(new File(string));
	}

	public static boolean Show() {
		return execute("/show");
	}

	public static boolean Hide() {
		return execute("/hide");
	}

	public static boolean Play() {
		return execute("/play");
	}

	public static boolean Exit() {
		return execute("/exit");
	}

	public static void Destroy() {
		ProcessHandle ph = getProcessHandle();
		if(ph != null) {
			if(ph.isAlive()) {
				if(ph.destroy()) {
					long start = System.currentTimeMillis();
					while(ph.isAlive() && System.currentTimeMillis() - start > TimeUnit.SECONDS.toMillis(5)) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					}
					if(ph.isAlive()) {
						ph.destroyForcibly();
					}
				}
			}
		}
	}

	public  static boolean AddLocation(String location) {
		return execute("/command:\"File/Add Location...\"", location, "/immediate");
	}

	public static boolean Clear() {
		if(executable != null) {
			Destroy();
			File directory = executable.getParentFile();
			File[] files = directory.listFiles(file -> file.getName().contains("playlists"));
			if(files.length > 0) {
				for(File f : files[0].listFiles()) {
					if(!f.delete()) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	static boolean foobarValidExecutable() {
		return executable != null && executable.canRead() && executable.getName().endsWith(".exe");
	}
	
	static ProcessHandle getProcessHandle() {
		ProcessHandle[] pa = ProcessHandle.allProcesses().filter(ph -> {
			Info info = ph.info();
			if(info != null && info.command().isPresent()) {
				if(info.command().get().endsWith("foobar2000.exe")) {
					return true;
				}
			}
			return false;
		}).toArray(ProcessHandle[]::new);
		return pa.length > 0 ? pa[0] : null;
	}

	private static boolean execute(String... vars) {
		if(executable.exists() && executable.canExecute()) {
			ProcessBuilder pb = new ProcessBuilder();
			ArrayList<String> command = new ArrayList<String>();
			command.add(executable.getAbsolutePath());
			command.addAll(Arrays.asList(vars));
			pb.command(command);
			try {
				if(pb.start() != null) {
					return true;
				}
			} catch (IOException e) {}
		}
		return false;
	}

}
