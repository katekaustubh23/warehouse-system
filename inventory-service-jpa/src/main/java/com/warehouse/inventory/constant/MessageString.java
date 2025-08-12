package com.warehouse.inventory.constant;

public enum MessageString {

	FETCH_ALL_DATA("Fetched all data"),
	CREATED_DATA("Created data"),
	UPDATED_DATA("Updated data"),
	FETCHED_DATA("Fetch related data"),
	DELETED_DATA("Selected data deleted");
	
	private final String displayName;

	MessageString(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
        return displayName;
    }
}
