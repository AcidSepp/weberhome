#!/bin/bash

./gradlew clean distZip

HOST=yannick@mitterweg7
DIR=/home/yannick/mitterweg7
CHARGING_ZIP=/home/yannick/IdeaProjects/weberhome/charging/build/distributions/charging-0.4.0.zip
SOLAR_METRICS_ZIP=/home/yannick/IdeaProjects/weberhome/solarMetrics/build/distributions/solarMetrics-0.8.0.zip
WEBSITE_DIR=/home/yannick/IdeaProjects/weberhome/website
SOCK=/tmp/ssh-mux-$$
OPTS="-o ControlPath=$SOCK"

# Open the master connection (the only login)
ssh $OPTS -o ControlMaster=yes -o ControlPersist=60 -fN $HOST

ssh $OPTS $HOST "mkdir -p $DIR"
scp $OPTS "$CHARGING_ZIP" $HOST:$DIR/
scp $OPTS "$SOLAR_METRICS_ZIP" $HOST:$DIR/
scp $OPTS -r "$WEBSITE_DIR" $HOST:$DIR/
ssh $OPTS $HOST "unzip -o $DIR/charging-0.4.0.zip -d $DIR"
ssh $OPTS $HOST "unzip -o $DIR/solarMetrics-0.8.0.zip -d $DIR"

# Close the master connection
ssh $OPTS -O exit $HOST