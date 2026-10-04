#!/bin/bash

./gradlew clean distZip

HOST=yannick@mitterweg7
DIR=/home/yannick/mitterweg7
ZIP=/home/yannick/IdeaProjects/weberhome/charging/build/distributions/charging-0.4.0.zip
SOCK=/tmp/ssh-mux-$$
OPTS="-o ControlPath=$SOCK"

# Open the master connection (the only login)
ssh $OPTS -o ControlMaster=yes -o ControlPersist=60 -fN $HOST

ssh $OPTS $HOST "mkdir -p $DIR"
scp $OPTS "$ZIP" $HOST:$DIR/
ssh $OPTS $HOST "unzip -o $DIR/charging-0.4.0.zip -d $DIR"

# Close the master connection
ssh $OPTS -O exit $HOST