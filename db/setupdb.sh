#!/bin/sh

echo 'use seats-db
db.seats.insertOne( {"seatId": NumberInt(1), "customerId": NumberInt(0), "state": "available", "category": "A", "timestamp": "2022"})
db.seats.createIndex( { "seatId": 1 } )
db.seats.createIndex( { "state": 1 } )' | \
    mongo -u admin -p admin --authenticationDatabase admin

