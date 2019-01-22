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

public class UserInfo {

	public UserInfo(String login, String lname, String fname, String email, String phone, String bcode, String affiliation, String unitlogin, boolean mustchpwd, boolean mustchbcode, boolean active, int userid) {
		this.login = login;
		this.lname = lname;
		this.fname = fname;
		this.email = email;
		this.phone = phone;
		this.bcode = bcode;
		this.affiliation = affiliation;
		this.unitlogin = unitlogin;
		this.mustchpwd = mustchpwd;
		this.mustchbcode = mustchbcode;
		this.active = active;
		this.userid = userid;
	}

	public final String login;
	public final String lname;
	public final String fname;
	public final String email;
	public final String phone;
	public final String bcode;
	public final String affiliation;
	public final String unitlogin;
	public final boolean mustchpwd;
	public final boolean mustchbcode;
	public final boolean active;
	public final int userid;
}
