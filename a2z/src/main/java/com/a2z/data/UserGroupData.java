package com.a2z.data;

import java.util.List;

import org.springframework.validation.annotation.Validated;

@Validated
public class UserGroupData extends RootData {

	private String userGroupName ;
	private List<String> userNames;
	private String role;
	
	public String getUserGroupName() {
		return userGroupName;
	}
	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}
	public List<String> getUserNames() {
		return userNames;
	}
	public void setUserNames(List<String> userNames) {
		this.userNames = userNames;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
}
