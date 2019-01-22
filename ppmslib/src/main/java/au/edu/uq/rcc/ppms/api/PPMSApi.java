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

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PPMSApi {

	CompletableFuture<Optional<UserInfo>> getUserInfo(String name);

	CompletableFuture<Optional<Booking>> getCurrentBooking(int id, int code);

	CompletableFuture<Optional<Booking>> getNextBooking(int id, int code);

	CompletableFuture<BookingPair> getBookingInfo(int id, int code);

	CompletableFuture<String> getSessionNote(int session);

	CompletableFuture<Void> setSessionNote(int session, String note);

	CompletableFuture<Void> reportIncident(int id, int severity, String desc);

	CompletableFuture<System[]> getSystems(Optional<Integer> id);

	default CompletableFuture<Optional<System>> getSystem(int id) {
		return getSystems(Optional.of(id)).thenApply(s -> Arrays.stream(s).findFirst());
	}
}
