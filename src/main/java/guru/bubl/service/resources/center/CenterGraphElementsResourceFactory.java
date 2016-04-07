/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import guru.bubl.module.model.User;

public interface CenterGraphElementsResourceFactory {
    CenterGraphElementsResource forUser(User user);
}
