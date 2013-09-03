package org.triple_brain.service;

import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.*;
import org.triple_brain.module.model.json.FriendlyResourceJson;

import javax.inject.Inject;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class ResourceServiceUtils {
    @Inject
    BeforeAfterEachRestCall beforeAfterEachRestCall;

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    public Observer imagesUpdateHandler = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            FreebaseFriendlyResource freebaseFriendlyResource = (FreebaseFriendlyResource) observable;
            FriendlyResourceCached resourceCached = freebaseFriendlyResource.getCachedFriendlyResource();
            Set<Image> images = (Set<Image>) o;
            FriendlyResource updatedResource = resourceCached;
            Object state = beforeAfterEachRestCall.before();
            try {
                updatedResource = friendlyResourceFactory.createOrLoadFromUri(
                        resourceCached.uri()
                );
                updatedResource.addImages(
                        images
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally{
                beforeAfterEachRestCall.after(state);
            }
            BayeuxInitializer.notificationService.notifyChannelMessage(
                    "/identification/" +
                            Uris.encodeURL(updatedResource.uri()) +
                            "/updated",
                    FriendlyResourceJson.toJson(
                            updatedResource
                    )
            );
        }
    };

    public Observer descriptionUpdateHandler = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            FreebaseFriendlyResource freebaseFriendlyResource = (FreebaseFriendlyResource) observable;
            FriendlyResourceCached resourceCached = freebaseFriendlyResource.getCachedFriendlyResource();
            String description = (String) o;
            Object state = beforeAfterEachRestCall.before();
            FriendlyResource updatedResource = resourceCached;
            try {
                updatedResource = friendlyResourceFactory.createOrLoadFromUri(
                        resourceCached.uri()
                );
                updatedResource.comment(
                        description
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally{
                beforeAfterEachRestCall.after(state);
            }
            BayeuxInitializer.notificationService.notifyChannelMessage(
                    "/identification/" +
                            Uris.encodeURL(updatedResource.uri()) +
                            "/updated",
                    FriendlyResourceJson.toJson(
                            updatedResource
                    )
            );
        }
    };
}
