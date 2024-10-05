package com.bem.onlineshopping.model;

public enum OrderStatusEnum {
    NOT_SHIPPED("Not Shipped"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    RETURNED("Returned");

    private final String displayName;

    OrderStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
