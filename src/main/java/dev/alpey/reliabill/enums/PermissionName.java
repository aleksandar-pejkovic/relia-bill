package dev.alpey.reliabill.enums;

import org.springframework.security.core.GrantedAuthority;

public enum PermissionName implements GrantedAuthority {

    READ,
    WRITE,
    UPDATE,
    DELETE,
    GRANT_ADMIN,
    READ_EVENTS;

    @Override
    public String getAuthority() {
        return "SCOPE_" + name();
    }
}
