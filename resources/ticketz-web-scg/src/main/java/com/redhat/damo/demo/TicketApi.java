package com.redhat.damo.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.kafka.Record;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;


@ApplicationScoped
@Path("/")
public class TicketApi {

//    private static int SEATS_IN_CATEGORY = 25006;
//    private static int SEATS_IN_CATEGORY = 10;
    private static int MAX_SEATS = 200000;
    private static List<String> CATEGORIES = Arrays.asList("A", "B", "C", "D");

    // I know, I know, should be an enum
    private static String SEAT_AVAILABLE = "available";
    private static String SEAT_RESERVED  = "reserved";
    private static String SEAT_PURCHASED = "purchased";

    @Inject @Channel("seats")
    //Emitter<Record<Integer, String>> emitter;
    Emitter<String> emitter;


    @Inject
    SeatTrackerv3 seatTracker;

    /**
     * Seeds the Kafka stream with all of the empty seats.  We're treating the Kafka stream
     * like a database, so this seeding is necessary to define all of the seats.
     * @return
     * @throws Exception
     */
    @GET
    @Path("/seed/{num}")
    public Response seed(@PathParam("num") int num) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        if (num < 1 || num > MAX_SEATS) {
            System.out.println("Attempting to seed with " + num);
            System.out.println("Cmon, be realistic!");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        int seeded=0;
        int perCategory = num / CATEGORIES.size();

        for (int i=0; i<perCategory; i++) {
            for (String category: CATEGORIES) {
                Seat seat = new Seat(++seeded, 0, SEAT_AVAILABLE, category, Instant.now().toString());
                System.out.println("Seat obj: " + seat.toJsonString());
                // emitter.send(Record.of(Integer.valueOf(seeded), seat.toJsonString()));
                emitter.send(seat.toJsonString());
            }
        }

        System.out.println("Done");
        return Response.ok("Seeded: " + seeded).build();
    }

    // The client can reserve a seat in the nominated category
    @GET
    @Path("/reserve/{category}")
    public Response reserveSeats(@PathParam("category") String category) throws Exception {

        // Call the seat tracking system (updated via Kafka) and fetch the first seat in state 'available'
        Seat seat = seatTracker.getNextFree(category);
        if (seat != null) {
            seat.setState(SEAT_RESERVED);
            seat.setTimestamp(Instant.now().toString());

            emitter.send(seat.toJsonString());
            return Response.ok(seat.getSeatId() + "").build();
        }
        // If no seat was available, then let the client know
        System.out.println("  -> No free seats");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // The client can reserve a seat in the nominated category
    @GET
    @Path("/reserveX/{num}")
    public Response reserveX(@PathParam("num") int num) throws Exception {

        // Grab the first available seat
        System.out.println("Reserving " + num + " seats...");
        Seat seat = seatTracker.getNextFree("A");
        int starting = 1;
        if (seat != null) {
            starting = seat.getSeatId();
        }
        System.out.println(  "Starting at seat num: " + starting);
        seat.setState(SEAT_RESERVED);
        seat.setTimestamp(Instant.now().toString());     

        for (int i=starting; i < num+starting; i++) {
            seat.setSeatId(i);
            emitter.send(seat.toJsonString());
        }
        // If no seat was available, then let the client know
        return Response.ok("Reserved: " + num).build();
    }


    // The client can reserve a seat in the nominated category
    @GET
    @Path("/reservenum/{seatId}")
    public Response reserveSeats(@PathParam("seatId") int seatId) throws Exception {
    
        // Call the seat tracking system (updated via Kafka) and fetch the first seat in state 'available'
        Seat seat = seatTracker.getSeat(seatId);
        if (seat != null) {
            seat.setState(SEAT_RESERVED);
            seat.setTimestamp(Instant.now().toString());
    
            emitter.send(seat.toJsonString());
            return Response.ok(seat.getSeatId() + "").build();
        }
        // If no seat was available, then let the client know
        System.out.println("  -> No free seats");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

 
    // Once a seat is reserved, then we can purchase it
    @GET
    @Path("/purchase/{seatId}")
    public Response purchaseOneSeat(@PathParam("seatId") int seatId) {
        System.out.println("Purchasing: " + seatId);
        Seat seat = seatTracker.getSeat(seatId);
        //Seat seat = new Seat(seatId, 0, SEAT_PURCHASED, "A", Instant.now().toString());
        seat.setState(SEAT_PURCHASED);
        seat.setTimestamp(Instant.now().toString());
        //emitter.send(Record.of(Integer.valueOf(seatId), seat.toJsonString()));
        emitter.send(seat.toJsonString());

        return Response.ok(seatId + "").build();
    }
    
    // Determine how many seats are still available
    @GET
    @Path("/free")
    public Response free() throws Exception {
        return Response.ok(seatTracker.countFreeSeats()).build();
    }


    // The client can reserve a seat in the nominated category
    @GET
    @Path("/reserveall")
    public Response reserveAllSeats() throws Exception {

        System.out.println("Reserving every single free seat");

        List<Seat> allFree = seatTracker.getAllFree();

        for (Seat seat: allFree) {
            seat.setState(SEAT_RESERVED);
            seat.setTimestamp(Instant.now().toString());
            System.out.println("Seat obj: " + seat.toJsonString());
                // emitter.send(Record.of(Integer.valueOf(seeded), seat.toJsonString()));
            emitter.send(seat.toJsonString());
        }

        return Response.ok("Reserved every single seat (" + allFree.size() + ")").build();
    }


    @GET
    @Produces("text/html")
    @Path("/innerBit")
    public Response innerBit() throws Exception {
        return Response.ok("<h1>Available: " + seatTracker.countFreeSeats() + "</h1>").build();
    }

}
