import java.io.File;

import kuusisto.tinysound.TinySound;
import kuusisto.tinysound.internal.MemMusic;

public class TinySoundExample {

	public static void main(String[] args) {
		//initialize TinySound
		TinySound.init();
		//load a sound and music
		//note: you can also load with Files, URLs and InputStreams
		MemMusic song = (MemMusic) TinySound.loadMusic(new File("C:\\Users\\Miles\\Desktop\\Test\\Slow Drive.wav"));
		//start playing the music
		song.play(true);
		boolean increasing = false;
		
		while(!song.done()) {
			try {
				/*
				if(song.getVolume() <= .25) {
					increasing = true;
					//TinySound.shutdown();
					//System.exit(0);
				}else if(song.getVolume() >= .95) {
					increasing = false;
				}
				if(increasing) {
					song.setVolume(song.getVolume() + .05);
				}else {
					song.setVolume(song.getVolume() - .05);
				}
				*/
				
				//System.out.println(song.getVolume());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TinySound.shutdown();
	}
	
}
