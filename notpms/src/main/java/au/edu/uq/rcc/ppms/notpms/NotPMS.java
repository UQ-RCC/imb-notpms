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
import au.edu.uq.rcc.ppms.api.PPMSApiImpl;
import au.edu.uq.rcc.ppms.notpms.Utils.BookingState;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.json.Json;
import javax.json.JsonReader;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.ini4j.Ini;

public class NotPMS {

	private final PPMSConfig config;
	private final URI ppmsBookUri;
	private final String systemUser;

	private PPMSApiImpl ppms;
	private au.edu.uq.rcc.ppms.api.System system;
	private final UserSettings userSettings;
	private BookingResult bookingResult;
	private boolean stopPester;

	private final ControlPanelUI ui;
	private final IncidentUI incidentUi;
	private final NaggerUI naggerUi;

	private final Timer updateTimer;
	private CompletableFuture<Void> updateTimerFuture;

	private final Timer logoffTimer;
	private Optional<Instant> logoffTime;

	private final int[] nagThresholds;
	private final boolean[] nagStates;

	public NotPMS(PPMSConfig cfg, String systemUser) {
		this.config = cfg;
		this.ppmsBookUri = cfg.ppmsURI().resolve(String.format("planning/?item=%d", cfg.instrumentId()));
		this.systemUser = systemUser;

		this.ppms = null;
		this.system = null;
		this.userSettings = new UserSettings();
		this.bookingResult = BookingResult.initial();
		this.stopPester = false;

		ActionListener l = new _ButtonListener();
		this.ui = new ControlPanelUI(l);
		this.ui.addWindowListener(new _WindowListener());
		this.incidentUi = new IncidentUI(ui, true);
		this.naggerUi = new NaggerUI(l);

		this.updateTimer = new Timer(10000, ae -> {
			if(updateTimerFuture.isDone()) {
				updateTimerFuture = update();
			}
		});
		this.updateTimer.setRepeats(true);
		this.updateTimerFuture = CompletableFuture.completedFuture(null);

		this.logoffTimer = new Timer(60000, ae -> Utils.logoff());
		this.logoffTimer.stop();
		this.logoffTimer.setRepeats(false);
		this.logoffTime = Optional.empty();

		this.nagThresholds = config.nagThresholds();
		this.nagStates = new boolean[config.nagThresholds().length];
		resetNags();

		SwingUtilities.invokeLater(() -> {
			Rectangle screenRect = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice()
					.getDefaultConfiguration()
					.getBounds();

			/* Set location to the bottom-right corner. */
			ui.pack();
			ui.setLocation((int)screenRect.getMaxX() - ui.getWidth(), (int)screenRect.getMaxY() - ui.getHeight());
			updateState(BookingResult.initial(), null);
			ui.setVisible(true);

			/* Set location to the center. */
			naggerUi.pack();
			naggerUi.setLocationRelativeTo(null);
			//naggerUi.setVisible(true);
		});
	}

	private void resetNags() {
		for(int i = 0; i < nagStates.length; ++i) {
			nagStates[i] = false;
		}
	}

	private void onWindowOpened(WindowEvent e) {
		ppms = new PPMSApiImpl(config.ppmsURI(), config.pumKey());

		/* Set the initial state. */
		//updateState(BookingResult.initial(), null);
		update().handle((s, t) -> {
			updateTimer.start();
			return null;
		});
	}

	private Void updateProc(BookingPair bp, Throwable t) {
		if(t != null) {
			t.printStackTrace(System.err);
		}

		SwingUtilities.invokeLater(() -> updateState(Utils.processBookings(systemUser, bp == null ? BookingPair.empty() : bp), t));
		return null;
	}

	private CompletableFuture<Void> update() {
		if(system == null) {
			/*
			 * There's a bit of screwry going on here.
			 * I can't join on the updateBookings() future directly as we're in the HTTP client's thread, so
			 * create a new future, use allOf() to wait on both.
			 * Allow the system future to finish, which will unblock the bookings future and allow it to finish.
			 */
			CompletableFuture<Void> bf = new CompletableFuture<>();
			CompletableFuture<Void> sf = ppms.getSystem(config.instrumentId()).handle((s, t) -> {
				if(t != null) {
					updateProc(null, t);
					bf.complete(null);
					return null;
				}

				if(!s.isPresent()) {
					/* We're running on an invalid system. */
					updateProc(null, new IllegalStateException("Unregistered PPMS system"));
					bf.complete(null);
					return null;
				}

				system = s.get();
				updateBookings().handle((ss, tt) -> {
					if(t != null) {
						bf.completeExceptionally(tt);
					} else {
						bf.complete(ss);
					}
					return null;
				});
				return null;
			});
			return CompletableFuture.allOf(sf, bf);

		} else {
			return updateBookings();
		}
	}

