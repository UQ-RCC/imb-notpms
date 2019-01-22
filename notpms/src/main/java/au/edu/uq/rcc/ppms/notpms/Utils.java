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

import au.edu.uq.rcc.ppms.api.Booking;
import au.edu.uq.rcc.ppms.api.BookingPair;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class Utils {

	public enum BookingState {
		INITIAL,
		NO_BOOKING,
		INVALID_BOOKING,
		PRE_BOOKING,
		BOOKING
	}

	public static BookingResult processBookings(String currentUser, BookingPair bookings) {
		BookingState s = BookingState.NO_BOOKING;
		boolean backToBack = false;

		Optional<String> user = bookings.current.map(b -> b.username);

		boolean currentUserEquals = user.map(u -> currentUser.equals(u)).orElse(false);
		boolean nextUserEquals = bookings.next.map(b -> currentUser.equals(b.username)).orElse(false);

		if(!bookings.current.isPresent() && !bookings.next.isPresent()) {
			s = BookingState.NO_BOOKING;
		} else if(bookings.current.isPresent() && bookings.next.isPresent()) {
			if(!currentUserEquals) {
				s = BookingState.INVALID_BOOKING;
			} else {
				s = BookingState.BOOKING;

				Booking c = bookings.current.get();
				Booking n = bookings.next.get();

				backToBack = c.minsLeft == n.minsLeft && c.username.equals(n.username);
			}
		} else if(bookings.current.isPresent() && !bookings.next.isPresent()) {
			if(currentUserEquals) {
				s = BookingState.BOOKING;
			} else {
				s = BookingState.INVALID_BOOKING;
			}
		} else if(!bookings.current.isPresent() && bookings.next.isPresent()) {
			if(nextUserEquals) {
				s = BookingState.PRE_BOOKING;
			} else {
				s = BookingState.NO_BOOKING;
			}
		}

		return new BookingResult(s, backToBack, bookings);
	}

	public static boolean isWindows() {
		return "x.dll".equals(System.mapLibraryName("x"));
	}

	public static void logoff() {
		//System.err.printf("LOGOFF USER\n");
		if(isWindows()) {
			/*
			 * The following don't work anymore:
			 * rundll32.exe shell32.dll,SHExitWindowsEx 0
			 */
			try {
				Runtime.getRuntime().exec("logoff");
			} catch(IOException e) {
				/* nop */
			}
		} else {
			/* FIXME: What do here? */
		}
	}

	public static void openUri(URI ppmsUri) {
		if(!Desktop.isDesktopSupported()) {
			return;
		}
		try {
			Desktop.getDesktop().browse(ppmsUri);
		} catch(IOException e) {
			/* nop */
		}
	}
}
