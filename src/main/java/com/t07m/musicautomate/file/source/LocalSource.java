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
package com.t07m.musicautomate.file.source;

import java.io.File;
import java.io.FilenameFilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalSource extends MusicSource{

	private final @Getter File file;
	
	public boolean exists() {
		return file.exists();
	}

	public boolean canRead() {
		return file.canRead();
	}

	public String[] listFiles() {
		return file.list(new FilenameFilter() {

			public boolean accept(File file, String name) {
				for(String format : MusicSource.getFileFormats()) {
					if(name.toLowerCase().endsWith("." + format)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public File getFile(String name) {
		return new File(file.getAbsoluteFile() + "\\" + name);
	}

}
