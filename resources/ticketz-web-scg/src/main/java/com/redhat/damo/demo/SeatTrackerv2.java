package com.redhat.damo.demo;

import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.Stores;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.streams.Topology;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;

//import io.confluent.ksql.api.client.BatchedQueryResult;
//import io.confluent.ksql.api.client.Client;
//import io.confluent.ksql.api.client.ClientOptions;
//import io.confluent.ksql.api.client.Row;
import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection(targets={ io.vertx.core.http.HttpClient.class})
@Singleton
public class SeatTrackerv2 {

//    static final String SEATS_STORE = "seats-store";
//    private static final String SEATS_TOPIC = "seats";

//    private KafkaStreams kafkaStreams;
//    private ReadOnlyKeyValueStore<Integer, String> store;

    public static String KSQLDB_SERVER_HOST = "ksql-server";
    public static int KSQLDB_SERVER_HOST_PORT = 8088;
/*
    public SeatTracker() {
        // System.out.println("SeatTracker initialiser");

        Topology topology = createTopology();
        kafkaStreams = new KafkaStreams(topology, getStreamConfig());
        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            kafkaStreams.close();
        }));

        // This key/value store is a materialised view of the kafka stream.  The code above keeps
        // it up to date when new events appear in the stream.
        store = kafkaStreams.store(fromNameAndType(SEATS_STORE, QueryableStoreTypes.keyValueStore()));

    }

    public Topology createTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(SEATS_STORE);

        // We need to use a Global Table here because otherwise each pod will only see the seats in one partition
        GlobalKTable<Integer, String> seatsTable = builder.globalTable( 
                SEATS_TOPIC,
                Consumed.with(Serdes.Integer(), Serdes.String()),
                Materialized.as(storeSupplier));

  	    return builder.build();
    }
*/
    /**
     * Find the first available seat in the category.  The main problem doing it this way is that it's
     * not atomic.  Still, should be fast enough not to cause any problems, even at moderate scale.
     * @param category
     * @return
     */
/*
    public Seat getNextFree(String category) {

        // System.out.println("Finding next available seat...");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        try {
            // Find first available seat
            KeyValueIterator<Integer, String> it = store.all();
            while (it.hasNext()) {

                String value = it.next().value;
                Seat seat = mapper.readValue(value, Seat.class);
                if (seat.isAvailable() && seat.getCategory().equals(category)) {
                    return seat;
                }
            }

            // If we're here, then we didn't find any seats
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
*/

    /**
     * Fetches the latest state of a given seat
     * @param seatId
     * @return
     */
/*
    public Seat getSeat(int seatId) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        try {
            String value = store.get(Integer.valueOf(seatId));
            if (value != null) {
                Seat seat = mapper.readValue(value, Seat.class);
                return seat;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    /**
     * Count the number of total free seats
     * @return
     */
//    public int countFreeSeats() {
//        // Find first available seat
//        KeyValueIterator<Integer, String> it = store.all();
//        int counter = 0;
//        while (it.hasNext()) {
//            // Don't bother casting it to a JSON object, just do a straight text search
//            if (it.next().value.indexOf("available") != -1) counter++;
//        }
//        return counter;
//    }

    /**
     * Standard Kafka client properties
     * @return
     */
    private static Properties getStreamConfig() {
        Properties props=new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable-application");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }
/*
    public void runSomething() throws Exception {
      ClientOptions options = ClientOptions.create()
          .setHost(KSQLDB_SERVER_HOST)
          .setPort(KSQLDB_SERVER_HOST_PORT);
      Client client = Client.create(options);
  
      // Send requests with the client by following the other examples
      String sql1 = "SELECT seat_id, state FROM available_seats;";
      BatchedQueryResult result = client.executeQuery(sql1);
  
      List<Row> resultRows = result.get();
      System.out.println("Received results. Num rows: " + resultRows.size());
      for (Row row : resultRows) {
          System.out.println("Row: " + row.values());
          System.out.println("First seatId: " + row.values().getInteger(0));
      }
      System.out.println("Done");
      
      // Terminate any open connections and close the client
      client.close();
    }
*/

    public int getNextFree(String category) throws Exception {
        String query = "{ \"ksql\": \"SELECT seat_id FROM available_seats LIMIT 1;\", \"streamsProperties\": {} }";
        String result = HTTPGet.Post("http://" + KSQLDB_SERVER_HOST + ":" + KSQLDB_SERVER_HOST_PORT + "/query", query);
        System.out.println("Result: " + result);

        if (result == null ) return -1;

        // There should only be 2 lines outputted here.  We want the second line
        String[] lines = result.split("\r\n|\r|\n");
        if (lines.length <2) {
            System.out.println("Not returned 2 lines!");
        }

        // Strip out the seat id.  Really should be a JSON parse, but hey, it works
        String str = lines[1];
        int start = str.indexOf("[");
        int end = str.indexOf("]");
        String seatIdStr = str.substring(start + 1, end);

        System.out.println("Found: " + seatIdStr);

        return Integer.parseInt(seatIdStr);

    }

    public int countFreeSeats() throws Exception {
      String query = "{ \"ksql\": \"SELECT seat_id FROM available_seats;\", \"streamsProperties\": {} }";
      String result = HTTPGet.Post("http://" + KSQLDB_SERVER_HOST + ":" + KSQLDB_SERVER_HOST_PORT + "/query", query);
      System.out.println("Result: " + result.lines().count());
      return (int)result.lines().count() - 1;
  }

    public int reserve(int seatId) throws Exception {
        String query = "{ \"ksql\": \"INSERT INTO seats (seat_id, seatId, customerId, state, category, last_updated) VALUES (" + seatId + ", " + seatId + ", 0, 'reserved', 'A', '2021-09-23');\", \"streamsProperties\": {} }";
        String result = HTTPGet.Post("http://" + KSQLDB_SERVER_HOST + ":" + KSQLDB_SERVER_HOST_PORT + "/ksql", query);
        System.out.println("Result: " + result.lines().count());
        return 1;
    }

    public int createEmpty(int seatId) throws Exception {
      String query = "{ \"ksql\": \"INSERT INTO seats (seat_id, seatId, customerId, state, category, last_updated) VALUES (" + seatId + ", " + seatId + ", 0, 'available', 'A', '2021-09-23');\", \"streamsProperties\": {} }";
      String result = HTTPGet.Post("http://" + KSQLDB_SERVER_HOST + ":" + KSQLDB_SERVER_HOST_PORT + "/ksql", query);
      System.out.println("Result: " + result.lines().count());
      return 1;
  }

}
