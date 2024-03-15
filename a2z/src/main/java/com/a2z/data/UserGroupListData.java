package com.a2z.data;

import java.util.List;

import org.springframework.validation.annotation.Validated;

@Validated
public class UserGroupListData extends RootData {

	private List<UserGroupData> userGroups;

	public List<UserGroupData> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroupData> userGroups) {
		this.userGroups = userGroups;
	}
	
}
