package br.com.clairtonluz.sicoba.model.pojo;

import br.com.clairtonluz.sicoba.model.entity.security.User;

/**
 * Created by clairton on 27/07/17.
 */
public class UserPojo {
    private Integer id;
    private String name;
    private String username;
    private Boolean enabled;
    private String token;

    public UserPojo(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.enabled = user.getEnabled();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
