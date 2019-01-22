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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;

public class PPMSApiImpl implements PPMSApi, AutoCloseable {

	public final URI pumUri;
	public final String pumApiKey;
	private final CloseableHttpAsyncClient httpClient;

	public PPMSApiImpl(URI ppmsUrl, String pumApiKey) {
		this.pumUri = ppmsUrl.resolve("pumapi/");
		this.pumApiKey = pumApiKey;
		this.httpClient = HttpAsyncClients.createDefault();
		this.httpClient.start();
	}

	@Override
	public void close() throws IOException {
		if(!httpClient.isRunning()) {
			return;
		}

		try {
			httpClient.close();
		} catch(RuntimeException e) {
			throw new IOException(e);
		}
	}

	public static UserInfo userInfoFromJson(JsonObject jo) {
		return new UserInfo(
				jo.getString("login"),
				jo.getString("lname"),
				jo.getString("fname"),
				jo.getString("email"),
				jo.getString("phone"),
				jo.getString("bcode"),
				jo.getString("affiliation"),
				jo.getString("unitlogin"),
				jo.getBoolean("mustchpwd"),
				jo.getBoolean("mustchbcode"),
				jo.getBoolean("active"),
				jo.getInt("userid")
		);
	}

	@Override
	public CompletableFuture<Optional<UserInfo>> getUserInfo(String name) {
		HttpPost post = new HttpPost(pumUri);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(new UrlEncodedFormEntity(listOf(
				new BasicNameValuePair("apikey", pumApiKey),
				new BasicNameValuePair("action", "getuser"),
				new BasicNameValuePair("login", name),
				new BasicNameValuePair("withuserid", "true"),
				new BasicNameValuePair("format", "json")
		), StandardCharsets.UTF_8));

		CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
		httpClient.execute(post, new CallbackAdapter(cf));
		return cf.thenApply((HttpResponse r) -> {
			byte[] bytes = getPayload(r);

			if(bytes.length == 0) {
				return Optional.empty();
			}

			try( JsonReader jr = Json.createReader(new ByteArrayInputStream(bytes))) {
				return Optional.of(userInfoFromJson(jr.readObject()));
			}
		});
	}

	@Override
	public CompletableFuture<Optional<Booking>> getCurrentBooking(int id, int code) {
		return getBooking(id, code, "getbooking");
	}

	@Override
	public CompletableFuture<Optional<Booking>> getNextBooking(int id, int code) {
		return getBooking(id, code, "nextbooking");
	}

	private CompletableFuture<Optional<Booking>> getBooking(int id, int code, String action) {
		HttpPost post = new HttpPost(pumUri);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(new UrlEncodedFormEntity(listOf(
				new BasicNameValuePair("apikey", pumApiKey),
				new BasicNameValuePair("action", action),
				new BasicNameValuePair("id", Integer.toString(id)),
				new BasicNameValuePair("code", Integer.toString(code))
		), StandardCharsets.UTF_8));

		CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
		httpClient.execute(post, new CallbackAdapter(cf));
		return cf.thenApply(r -> {
			byte[] bytes = getPayload(r);

			if(bytes.length == 0) {
				return Optional.empty();
			}

			String[] comps = new String(bytes, StandardCharsets.UTF_8).split("[\r\n]+");
			if(comps.length != 3) {
				throw new RuntimeException("Malformed Response");
			}

			return Optional.of(new Booking(
					comps[0],
					Integer.parseUnsignedInt(comps[1]),
					Integer.parseUnsignedInt(comps[2])
			));
		});
	}

