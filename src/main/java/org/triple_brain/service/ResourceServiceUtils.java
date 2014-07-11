package org.triple_brain.service;

import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.*;
import org.triple_brain.module.model.graph.FriendlyResourceOperator;
import org.triple_brain.module.model.graph.FriendlyResourcePojo;
import org.triple_brain.module.model.graph.Identification;
import org.triple_brain.module.model.graph.IdentificationPojo;
import org.triple_brain.module.model.json.FriendlyResourceJson;
import org.triple_brain.module.model.json.IdentificationJson;

import javax.inject.Inject;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class ResourceServiceUtils {

    @Inject
    GraphTransaction graphTransaction;

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    public Observer imagesUpdateHandler = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            FreebaseFriendlyResource freebaseFriendlyResource = (FreebaseFriendlyResource) observable;
            IdentificationPojo resourceCached = freebaseFriendlyResource.getCachedFriendlyResource();
            Set<Image> images = (Set<Image>) o;
            FriendlyResourceOperator updatedResource;
            Object state = graphTransaction.before();
            try {
                updatedResource = friendlyResourceFactory.withUri(
                        resourceCached.uri()
                );
                updatedResource.addImages(
                        images
                );
                resourceCached.images().addAll(images);
                BayeuxInitializer.notificationService.notifyChannelMessage(
                        "/identification/" +
                                Uris.encodeURL(resourceCached.getExternalResourceUri()) +
                                "/updated",
                        IdentificationJson.toJson(
                                resourceCached
                        )
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                graphTransaction.after(state);
            }
        }
    };

    public Observer descriptionUpdateHandler = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            FreebaseFriendlyResource freebaseFriendlyResource = (FreebaseFriendlyResource) observable;
            IdentificationPojo resourceCached = freebaseFriendlyResource.getCachedFriendlyResource();
            String description = (String) o;
            Object state = graphTransaction.before();
            FriendlyResourceOperator updatedResource;
            try {
                updatedResource = friendlyResourceFactory.withUri(
                        resourceCached.uri()
                );
                updatedResource.comment(
                        description
                );
                resourceCached.setComment(description);
                BayeuxInitializer.notificationService.notifyChannelMessage(
                        "/identification/" +
                                Uris.encodeURL(resourceCached.uri()) +
                                "/updated",
                        IdentificationJson.toJson(
                                resourceCached
                        )
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                graphTransaction.after(state);
            }
        }
    };
}