	private CompletableFuture<Void> updateBookings() {
		return ppms.getBookingInfo(config.instrumentId(), config.instrumentCode()).handle(this::updateProc);
	}

	private static UserSettings decodeUserSettings(String s) {
		String json;
		try {
			json = new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
		} catch(IllegalArgumentException e) {
			return new UserSettings();
		}

		try( JsonReader jr = Json.createReader(new StringReader(json))) {
			return UserSettings.fromJson(jr.readObject());
		} catch(RuntimeException e) {
			return new UserSettings();
		}
	}

	private static String encodeUserSettings(UserSettings us) {
		return Base64.getEncoder().encodeToString(us.toJson().toString().getBytes(StandardCharsets.UTF_8));
	}

	private void updateState(BookingResult br, Throwable t) {
		//System.err.printf("%s\n", br);
		ui.setBookingEnabled(true);
		ui.setLogoffEnabled(true);

		if(t != null) {
			ui.setTitle("ERROR");
			ui.setTopText("An error occurred when querying bookings.");
			ui.setBottomText("Please contact your system administrator.");
			updateStateNagger(br, t);
			return;
		}

		ui.setTitle(br.s.toString());

		if(br.s == BookingState.INITIAL) {
			ui.setTopText("Please wait, checking booking...");
			ui.setBottomText("");
			ui.setIncidentEnabled(false);
			ui.setEmailEnabled(false);
		} else if(br.s == BookingState.NO_BOOKING || br.s == BookingState.INVALID_BOOKING) {
			ui.setTopText("You don't have a booking.");
			ui.setBottomText("Please make a booking or logoff immediately.");
			ui.setIncidentEnabled(false);
			ui.setEmailEnabled(false);
		} else if(br.s == BookingState.PRE_BOOKING) {
			ui.setTopText("You don't have a booking.");
			Booking b = br.bookings.next.get();
			ui.setBottomText("%s's booking will start in %d minute(s)...", b.username, b.minsLeft);
			ui.setIncidentEnabled(true);
			ui.setEmailEnabled(true);
		} else if(br.s == BookingState.BOOKING) {
			Booking b = br.bookings.current.get();
			ui.setTopText("%s, you have %d minute(s) remaining...", b.username, b.minsLeft);

			br.bookings.next.ifPresentOrElse(
					n -> ui.setBottomText("%s's booking starts in %d minute(s)...", n.username, n.minsLeft),
					() -> ui.setBottomText("")
			);
			ui.setIncidentEnabled(true);
			ui.setEmailEnabled(true);
		}

		updateStateNagger(br, t);
		bookingResult = br;
	}

	private boolean checkNag(int timeLeft) {
		for(int i = 0; i < nagThresholds.length; ++i) {
			if(nagThresholds[i] != timeLeft || nagStates[i]) {
				continue;
			}

			return nagStates[i] = true;
		}

		return false;
	}

	private void updateLogoffTimer(BookingResult br) {
		BookingState oldState = bookingResult.s;
		BookingState newState = br.s;

		if(!config.logoffUser()) {
			logoffTime = Optional.empty();
			return;
		}

		/* Kill the logoff timer in PRE_BOOKING or BOOKING. */
		if(newState == BookingState.PRE_BOOKING || newState == BookingState.BOOKING) {
			logoffTimer.stop();
			logoffTime = Optional.empty();
		} else if(newState == BookingState.INVALID_BOOKING || newState == BookingState.NO_BOOKING) {
			Optional<Integer> mins = Optional.empty();
			if(newState != oldState && newState == BookingState.INVALID_BOOKING) {
				mins = Optional.of(1);
			} else {
				if(br.bookings.next.isPresent()) {
					Booking next = br.bookings.next.get();

					/* See if the next booking has changed. */
					if(logoffTime.isPresent()) {
						long secondsUntilLogoff = Instant.now().until(logoffTime.get(), ChronoUnit.SECONDS);
						long secondsUntilNext = next.minsLeft * 60;

						if(secondsUntilNext < secondsUntilLogoff) {
							mins = Optional.of((int)secondsUntilNext / 60);
						}

					} else {
						mins = Optional.of(next.minsLeft > config.logoffMinutes() ? config.logoffMinutes() : next.minsLeft);
					}
				} else {
					if(!logoffTime.isPresent()) {
						mins = Optional.of(config.logoffMinutes());
					}
				}
			}

			mins.ifPresent(m -> {
				//System.err.printf("LT@%s\n", m);
				/* Rearm timer. */
				logoffTimer.setInitialDelay(m * 60000);
				logoffTimer.restart();
				logoffTime = Optional.of(Instant.now().plus(m, ChronoUnit.MINUTES));
			});

		}
	}

