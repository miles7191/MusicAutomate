import java.io.IOException;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

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

public class ffprobeExample {

	public static void main(String[] args) throws IOException {
		FFmpeg ffmpeg = new FFmpeg("C:\\Users\\Miles\\Desktop\\Test\\ffmpeg.exe");
		FFprobe ffprobe = new FFprobe("C:\\Users\\Miles\\Desktop\\Test\\ffprobe.exe");
		
		FFmpegProbeResult probeResult = ffprobe.probe("C:\\Users\\Miles\\Desktop\\Test\\GetWreck.mp3");
		FFmpegFormat format = probeResult.getFormat();
		System.out.println(format.duration);
	}

}
