#!/bin/sh

# Maven compile
cd /home/dlederer/Development/mcg-mk2/ticketz-web-scg
mvn -DskipTests clean package -Dnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman

# Build image in OCP
cd /home/dlederer/Development/mcg-mk2/ticketz-web-scg/docker
cp ../target/ticketz-web-scg-runner target/ && oc start-build ticketz-web-scg --from-dir . --follow -n ticketz

# Build image via podman and upload
podman build . -t=ticketz-web-scg
podman push localhost/ticketz-web-scg:latest quay.io/dlederer/ticketz-web-scg:latest