	private void updateStateNagger(BookingResult br, Throwable t) {
		/* If there's an error, let the control panel handle it. */
		if(t != null || br.s == BookingState.INITIAL) {
			naggerUi.setVisible(false);
			return;
		}

		updateLogoffTimer(br);

		/* If we're leaving a booking, start pestering again. */
		if(br.s != BookingState.BOOKING) {
			this.stopPester = false;
		}

		naggerUi.clearText();
		naggerUi.setPPMSVisible(true);
		naggerUi.setIncidentVisible(true);

		/* If we're on our INITIAL state, we shouldn't be visible. Also system isn't set. */
		if(br.s == BookingState.INITIAL) {
			naggerUi.setVisible(false);
			return;
		}

		int maxGap = config.maxGap();
		//boolean logoffUserFlag = config.logoffUser();
		String forSystem = String.format(" for %s", system.name);

		Optional<Long> logoffMinutes = logoffTime
				.map(then -> Instant.now().until(then, ChronoUnit.MINUTES))
				.map(l -> l < 1 ? 1 : l);

		boolean showNag = false;
		if(br.s == BookingState.INITIAL) {
			naggerUi.setQuickBookVisible(false);
			naggerUi.setPesterVisible(false);
			naggerUi.setLogoffVisible(true);

			naggerUi.clearText();
		} else if(br.s == BookingState.BOOKING) {
			Booking current = br.bookings.current.get();
			naggerUi.setText(1, "%s, your booking%s will end in %d minute(s).", current.username, forSystem, current.minsLeft);

			boolean allowQuick;
			if(br.bookings.next.isPresent()) {
				Booking next = br.bookings.next.get();

				if(systemUser.equalsIgnoreCase(next.username)) {
					naggerUi.setText(2, "You have a booking%s commencing in %d minute(s).", forSystem, next.minsLeft);
				} else {
					naggerUi.setText(2, "%s has a booking%s commencing in %d minute(s).", next.username, forSystem, next.minsLeft);
				}

				allowQuick = next.minsLeft >= maxGap;

				/* Don't display logoff if we have back-to-back sessions. */
				if(br.backToBack) {
					logoffMinutes = Optional.empty();
				}
			} else {
				naggerUi.setText(2, "There are no future bookings%s.", forSystem);
				allowQuick = true;
			}

			logoffMinutes.ifPresent(l -> naggerUi.setText(3, "You will be automatically logged off in %d minutes(s).", l));

			naggerUi.setQuickBookVisible(allowQuick);
			naggerUi.setPesterVisible(true);
			showNag = checkNag(current.minsLeft);
		} else if(br.s == BookingState.PRE_BOOKING) {
			naggerUi.setQuickBookVisible(false);
			naggerUi.setPesterVisible(false);
			naggerUi.setText(1, "%s, you don't have a session booked at this time-slot%s.", this.systemUser, forSystem);

			Booking next = br.bookings.next.get();
			naggerUi.setText(2, "Luckily, you have a session commencing in %d minute(s).", next.minsLeft);
			naggerUi.setText(3, "So you won't be automatically logged off.");
			naggerUi.setText(4, "Next time, please log-on only during your session.");
			showNag = true;
		} else if(br.s == BookingState.NO_BOOKING) {
			naggerUi.setQuickBookVisible(false);
			naggerUi.setIncidentVisible(false);
			naggerUi.setPesterVisible(false);
			naggerUi.setLogoffVisible(true);

			naggerUi.setText(1, "%s, you don't have a session booked at this time-slot%s.", this.systemUser, forSystem);
			naggerUi.setText(2, "Please make a booking, of you will automatically be logged off in %s minute(s).", logoffMinutes.get());
			showNag = true;
		} else if(br.s == BookingState.INVALID_BOOKING) {
			naggerUi.setQuickBookVisible(false);
			naggerUi.setPesterVisible(false);
			naggerUi.setLogoffVisible(true);

			naggerUi.setText(1, "%s, you don't have a session booked at this time-slot%s.", this.systemUser, forSystem);

			Booking current = br.bookings.current.get();
			naggerUi.setText(2, "%s has a session which ends in %d minute(s).", current.username, current.minsLeft);

			if(br.bookings.next.isPresent()) {
				Booking next = br.bookings.next.get();
				if(systemUser.equalsIgnoreCase(next.username)) {
					naggerUi.setText(3, "You have a booking%s commencing in %d minute(s).", forSystem, next.minsLeft);
				} else {
					naggerUi.setText(3, "%s has a booking%s commencing in %d minute(s).", next.username, forSystem, next.minsLeft);
				}
			}

			logoffMinutes.ifPresent(l -> naggerUi.setText(4, "You will be automatically logged off in %d minutes(s).", l));
			showNag = true;
		}

		naggerUi.reflowButtons();
		if(!stopPester && showNag) {
			naggerUi.setVisible(true);
		}
	}

