package org.lokfid;

import org.cef.network.CefCookie;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Doogie13
 * @since 21/08/2024
 */
public class SerialisableCefCookie implements Serializable {

    private final String name;
    private final String value;
    private final String domain;
    private final String path;
    private final boolean secure;
    private final boolean httponly;
    private final Date creation;
    private final Date lastAccess;
    private final boolean hasExpires;
    private final Date expires;

    public SerialisableCefCookie(CefCookie cookie) {
        this.name = cookie.name;
        this.value = cookie.value;
        this.domain = cookie.domain;
        this.path = cookie.path;
        this.secure = cookie.secure;
        this.httponly = cookie.httponly;
        this.creation = cookie.creation;
        this.lastAccess = cookie.lastAccess;
        this.hasExpires = cookie.hasExpires;
        this.expires = cookie.expires;
    }

    public CefCookie getCookie() {
        return new CefCookie(name, value, domain, path, secure, httponly, creation, lastAccess, hasExpires, expires);
    }

}
