#!/bin/bash

oc project ticketz

oc apply -f 000-enable-ACM.yaml
