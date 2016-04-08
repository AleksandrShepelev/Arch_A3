/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Monitors;

import Framework.BaseConsole;

public class ECSConsole extends BaseConsole {

    private static final String SET_TEMPERATURE_RANGE = "1";
    private static final String SET_HUMIDITY_RANGE = "2";

    protected void initMonitor(String args[]) {
        _monitor = new ECSMonitor(args);
    }

    public static void main(String args[]) {
        ECSConsole console = new ECSConsole();
        console.initMonitor(args);
        console.execute();
    } // main

    @Override
    protected void initStartMenu() {
        ECSMonitor monitor = (ECSMonitor)_monitor;

        System.out.println("\n\n\n\n");
        System.out.println("Environmental Control System (ECS) Command Console: \n");
        System.out.println("Using message manger at: " + monitor.getManagerAddress() + "\n");

        System.out.println("Set Temperature Range: " + monitor.getTempRangeLow() + "F - " + monitor.getTempRangeHigh() + "F");
        System.out.println("Set Humidity Range: " + monitor.getHumiRangeLow() + "% - " + monitor.getHumiRangeHigh() + "%\n");
        System.out.println("Select an Option: \n");
        System.out.println(SET_TEMPERATURE_RANGE + ": Set temperature ranges");
        System.out.println(SET_HUMIDITY_RANGE + ": Set humidity ranges");
        System.out.println(STOP_SYSTEM + ": Stop System\n");
        System.out.print("\n>>>> ");
    }

    private float getUserInputData(String helloMessage) {
        boolean error = true;
        String option;
        float result = 0;
        while (error) {
            System.out.print(helloMessage);
            option = _input.KeyboardReadString();

            if (_input.IsNumber(option)) {
                error = false;
                result = Float.valueOf(option);
            } else {
                System.out.println("Not a number, please try again...");
            } // if
        } // while

        return result;
    }

    private void handleTemperatureRange() {
        // Here we get the temperature ranges
        boolean error = true;
        while (error) {
            // Here we get the low temperature range
            float tempRangeLow = getUserInputData("\nEnter the low temperature>>> ");

            // Here we get the high temperature range
            float tempRangeHigh = getUserInputData("\nEnter the high temperature>>> ");

            if (tempRangeLow >= tempRangeHigh) {
                System.out.println("\nThe low temperature range must be less than the high temperature range...");
                System.out.println("Please try again...\n");
                error = true;
            } else {
                error = false;
                ECSMonitor monitor = (ECSMonitor)_monitor;
                monitor.setTemperatureRange(tempRangeLow, tempRangeHigh);
            } // if
        } // while
    }

    private void handleHumidityRange() {
        // Here we get the temperature ranges
        boolean error = true;
        while (error) {
            // Here we get the low temperature range
            float humidityLow = getUserInputData("\nEnter the low humidity>>> ");

            // Here we get the high temperature range
            float humidityHigh = getUserInputData("\nEnter the high humidity>>>  ");

            if (humidityLow >= humidityHigh) {
                System.out.println("\nThe low humidity range must be less than the high humidity range...");
                System.out.println("Please try again...\n");
                error = true;
            } else {
                error = false;
                ECSMonitor monitor = (ECSMonitor)_monitor;
                monitor.setHumidityRange(humidityLow, humidityHigh);
            } // if
        } // while
    }

    @Override
    protected void handleUserInput(String option) {
        if (option.equals(SET_TEMPERATURE_RANGE)) {
            handleTemperatureRange();
        }

        if (option.equals(SET_HUMIDITY_RANGE)) {
            handleHumidityRange();
        }
    }

}
