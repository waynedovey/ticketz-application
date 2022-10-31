#!/bin/sh

cd /home/dlederer/Development/mcg-mk2/kafka-sink/docker

oc new-project ticketz

oc new-build . --image=registry.access.redhat.com/ubi8/ubi-minimal --name=kafka-sink --strategy=docker -n ticketz

sleep 5

#oc start-build ticketz-web --from-dir . --follow -n ticketz
cp ../target/kafka-sink-runner target/ && oc start-build kafka-sink --from-dir . --follow -n ticketz
