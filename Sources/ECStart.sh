#!/bin/sh

echo "Starting ECS Sensors"
sleep 1

echo "Starting Temperature Controller Console"
java TemperatureController &
sleep 1

echo "Starting Humidity Sensor Console"
java HumidityController &
sleep 1
java TemperatureSensor &
sleep 1

echo "Starting Humidity Sensor Console"
java HumiditySensor &
