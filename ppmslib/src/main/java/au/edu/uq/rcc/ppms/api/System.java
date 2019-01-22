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
package au.edu.uq.rcc.ppms.api;

public final class System {

	public final int coreFacilityRef;
	public final int systemId;
	public final String type;
	public final String name;
	public final String localisation;
	public final boolean active;
	public final boolean schedules;
	public final boolean stats;
	public final boolean bookable;
	public final boolean autonomyRequired;
	public final boolean autonomyRequiredAfterHours;

	public System(int coreFacilityRef, int systemId, String type, String name, String localisation, boolean active, boolean schedules, boolean stats, boolean bookable, boolean autonomyRequired, boolean autonomyRequiredAfterHours) {
		this.coreFacilityRef = coreFacilityRef;
		this.systemId = systemId;
		this.type = type;
		this.name = name;
		this.localisation = localisation;
		this.active = active;
		this.schedules = schedules;
		this.stats = stats;
		this.bookable = bookable;
		this.autonomyRequired = autonomyRequired;
		this.autonomyRequiredAfterHours = autonomyRequiredAfterHours;
	}

}
