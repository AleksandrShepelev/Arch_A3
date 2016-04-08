/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Monitors;

import Framework.BaseConsole;

public class SecurityConsole extends BaseConsole {

    private static final String ARM = "ON";
    private static final String DISARM = "OFF";

    @Override
    protected void initMonitor(String[] args) {
        _monitor = new SecurityMonitor(args);
    }

    public static void main(String args[]) {
        try {
            SecurityConsole console = new SecurityConsole();
            console.initMonitor(args);
            console.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getNewStateFromUser(boolean isArmed, String option) {
        String expectedInput;
        expectedInput = isArmed ? DISARM : ARM ;
        System.out.println("Enter '" + expectedInput + "' to arm the system");

        if (option.equals(expectedInput)) {
            return !isArmed;
        }
        else {
            return isArmed;
        }
    }

    @Override
    protected void handleUserInput(String option) {
        SecurityMonitor monitor = (SecurityMonitor)_monitor;
        monitor.setArmedState(getNewStateFromUser(monitor.isArmed(), option));
    }

    @Override
    protected void initStartMenu() {
        SecurityMonitor monitor = (SecurityMonitor) _monitor;

        System.out.println("\n\n\n\n");
        System.out.println("Security Command Console: \n");
        System.out.println("Using message manger at: " + monitor.getManagerAddress() + "\n");

        System.out.println("Now security is " + getArmedStateAsString() + "\n");
        System.out.println("Select an Option: \n");
        System.out.println(ARM + ": Arm the system");
        System.out.println(DISARM + ": Disarm teh system");
        System.out.println(STOP_SYSTEM + ": Stop System\n");
        System.out.print("\n>>>> ");
    }

    private String getArmedStateAsString() {
        return ((SecurityMonitor)_monitor).isArmed() ? "ARMED" : "DISARMED";
    }

}
