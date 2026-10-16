#!/bin/bash

./gradlew clean installDist

HOST=yannick@mitterweg7
DIR=/home/yannick/mitterweg7
CHARGING_DIR=/home/yannick/IdeaProjects/weberhome/charging/build/install/charging
SOLAR_METRICS_DIR=/home/yannick/IdeaProjects/weberhome/solarMetrics/build/install/solarMetrics
WEBSITE_DIR=/home/yannick/IdeaProjects/weberhome/website
SOCK=/tmp/ssh-mux-$$
OPTS="-o ControlPath=$SOCK"

# Open the master connection (the only login)
ssh $OPTS -o ControlMaster=yes -o ControlPersist=60 -fN $HOST

ssh $OPTS $HOST "mkdir -p $DIR"
scp $OPTS -r "$CHARGING_DIR" $HOST:$DIR/
scp $OPTS -r "$SOLAR_METRICS_DIR" $HOST:$DIR/
scp $OPTS -r "$WEBSITE_DIR" $HOST:$DIR/

# Close the master connection
ssh $OPTS -O exit $HOST