/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import java.net.URI;

public class ServiceUtils {
    public static String usernameInURI(URI uri){
        return uri.getPath().split("\\/")[1];
    }
}
