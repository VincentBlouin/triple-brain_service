/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.identification;

import guru.bubl.module.model.User;

public interface IdentificationResourceFactory {
    IdentificationResource forAuthenticatedUser(User user);
}
