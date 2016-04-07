#!/bin/sh

echo "Starting ECS Sensors"
sleep 1

echo "Starting Temperature Controller Console"
java Sensors.TemperatureController &
sleep 1

echo "Starting Humidity Sensor Console"
java Sensors.HumidityController &
sleep 1
java Sensors.TemperatureSensor &
sleep 1

echo "Starting Humidity Sensor Console"
java Sensors.HumiditySensor &
