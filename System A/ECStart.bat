%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java Monitors.ECSConsole %1
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.TemperatureController %1
%ECHO Starting Humidity Controller Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.HumidityController %1
%ECHO Starting Security Controller Console
START "SECURITY CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.SecurityController %1
%ECHO Starting Temperature Sensor Console
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java Sensors.TemperatureSensor %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java Sensors.HumiditySensor %1
%ECHO Starting Window Sensor Console
START "WINDOW SENSOR CONSOLE" /MIN /NORMAL java Sensors.WindowSensor %1
%ECHO Starting Door Sensor Console
START "DOOR SENSOR CONSOLE" /MIN /NORMAL java Sensors.DoorSensor %1
%ECHO Starting Motion Sensor Console
START "MOTION SENSOR CONSOLE" /MIN /NORMAL java Sensors.MotionSensor %1