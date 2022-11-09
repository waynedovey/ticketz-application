#!/bin/sh

# Maven compile
cd /home/dlederer/Development/mcg/ticketz-web
mvn -DskipTests clean package -Dnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman

# Build image in OCP
cd /home/dlederer/Development/mcg/ticketz-web/docker
cp ../target/ticketz-web-runner target/ && oc start-build ticketz-web --from-dir . --follow -n ticketz

# Build image via podman and upload
podman build . -t=ticketz-web
podman push localhost/ticketz-web:latest quay.io/dlederer/ticketz-web:latest

