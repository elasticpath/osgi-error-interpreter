#!/bin/bash
set -e
mvn clean install
sls deploy

