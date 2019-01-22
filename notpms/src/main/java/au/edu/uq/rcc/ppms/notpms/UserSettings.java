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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class UserSettings {

	public Optional<Boolean> email;
	public final Map<Long, String> questions;

	public UserSettings() {
		this.email = Optional.of(true);
		this.questions = new HashMap<>();
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public JsonObject toJson() {
		JsonObjectBuilder jb = Json.createObjectBuilder();
		email.ifPresent(b -> jb.add("email", b));

		JsonObjectBuilder qb = Json.createObjectBuilder();
		questions.entrySet().forEach(e -> qb.add(e.getKey().toString(), e.getValue()));
		jb.add("questions", qb);
		return jb.build();
	}

	private static Optional getOptionalBoolean(JsonObject jo, String name) {
		JsonValue jv = jo.get(name);
		if(jv == null) {
			return Optional.empty();
		}

		if(jv.getValueType() == JsonValue.ValueType.TRUE) {
			return Optional.of(true);
		} else if(jv.getValueType() == JsonValue.ValueType.FALSE) {
			return Optional.of(false);
		} else {
			return Optional.empty();
		}
	}

	public static UserSettings fromJson(JsonObject jo) {
		UserSettings us = new UserSettings();
		us.email = getOptionalBoolean(jo, "email");

		JsonValue _qns = jo.get("questions");
		if(_qns != null && _qns.getValueType() == JsonValue.ValueType.OBJECT) {
			JsonObject qns = (JsonObject)_qns;

			for(String s : qns.keySet()) {
				JsonValue qa = qns.get(s);
				if(qa.getValueType() != JsonValue.ValueType.STRING) {
					continue;
				}

				long id;
				try {
					id = Long.parseUnsignedLong(s);
				} catch(NumberFormatException e) {
					continue;
				}

				us.questions.put(id, qns.getString(s));
			}
		}

		return us;
	}
}
