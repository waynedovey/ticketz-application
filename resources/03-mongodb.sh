#!/bin/sh

oc new-app \
    -e MONGODB_USER=seats \
    -e MONGODB_PASSWORD=seats \
    -e MONGODB_DATABASE=seats-db \
    -e MONGODB_ADMIN_PASSWORD=admin \
    --name=seats-db \
    -n ticketz \
    registry.redhat.io/rhscl/mongodb-36-rhel7
