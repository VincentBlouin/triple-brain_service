/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.module.model.admin.WholeGraphAdminDailyJob;
import guru.bubl.module.model.graph.GraphTransactional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;

@Path("/daily-job")
public class DailyJobResource {

    @Inject
    WholeGraphAdminDailyJob wholeGraphAdminDailyJob;

    @GET
    @GraphTransactional
    @Path("/")
    public Response doDailyJob(){
        System.out.println("daily job started");
        Instant start = Instant.now();
        try{
            wholeGraphAdminDailyJob.execute();
            System.out.println("daily job finished");
        }catch(Exception e){
            System.out.println("daily job finished with errors");
        }finally{
            Instant end = Instant.now();
            System.out.println(Duration.between(start, end));
        }
        return Response.ok().build();
    }
}
