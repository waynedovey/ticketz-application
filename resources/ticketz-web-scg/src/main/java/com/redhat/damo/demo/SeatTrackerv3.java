package com.redhat.damo.demo;


import javax.inject.Inject;
import javax.inject.Singleton;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.Projections;

import org.bson.Document;
import org.bson.conversions.Bson;


@Singleton
public class SeatTrackerv3 {

    @Inject MongoClient mongoClient;

    private static String MONGO_DB_NAME = "seats-db";
    private static String MONGO_COLLECTION = "seats";

    private MongoCollection getCollection(){
        return mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_COLLECTION);
    }



    /**
     * Find the first available seat in the category.  The main problem doing it this way is that it's
     * not atomic.  Still, should be fast enough not to cause any problems, even at moderate scale.
     * @param category
     * @return
     */

    public Seat getNextFree(String category) throws Exception {

//        try {Thread.sleep(50);} catch (Exception e) {}

        Bson projectionFields = Projections.fields(
            Projections.include("seatId", "customerId", "state", "category"));
        
        Document doc = (Document) getCollection().find(eq("state", "available"))
            .projection(projectionFields)
            .first();

        if (doc == null) return null;

        Seat seat = new Seat(doc.getInteger("seatId").intValue(),
                             doc.getInteger("customerId").intValue(),
                             doc.getString("state"),
                             doc.getString("category"),
                             null);

        System.out.println("Next free:" + seat.toJsonString());
        return seat;
    }

    public List<Seat> getAllFree() throws Exception {

        List<Seat> retList = new ArrayList<Seat>();
        
        Bson projectionFields = Projections.fields(
            Projections.include("seatId", "customerId", "state", "category"));
        
//        Document doc = (Document) getCollection().find(eq("state", "available"))
//            .projection(projectionFields)
        MongoCursor<Document> cursor = getCollection().find(eq("state", "available"))
                    .projection(projectionFields).iterator();
        try {
            while (cursor.hasNext()) {
                Document thisDoc = cursor.next();
                Seat seat = new Seat(thisDoc.getInteger("seatId").intValue(),
                                     thisDoc.getInteger("customerId").intValue(),
                                     thisDoc.getString("state"),
                                     thisDoc.getString("category"),
                                     null);
                retList.add(seat);
            }
        } finally {
            cursor.close();
        }

        System.out.println("Found " + retList.size() + " free seats");
        return retList;
    }



    /**
     * Fetches the latest state of a given seat
     * @param seatId
     * @return
     */

    public Seat getSeat(int seatId) {

        try {Thread.sleep(100);} catch (Exception e) {}

        Bson projectionFields = Projections.fields(
            Projections.include("seatId", "customerId", "state", "category"));
        
        Document doc = (Document) getCollection().find(eq("seatId", seatId))
            .projection(projectionFields)
            .first();

        if (doc == null) return null;

        Seat seat = new Seat(doc.getInteger("seatId").intValue(),
                             doc.getInteger("customerId").intValue(),
                             doc.getString("state"),
                             doc.getString("category"),
                             null);
        return seat;
    }

    /**
     * Count the number of total free seats
     * @return
     */
    public int countFreeSeats() throws Exception {
        Bson query = eq("state", "available");

        long count = getCollection().countDocuments(query);
        return (int)count;
    }
}
