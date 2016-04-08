cd .. &&
javac *.java
javac Sensors/*.java
killall rmiregistry &
sleep 1
rmiregistry &
java MessageManager

#lsof -i :1099