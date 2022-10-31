package com.redhat.damo.sink;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;
import org.bson.conversions.Bson;

import io.smallrye.reactive.messaging.annotations.Blocking;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ConsumerService {

    @Inject MongoClient mongoClient;

    private static String MONGO_DB_NAME = "seats-db";
    private static String MONGO_COLLECTION = "seats";

    private MongoCollection getCollection(){
        return mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_COLLECTION);
    }

    @Incoming("seats")
    @Blocking         // Let Quarkus/kafka know that we're doing a little bit of processing
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void consume(List<String> seatList) throws Exception {

        long backthenbatch = System.currentTimeMillis();
        System.out.println("Received " + seatList.size() + " records in one batch");

        List<ReplaceOneModel> inserts = new ArrayList<ReplaceOneModel>();
        ReplaceOptions opts = new ReplaceOptions().upsert(true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    
        for (String seatStr : seatList) {

            System.out.println("Record: " + seatStr);

            Seat seat = mapper.readValue(seatStr, Seat.class);

            Document seatDoc = new Document("seatId", Integer.valueOf(seat.getSeatId()))
                                    .append("customerId", Integer.valueOf(seat.getCustomerId()))
                                    .append("state", seat.getState())
                                    .append("category", seat.getCategory())
                                    .append("timestamp", seat.getTimestamp());

            Bson query = Filters.eq("seatId", Integer.valueOf(seat.getSeatId()));

            inserts.add(new ReplaceOneModel<>(query, seatDoc, opts));
        }


        try {
//          BulkWriteResult result = getCollection().bulkWrite(inserts);
          System.out.println(getCollection().bulkWrite(inserts));
//          UpdateResult result = getCollection().replaceOne(query, seatDoc, opts);
          //System.out.println(getCollection().replaceOne(query, seatDoc, opts));
          //System.out.println("Matched document count: " + result.getMatchedCount());
          //System.out.println("Modified document count: " + result.getModifiedCount());
          //System.out.println("Upserted id: " + result.getUpsertedId()); // only contains a value when an upsert is performed
        } catch (MongoException me) {
          System.out.println("Unable to update due to an error: " + me);
        }
        
        System.out.println("Time to write batch: " + (System.currentTimeMillis() - backthenbatch));
    }

}
