/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import java.net.URI;

public class ServiceUtils {
    public static String usernameInURI(URI uri){
        return uri.getPath().split("\\/")[1];
    }
}
