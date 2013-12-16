package roundrobin.database.resources;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import roundrobin.database.StatManager;
import roundrobin.database.pojos.StatResponse;



@Path("/statistics")
public class StatResource {
		
	private StatManager statMngr = StatManager.getInstance();
	@GET
	@Path("/stats/{itemId}/{statParameter}/{typeOfStat}/{granularity}")
	@Produces(MediaType.STATS)
	public StatResponse getStat(@PathParam("itemId") String itemId,
			@PathParam("statParameter") String statParameter,
			@PathParam("typeOfStat") String typeOfStat,
			@PathParam("granularity") String granularity) throws IOException {
			
			return statMngr.getStat(itemId, statParameter, typeOfStat, granularity);
	}
    
	  
	


}