	private void onWindowClosing(WindowEvent e) {
		/* With DO_NOTHING on close, there's no nice way to do this. */
//		/* Happens if onWindowOpened() throws. */
//		if(ppms == null) {
//			return;
//		}
//
//		try {
//			writeUserSettings().get();
//		} catch(InterruptedException | ExecutionException | CompletionException ex) {
//			/* nop */
//		}
//
//		try {
//			ppms.close();
//		} catch(IOException ex) {
//			/* nop */
//		}
	}

	private CompletableFuture<Void> writeUserSettings() {
		return bookingResult.bookings.current
				.map(b -> ppms.setSessionNote(b.sessionId, encodeUserSettings(userSettings)))
				.orElse(CompletableFuture.completedFuture(null));
	}

	private class _ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			switch(ae.getActionCommand()) {
				case "book":
				case "openppms":
					Utils.openUri(ppmsBookUri);
					break;
				case "stoppester":
					stopPester = true;
					naggerUi.setVisible(false);
					break;
				case "incident":
					incidentUi.setVisible(true);
					incidentUi.getCloseStatus()
							.filter(b -> b)
							.ifPresent(b -> ppms.reportIncident(config.instrumentId(), incidentUi.getSeverity(), incidentUi.getComment()));
					break;
				case "email": {
					int opt = JOptionPane.showConfirmDialog(
							ui,
							"Do you wish to receive reminder emails?",
							"Email Preferences",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE
					);

					if(opt == 0) {
						userSettings.email = Optional.of(true);
						writeUserSettings();
					} else if(opt == 1) {
						userSettings.email = Optional.of(false);
						writeUserSettings();
					}
					break;
				}
				case "logoff":
					Utils.logoff();
					break;
			}
		}

	}

	private class _WindowListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			NotPMS.this.onWindowClosing(e);
		}

		@Override
		public void windowOpened(WindowEvent e) {
			NotPMS.this.onWindowOpened(e);
		}
	}

	public static Path getConfigFilePath() {
		if(Utils.isWindows()) {
			return Paths.get(System.getenv("ProgramData"), "notpms.ini");
		} else {
			return Paths.get("/etc/notpms.ini");
		}
	}

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public static void main(String[] args) throws Exception {
		/*
		javax.swing.UIManager$LookAndFeelInfo[Metal javax.swing.plaf.metal.MetalLookAndFeel]
		javax.swing.UIManager$LookAndFeelInfo[Nimbus javax.swing.plaf.nimbus.NimbusLookAndFeel]
		javax.swing.UIManager$LookAndFeelInfo[CDE/Motif com.sun.java.swing.plaf.motif.MotifLookAndFeel]
		javax.swing.UIManager$LookAndFeelInfo[GTK+ com.sun.java.swing.plaf.gtk.GTKLookAndFeel]
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
			/* nop */
		}

		PPMSConfig cfg;
		try( InputStream is = Files.newInputStream(getConfigFilePath())) {
			cfg = new PPMSConfigINI(new Ini(is));
		} catch(RuntimeException | IOException e) {
			e.printStackTrace(System.err);
			JOptionPane.showMessageDialog(null,
					"Unable to load configuration.\nPlease contact your system administrator.",
					"PPMS Control Panel",
					JOptionPane.ERROR_MESSAGE
			);
			System.exit(1);
			return;
		}

		String systemUser = System.getProperty("user.name");
		if(Utils.isWindows()) {
			/* Unsupported, but no other way to do it. */
			com.sun.security.auth.module.NTSystem nt = new com.sun.security.auth.module.NTSystem();
			systemUser = nt.getName();
		}
		new NotPMS(cfg, systemUser);
	}
}
