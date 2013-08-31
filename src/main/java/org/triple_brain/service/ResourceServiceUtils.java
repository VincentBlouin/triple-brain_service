package org.triple_brain.service;

import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.*;
import org.triple_brain.module.model.json.ExternalResourceJson;

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
            FreebaseExternalFriendlyResource freebaseExternalFriendlyResource = (FreebaseExternalFriendlyResource) observable;
            FriendlyResource resource = freebaseExternalFriendlyResource.get();
            Set<Image> images = (Set<Image>) o;
            Object state = beforeAfterEachRestCall.before();
            try {
                resource.addImages(
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
                            Uris.encodeURL(resource.uri()) +
                            "/updated",
                    ExternalResourceJson.get(
                            resource
                    )
            );
        }
    };

    public Observer descriptionUpdateHandler = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            FreebaseExternalFriendlyResource freebaseExternalFriendlyResource = (FreebaseExternalFriendlyResource) observable;
            FriendlyResource resource = freebaseExternalFriendlyResource.get();
            String description = (String) o;
            Object state = beforeAfterEachRestCall.before();
            try {
                resource.description(
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
                            Uris.encodeURL(resource.uri()) +
                            "/updated",
                    ExternalResourceJson.get(
                            resource
                    )
            );
        }
    };



}
