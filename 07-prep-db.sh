#!/bin/sh

oc project ticketz

POD=$( oc get pods -l deployment=seats-db -o name | sed 's^pod/^^g')

oc cp db/setupdb.sh $POD:/tmp/setupdb.sh
oc rsh $POD /tmp/setupdb.sh
