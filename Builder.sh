#!/bin/bash

javac -cp "WebContent/WEB-INF/lib/*.jar:$CATALINA_HOME/lib/servlet-api.jar" -d build src/Home.java src/ViewAll.java src/Search.java src/ViewSingle.java