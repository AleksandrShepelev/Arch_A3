%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO Mainteinance Monitor
START "MUSEUM MAINTEINANCE MONITOR" /NORMAL java Monitors.MaintenanceMonitor %1
%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java Monitors.ECSConsole %1
%ECHO ECS Monitoring Console
START "MUSEUM SECURITY CONSOLE" /NORMAL java Monitors.SecurityConsole %1
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.TemperatureController %1
%ECHO Starting Humidity Controller Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.HumidityController %1
%ECHO Starting Security Controller Console
START "SECURITY CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.SecurityController %1
%ECHO Starting Sprinkler Controller Console
START "SPRINKLER CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.SprinklerController %1
%ECHO Starting Fire Controller Console
START "FIRE CONTROLLER CONSOLE" /MIN /NORMAL java Controllers.FireController %1
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
%ECHO Starting Fire Sensor Console
START "FIRE SENSOR CONSOLE" /MIN /NORMAL java Sensors.FireSensor %1