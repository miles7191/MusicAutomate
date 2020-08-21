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

import org.apache.commons.io.FileUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHandler {

	public static boolean copyDirectory(@NonNull File src, @NonNull File dest) {
		try {
			FileUtils.copyDirectory(src, dest, file -> file.getName().endsWith(".mp3"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void clearDirectory(@NonNull File directory) {
		if(directory.exists()) {
			for(File f : directory.listFiles()) {
				if(f.isDirectory()) {
					clearDirectory(f);
				}else{
					f.delete();
				}
			}
		}
	}

}
