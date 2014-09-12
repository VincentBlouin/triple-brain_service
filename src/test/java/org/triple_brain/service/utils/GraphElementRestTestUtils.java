/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.ModelTestScenarios;
import org.triple_brain.module.model.json.IdentificationJson;
import org.triple_brain.service.resources.GraphElementIdentificationResource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.net.URI;

public class GraphElementRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;
    private Gson gson = new Gson();

    public static GraphElementRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser){
        return new GraphElementRestTestUtils(resource, authCookie, authenticatedUser);
    }
    protected GraphElementRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser){
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
    }

    public ClientResponse addIdentificationToGraphElementWithUri(JSONObject identification, URI graphElementUri) {
        return resource
                .path(graphElementUri.getPath())
                .path("identification")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, identification);
    }

    public ClientResponse addFoafPersonTypeToVertexA() {
        try {
            JSONObject personType = IdentificationJson.toJson(new ModelTestScenarios().personType()).put(
                    GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                    GraphElementIdentificationResource.identification_types.TYPE
            ).put(
                    "externalResourceUri",
                    "http://xmlns.com/foaf/0.1/Person"
            )
                    .put(
                            GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                            GraphElementIdentificationResource.identification_types.TYPE
                    );
            return addIdentificationToGraphElementWithUri(
                    personType,
                    graphUtils().vertexAUri()
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public URI identificationUriFromResponse(ClientResponse response) {
        URI responseUri = URI.create(response.getHeaders().get("Location").get(0));
        UserUris userUris = new UserUris(authenticatedUser);
        return URI.create(userUris.baseIdentificationUri() + "/" + UserUris.graphElementShortId(
                responseUri
        ));
    }

    private GraphRestTestUtils graphUtils(){
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }
}
