#!/bin/sh

find ./ -name "*.class" -type f -exec rm -f \{\} \;

javac *.java
javac Sensors/*.java
javac Controllers/*.java