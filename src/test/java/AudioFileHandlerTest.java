import java.io.File;

import org.junit.Test;

import com.t07m.musicautomate.file.AudioFile;
import com.t07m.musicautomate.file.AudioFileHandler;
import com.t07m.musicautomate.file.FFmpegHandler;
import com.t07m.musicautomate.file.FFprobeHandler;

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

public class AudioFileHandlerTest {

	@Test
	public void test() {
		FFmpegHandler.setPath("lib/ffmpeg.exe");
		FFprobeHandler.setPath("lib/ffprobe.exe");
		File sourceDir = new File("C:\\Users\\Miles\\Documents Thiara\\Lobby Music\\Source");
		for(File file : sourceDir.listFiles()) {
			AudioFile source = AudioFileHandler.getAudioFile(file.getAbsolutePath());
			AudioFile converted = AudioFileHandler.convertAudioFile(source, "C:\\Users\\Miles\\Desktop\\Test\\Scratch");
			System.out.println(converted);
		}
		
	}

}