	@Override
	public CompletableFuture<BookingPair> getBookingInfo(int id, int code) {
		return CompletableFuture.supplyAsync(() -> {
			CompletableFuture<Optional<Booking>> current = this.getCurrentBooking(id, code);
			CompletableFuture<Optional<Booking>> next = this.getNextBooking(id, code);
			try {
				return new BookingPair(current.get(), next.get());
			} catch(ExecutionException | InterruptedException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<String> getSessionNote(int session) {
		HttpPost post = new HttpPost(pumUri);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(new UrlEncodedFormEntity(listOf(
				new BasicNameValuePair("apikey", pumApiKey),
				new BasicNameValuePair("action", "getsessionnote"),
				new BasicNameValuePair("resid", Integer.toString(session))
		), StandardCharsets.UTF_8));

		CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
		httpClient.execute(post, new CallbackAdapter(cf));
		return cf.thenApply(r -> new String(getPayload(r), StandardCharsets.UTF_8));
	}

	@Override
	public CompletableFuture<Void> setSessionNote(int session, String note) {
		HttpPost post = new HttpPost(pumUri);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(new UrlEncodedFormEntity(listOf(
				new BasicNameValuePair("apikey", pumApiKey),
				new BasicNameValuePair("action", "setsessionnote"),
				new BasicNameValuePair("resid", Integer.toString(session)),
				new BasicNameValuePair("note", note)
		), StandardCharsets.UTF_8));

		CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
		httpClient.execute(post, new CallbackAdapter(cf));
		/* I have never seen this call fail, even with bogus data. */
		return cf.thenRun(Function::identity);
	}

	/* Java 11 compat. */
	private <T> List<T> listOf(T... args) {
		return Arrays.stream(args).collect(Collectors.toList());
	}

	@Override
	public CompletableFuture<Void> reportIncident(int id, int severity, String desc) {
		if(severity < 1) {
			severity = 1;
		} else if(severity > 3) {
			severity = 3;
		}

		HttpPost post = new HttpPost(pumUri);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(new UrlEncodedFormEntity(listOf(
				new BasicNameValuePair("apikey", pumApiKey),
				new BasicNameValuePair("action", "createinc"),
				new BasicNameValuePair("id", Integer.toString(id)),
				new BasicNameValuePair("severity", Integer.toString(severity)),
				new BasicNameValuePair("descr", desc)
		), StandardCharsets.UTF_8));

		CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
		httpClient.execute(post, new CallbackAdapter(cf));
		return cf.thenRun(Function::identity);
	}

	@Override
	public CompletableFuture<System[]> getSystems(Optional<Integer> id) {
		HttpPost post = new HttpPost(pumUri);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		if(id.isPresent()) {
			post.setEntity(new UrlEncodedFormEntity(listOf(
					new BasicNameValuePair("apikey", pumApiKey),
					new BasicNameValuePair("action", "getsystems"),
					new BasicNameValuePair("id", Integer.toString(id.get()))
			), StandardCharsets.UTF_8));
		} else {
			post.setEntity(new UrlEncodedFormEntity(listOf(
					new BasicNameValuePair("apikey", pumApiKey),
					new BasicNameValuePair("action", "getsystems")
			), StandardCharsets.UTF_8));
		}

		CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
		httpClient.execute(post, new CallbackAdapter(cf));
		return cf.thenApply(r -> {
			byte[] bytes = getPayload(r);

			if(bytes.length == 0) {
				return new System[0];
			}

			String csvString = new String(bytes, StandardCharsets.UTF_8);
			try( CSVParser csv = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new StringReader(csvString))) {
				return csv.getRecords().stream()
						.map(rec -> new System(
						Integer.parseUnsignedInt(rec.get("Core facility ref")),
						Integer.parseUnsignedInt(rec.get("System id")),
						rec.get("Type"),
						rec.get("Name"),
						rec.get("Localisation"),
						Boolean.parseBoolean(rec.get("Active")),
						Boolean.parseBoolean(rec.get("Schedules")),
						Boolean.parseBoolean(rec.get("Stats")),
						Boolean.parseBoolean(rec.get("Bookable")),
						Boolean.parseBoolean(rec.get("Autonomy Required")),
						Boolean.parseBoolean(rec.get("Autonomy Required After Hours"))
				)).toArray(System[]::new);

			} catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	private static byte[] getPayload(HttpResponse r) {
		StatusLine sl = r.getStatusLine();
		if(sl.getStatusCode() != 200) {
			throw new CompletionException(new HttpException(sl.getReasonPhrase()));
		}

		byte[] data;
		try( ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			r.getEntity().writeTo(baos);
			data = baos.toByteArray();
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}

		/*
		 * This API is inconsistent.
		 * Some errors return a non-200 status code.
		 * Some return 200, but with:
		 * - "error: request not authorized" (invalid api key)
		 * - "Error 500" (they're not validating their input properly)
		 */
		if(data.length >= 5) {
			String err = new String(data, StandardCharsets.UTF_8).toLowerCase();
			if(err.startsWith("error")) {
				throw new CompletionException(new HttpException(err));
			}
		}

		return data;
	}
}
