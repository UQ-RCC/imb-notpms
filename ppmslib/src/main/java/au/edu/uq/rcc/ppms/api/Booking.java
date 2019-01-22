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

import java.util.Objects;

public class Booking {

	public Booking(String username, int minsLeft, int sessionId) {
		this.username = username;
		this.minsLeft = minsLeft;
		this.sessionId = sessionId;
	}

	public final String username;
	public final int minsLeft;
	public final int sessionId;

	@Override
	public String toString() {
		return "Booking{" + "username=" + username + ", minsLeft=" + minsLeft + ", sessionId=" + sessionId + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + Objects.hashCode(this.username);
		hash = 23 * hash + this.minsLeft;
		hash = 23 * hash + this.sessionId;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final Booking other = (Booking)obj;
		if(this.minsLeft != other.minsLeft) {
			return false;
		}
		if(this.sessionId != other.sessionId) {
			return false;
		}
		return Objects.equals(this.username, other.username);
	}

}
