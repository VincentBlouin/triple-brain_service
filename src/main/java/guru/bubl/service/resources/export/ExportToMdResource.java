package guru.bubl.service.resources.export;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.ExportToMarkdown;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.ExportToMarkdownFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Consumes(MediaType.APPLICATION_JSON)
public class ExportToMdResource {

    private ExportToMarkdown exportToMarkdown;

    @AssistedInject
    public ExportToMdResource(
            ExportToMarkdownFactory exportToMarkdownFactory,
            @Assisted String username
    ) {

        this.exportToMarkdown = exportToMarkdownFactory.withUsername(username);
    }

    @POST
    @Path("/")
    public Response get() {
//        Thread thread = new Thread() {
//            public void run() {
//                exportToMarkdown.export();
//            }
//        };
//        thread.start();
//        return Response
//                .ok().build();
        File file = exportToMarkdown.export();
        StreamingOutput fileStream = new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
                try {
                    java.nio.file.Path path = Paths.get(file.getAbsolutePath());
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + file.getName())
                .build();
    }

}
