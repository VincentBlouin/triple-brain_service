/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.identification;

public interface IdentificationResourceFactory {
    IdentificationResource forAuthenticatedUsername(String username);
}
