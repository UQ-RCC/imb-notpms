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
package au.edu.uq.rcc.ppms.controlpanel;

import au.edu.uq.rcc.ppms.notpms.Utils;
import au.edu.uq.rcc.ppms.notpms.BookingResult;
import au.edu.uq.rcc.ppms.api.Booking;
import au.edu.uq.rcc.ppms.api.BookingPair;
import au.edu.uq.rcc.ppms.notpms.Utils.BookingState;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class BookingLogicTests {

	@Test
	public void bookingTests() {
		BookingPair bp;

		bp = new BookingPair(
				Optional.of(new Booking("user1", 10, 0)),
				Optional.empty()
		);
		Assert.assertEquals(
				new BookingResult(BookingState.BOOKING, false, bp),
				Utils.processBookings("user1", bp)
		);

		/* No current or next bookings. */
		Assert.assertEquals(
				new BookingResult(BookingState.NO_BOOKING, false, BookingPair.empty()),
				Utils.processBookings("user1", BookingPair.empty())
		);

		/* User owns current and next sessions. */
		bp = new BookingPair(
				Optional.of(new Booking("user1", 10, 0)),
				Optional.of(new Booking("user1", 10, 0))
		);
		Assert.assertEquals(
				new BookingResult(BookingState.BOOKING, true, bp),
				Utils.processBookings("user1", bp)
		);

		/* User owns current, but not next session. */
		bp = new BookingPair(
				Optional.of(new Booking("user1", 10, 0)),
				Optional.of(new Booking("user2", 10, 0))
		);
		Assert.assertEquals(
				new BookingResult(BookingState.BOOKING, false, bp),
				Utils.processBookings("user1", bp)
		);

		/* User owns next, but not current session. */
		bp = new BookingPair(
				Optional.of(new Booking("user2", 10, 0)),
				Optional.of(new Booking("user1", 10, 0))
		);
		Assert.assertEquals(
				new BookingResult(BookingState.INVALID_BOOKING, false, bp),
				Utils.processBookings("user1", bp)
		);

		/* User owns next, and there's no current booking. */
		bp = new BookingPair(
				Optional.empty(),
				Optional.of(new Booking("user1", 10, 0))
		);
		Assert.assertEquals(
				new BookingResult(BookingState.PRE_BOOKING, false, bp),
				Utils.processBookings("user1", bp)
		);

	}
}
