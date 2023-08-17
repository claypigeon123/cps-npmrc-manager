#!/bin/bash

cd /opt/build
mvn -Pnative clean package
cp target/npmrcm /opt/output/npmrcm