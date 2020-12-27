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
package com.t07m.musicautomate.command;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.console.Command;
import com.t07m.console.Console;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import kuusisto.tinysound.TinySound;

public class SetCommand extends Command{

	private static Logger logger = LoggerFactory.getLogger(SetCommand.class);
	
	public SetCommand() {
		super("Set");
		OptionParser op = new OptionParser();
		String[] volumeOptions = { "v", 
		"volume" };
		op.acceptsAll(Arrays.asList(volumeOptions), "Volume").withRequiredArg().ofType(Double.class);
		setOptionParser(op);
	}
	
	public void process(OptionSet optionSet, Console console) {
	    if(optionSet.has("volume")) {
	    	double volume = ((Double) optionSet.valueOf("volume")).doubleValue();
	    	TinySound.setGlobalVolume(volume/100);
	    	logger.info("Volume: " + (int) (TinySound.getGlobalVolume()*100) + "%");
	    }
	  }	
	
}
