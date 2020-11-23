import java.io.IOException;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;

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

public class ffmpegExample {

	public static void main(String[] args) throws IOException {
		FFmpeg ffmpeg = new FFmpeg("C:\\Users\\Miles\\Desktop\\Test\\ffmpeg.exe");
		FFprobe ffprobe = new FFprobe("C:\\Users\\Miles\\Desktop\\Test\\ffprobe.exe");

		FFmpegBuilder builder = new FFmpegBuilder()

		  .setInput("C:\\Users\\Miles\\Desktop\\Test\\GetWreck.mp3")     // Filename, or a FFmpegProbeResult
		  .overrideOutputFiles(true) // Override the output if it exists

		  .addOutput("C:\\Users\\Miles\\Desktop\\Test\\GetWreck.wav")   // Filename for the destination
		    .setFormat("wav")        // Format is inferred from filename, or can be set

		    .setAudioChannels(1)         // Mono audio
		    .setAudioSampleRate(44100)  // at 48KHz
		    .setAudioBitRate(16384)      // at 32 kbit/s

		    .done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

		// Run a one-pass encode
		FFmpegJob job = executor.createJob(builder);
		job.run();
		System.out.println(job.getState());
	}
	
}
