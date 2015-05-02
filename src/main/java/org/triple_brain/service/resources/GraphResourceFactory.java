/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources;

import org.triple_brain.module.model.User;

public interface GraphResourceFactory {
    public GraphResource withUser(User user);
}
