package org.triple_brain.service;

import java.net.URI;

/*
* Copyright Mozilla Public License 1.1
*/
public class ServiceUtils {
    public static String usernameInURI(URI uri){
        return uri.getPath().split("\\/")[1];
    }
}
