package org.cosmiccoders.api.dao;


import org.cosmiccoders.api.model.Role;

import java.util.List;

public interface UserWithRoles {
    Long getId();
    String getUsername();
    String getEmail();
    String getPassword();
    List<Role> getRoles();
}
