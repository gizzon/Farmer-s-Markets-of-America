#!/bin/bash

rm -r $CATALINA_HOME/webapps/Homework5.war $CATALINA_HOME/webapps/Homework5
cp war/Homework5.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
