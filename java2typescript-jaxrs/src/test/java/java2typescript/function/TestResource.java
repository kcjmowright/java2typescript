package java2typescript.function;

import java.util.UUID;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/domain/{in}/test")
public interface TestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  AggregationResultDTO findEvents(@PathParam("in") int in, @BeanParam() TestDTO search, @BeanParam PagingSortDTO s);

  @Path("/all")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  SearchResultDTO<AggregationResultDTO> findAll(@PathParam("in") int domainId,
      @BeanParam TestDTO search, @QueryParam("includeParts") @DefaultValue("true") boolean includeParts,
      @QueryParam("includeCounts") @DefaultValue("false") boolean includeCounts);

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  AggregationResultDTO create(@PathParam("in") int in, AggregationResultDTO dto);

  @PUT
  @Path("/{id}")
  @Consumes({MediaType.APPLICATION_JSON})
  void update(@PathParam("in") int domainId, @PathParam("id") UUID id, TestDTO dto);

  @DELETE
  @Path("/{id}")
  void delete(@PathParam("in") int domainId, @PathParam("id") UUID id);

  @Path("/in")
  @GET
  void in(@PathParam("in") int in);

}
