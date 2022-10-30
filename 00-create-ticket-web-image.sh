#!/bin/sh

cd /home/dlederer/Development/mcg/ticketz-web/docker

oc new-project ticketz

oc new-build . --image=registry.access.redhat.com/ubi8/ubi-minimal --name=ticketz-web --strategy=docker -n ticketz

sleep 5

oc start-build ticketz-web --from-dir . --follow -n ticketz
