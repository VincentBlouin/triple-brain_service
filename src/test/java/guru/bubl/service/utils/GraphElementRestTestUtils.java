/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.model.graph.Identification;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.graph.IdentificationType;
import guru.bubl.test.module.utils.ModelTestScenarios;
import guru.bubl.module.model.json.IdentificationJson;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.util.Map;

public class GraphElementRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;
    private Gson gson = new Gson();

    public static GraphElementRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        return new GraphElementRestTestUtils(resource, authCookie, authenticatedUser);
    }

    protected GraphElementRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
    }

    public ClientResponse addIdentificationToGraphElementWithUri(IdentificationPojo identification, URI graphElementUri) {
        return addIdentificationToGraphElementWithUri(
                IdentificationJson.singleToJson(identification),
                graphElementUri
        );
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
        IdentificationPojo identification = new ModelTestScenarios().person();
        identification.setType(IdentificationType.type);
        JSONObject personType = IdentificationJson.singleToJson(identification);
        return addIdentificationToGraphElementWithUri(
                personType,
                graphUtils().vertexAUri()
        );

    }


    public IdentificationPojo getIdentificationFromResponse(ClientResponse response) {
        return IdentificationJson.singleFromJson(
                response.getEntity(String.class)
        );
    }

    public URI identificationUriFromResponse(ClientResponse response) {
        URI responseUri = URI.create(response.getHeaders().get("Location").get(0));
        UserUris userUris = new UserUris(authenticatedUser);
        return URI.create(userUris.baseIdentificationUri() + "/" + UserUris.graphElementShortId(
                responseUri
        ));
    }

    private GraphRestTestUtils graphUtils() {
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }
}
