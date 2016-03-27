/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.schema.Schema;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.module.model.json.graph.SchemaJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SchemasResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void ok_status_when_listing_schemas() {
        assertThat(
                listClientResponse().getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_list_schemas() {
        Set<SchemaPojo> schemas = list();
        assertThat(
                schemas.size(),
                is(0)
        );
        schemaUtils().createSchema();
        schemas = list();
        assertThat(
                schemas.size(),
                is(1)
        );
    }

    protected Set<SchemaPojo> list() {
        return SchemaJson.listFromJson(
                listClientResponse().getEntity(
                        String.class
                )
        );
    }

    protected ClientResponse listClientResponse() {
        return resource
                .path("service")
                .path("schemas")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }

}
