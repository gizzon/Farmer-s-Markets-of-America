#!/bin/bash

javadoc -cp $CATALINA_HOME/lib/servlet-api.jar -d docs src/Home.java src/ViewAll.java src/Search.java src/ViewSingle.java