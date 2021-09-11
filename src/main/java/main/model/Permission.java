package main.model;

public enum Permission {
    USER("user:write"),
    MODERATE("user:moderate");

    private final String permossion;

    Permission(String permission) {
        this.permossion=permission;
    }

    public String getPermossion() {
        return permossion;
    }
}
