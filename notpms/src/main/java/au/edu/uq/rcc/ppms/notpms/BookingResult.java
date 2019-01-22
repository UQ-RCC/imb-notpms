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

import au.edu.uq.rcc.ppms.api.BookingPair;
import au.edu.uq.rcc.ppms.notpms.Utils.BookingState;
import java.util.Objects;

public class BookingResult {

	public final BookingState s;
	public final BookingPair bookings;
	public final boolean backToBack;

	public static BookingResult initial() {
		return new BookingResult(BookingState.INITIAL, false, BookingPair.empty());
	}

	public BookingResult(BookingState s, boolean backToBack, BookingPair bookings) {
		this.s = s;
		this.backToBack = s == BookingState.BOOKING && backToBack;
		this.bookings = bookings;
	}

	@Override
	public String toString() {
		return "BookingResult{" + "s=" + s + ", bookings=" + bookings + ", backToBack=" + backToBack + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.s);
		hash = 37 * hash + Objects.hashCode(this.bookings);
		hash = 37 * hash + (this.backToBack ? 1 : 0);
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
		final BookingResult other = (BookingResult)obj;
		if(this.backToBack != other.backToBack) {
			return false;
		}
		if(this.s != other.s) {
			return false;
		}
		if(!Objects.equals(this.bookings, other.bookings)) {
			return false;
		}
		return true;
	}

}
