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
import java.util.Optional;

public class BookingPair {

	public final Optional<Booking> current;
	public final Optional<Booking> next;

	public BookingPair(Optional<Booking> current, Optional<Booking> next) {
		this.current = current;
		this.next = next;
	}

	public static BookingPair empty() {
		return new BookingPair(Optional.empty(), Optional.empty());
	}

	@Override
	public String toString() {
		return "BookingPair{" + "current=" + current + ", next=" + next + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.current);
		hash = 71 * hash + Objects.hashCode(this.next);
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
		final BookingPair other = (BookingPair)obj;
		if(!Objects.equals(this.current, other.current)) {
			return false;
		}
		return Objects.equals(this.next, other.next);
	}
}
