/*
NotPMS PPMS Tracker
https://github.com/UQ-RCC/imb-notpms

SPDX-License-Identifier: Apache-2.0
Copyright (c) 2019 The University of Queensland

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package au.edu.uq.rcc.ppms.notpms;

import java.net.URI;
import java.util.Arrays;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

public class PPMSConfigINI implements PPMSConfig {

	private final URI ppmsUri;
	private final String pumKey;
	private final int instrumentCode;
	private final int instrumentId;
	private final int platformId;
	private final int logoffMinutes;
	private final int maxGap;
	private final int[] nagThresholds;

	public PPMSConfigINI(Ini ini) {
		Section _ppms = requireSection(ini, "ppms");

		{
			String uri = requireValue(_ppms, "url");
			if(!uri.endsWith("/")) {
				uri += "/";
			}
			this.ppmsUri = URI.create(uri);
		}

		this.pumKey = requireValue(_ppms, "pum_apikey");
		this.instrumentCode = Integer.parseUnsignedInt(requireValue(_ppms, "code"));
		this.instrumentId = Integer.parseUnsignedInt(requireValue(_ppms, "id"));
		this.platformId = Integer.parseUnsignedInt(requireValue(_ppms, "pf"));
		this.logoffMinutes = Integer.parseUnsignedInt(requireValue(_ppms, "logoff"));
		this.maxGap = Integer.parseUnsignedInt(requireValue(_ppms, "max_gap"));
		nagThresholds = Arrays.stream(_ppms.getAll("alert", int[].class)).distinct().sorted().toArray();
	}

	@Override
	public URI ppmsURI() {
		return ppmsUri;
	}

	@Override
	public String pumKey() {
		return pumKey;
	}

	@Override
	public int instrumentCode() {
		return instrumentCode;
	}

	@Override
	public int instrumentId() {
		return instrumentId;
	}

	@Override
	public int platformId() {
		return platformId;
	}

	@Override
	public boolean logoffUser() {
		return logoffMinutes != 0;
	}

	@Override
	public int logoffMinutes() {
		return logoffMinutes;
	}

	@Override
	public int maxGap() {
		return maxGap;
	}

	@Override
	public int[] nagThresholds() {
		return Arrays.copyOf(nagThresholds, nagThresholds.length);
	}

	public static Section requireSection(Ini ini, String name) {
		Section s = ini.get(name);
		if(s == null) {
			throw new IllegalArgumentException(String.format("Missing [%s] section", name));
		}
		return s;
	}

	public static String requireValue(Section s, String name) {
		String val = s.fetch(name);
		if(val == null) {
			throw new IllegalArgumentException(String.format("Missing key '%s' in [%s] section", name, s.getName()));
		}
		return val;
	}
}
