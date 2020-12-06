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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.ResourceNameFilter;
import jcifs.SmbResource;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SMBSource extends MusicSource{

	private final String domain;
	private final String username;
	private final String password;
	private final @Getter String address;
	private final @Getter String path;
	private final String scratchPath;

	public boolean exists() {
		try(SmbResource res = getContext().get(getRemoteURL())){
			return res != null && res.exists();
		} catch (CIFSException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean canRead() {
		try(SmbResource res = getContext().get(getRemoteURL())){
			return res != null && res.canRead();
		} catch (CIFSException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String[] listFiles() {
		try(SmbResource res = getContext().get(getRemoteURL())){
			List<String> names = new ArrayList<String>();
			Iterator<SmbResource> itr = res.children(new ResourceNameFilter() {
				public boolean accept(SmbResource parent, String name) throws CIFSException {
					for(String format : MusicSource.getFileFormats()) {
						if(name.toLowerCase().endsWith("." + format)) {
							return true;
						}
					}
					return false;
				}
			});
			while(itr.hasNext()) {
				names.add(itr.next().getName());
			}
			return names.toArray(new String[names.size()]);
		} catch (CIFSException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File getFile(String name) {
		File file = null;
		try(SmbResource res = getContext().get(getRemoteURL())){
			try(SmbResource child = res.resolve(name)){
				if(child != null) {
					file = new File(scratchPath + "/" + name);
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					IOUtils.copyLarge(child.openInputStream(), fos);
					IOUtils.closeQuietly(fos);
					file.deleteOnExit();
					return file;
				}
			}
		} catch (IOException e) {
			if(file != null) {
				file.delete();
			}
			e.printStackTrace();
		}
		return null;
	}

	public void complete(File file) {
		file.delete();
	}

	private CIFSContext getContext() {
		return SingletonContext.getInstance().withCredentials(getAuth());
	}

	private NtlmPasswordAuthenticator getAuth() {
		return new NtlmPasswordAuthenticator(domain, username, password);
	}

	private String getRemoteURL() {
		return "smb://" + address + path;
	}


}
