package it.globus.finance.rest.dto;

public class SignInRequest {
    private String username;
    private String password;

    public SignInRequest(String login, String password) {
        this.username = login;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setLogin(String login) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
