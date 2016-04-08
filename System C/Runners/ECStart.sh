#!/bin/sh

echo "Starting ECS Sensors"
sleep 1

echo "Starting Temperature Controller Controller"
java Controllers.TemperatureController &
sleep 1

java Sensors.TemperatureSensor &
sleep 1

echo "Starting Humidity Sensor Controller"
java Controllers.HumidityController &
sleep 1

java Sensors.HumiditySensor &

echo "Starting Security Controller"
java Controllers.SecurityController &
sleep 1

java Sensors.DoorSensor &
java Sensors.MotionSensor &
java Sensors.WindowSensor &
