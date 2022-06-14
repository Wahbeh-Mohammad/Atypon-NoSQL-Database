package com.atypon.read_controller.models.types;

import org.json.JSONObject;

import java.util.Objects;

public class User {
    private final String username, password;
    private UserRole role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        if(role != null)
            this.role = UserRole.fromString(role);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return this.role.equals(UserRole.ADMIN);
    }

    public static User fromJSON(JSONObject jsonUser) {
        String username = jsonUser.getString("username"),
                password = jsonUser.getString("password"),
                role = jsonUser.getString("role");
        return new User(username, password, role);
    }

    public JSONObject toJSON() {
        String rawJSON = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"role\":\""+ role.toString() +"\"}";
        return new JSONObject(rawJSON);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(username, user.username)) return false;
        if (!Objects.equals(password, user.password)) return false;
        return role.equals(user.role);
    }

    @Override
    public int hashCode() {
        // 31x + y
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result & 0x7fffffff;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }
}