/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Monitors;

import Framework.BaseConsole;

public class SecurityConsole extends BaseConsole{
    private static final String ARM = "ON";
    private static final String DISARM = "OFF";
    private SecurityMonitor monitor;


    @Override
    protected void initMonitor(String[] args) {
        _monitor = new SecurityMonitor(args);
    }

    public static void main(String args[])
    {
        SecurityConsole console = new SecurityConsole();
        console.initMonitor(args);
        console.execute();
    }

    private void handleSecurityState()
    {
        monitor = (SecurityMonitor) _monitor;
        monitor.setArmedState(getNewStateFromUser(monitor.isArmed()));
    }

    private boolean getNewStateFromUser(Boolean isArmed) {
        System.out.println("Enter 'OFF' to arm the system");

        String option;
        String expectedInput;
        while (true) {
            expectedInput = isArmed ? ARM : DISARM;
            System.out.println("Enter '" + expectedInput + "' to arm the system");
            option = _input.KeyboardReadString();

            if(option.equals(expectedInput)){
                return !isArmed;
            }
            System.out.println("Unexpected input, expected: " +
                    expectedInput + ", please try again...");
        } // while
    }

    @Override
    protected void handleUserInput(String option) {
        handleSecurityState();
    }

    @Override
    protected void initStartMenu() {
        ECSMonitor monitor = (ECSMonitor)_monitor;

        System.out.println("\n\n\n\n");
        System.out.println("Security Command Console: \n");
        System.out.println("Using message manger at: " + monitor.getManagerAddress() + "\n");

        System.out.println("Now secrity is " + getArmedStateAsString() + "%\n");
        System.out.println("Select an Option: \n");
        System.out.println(ARM + ": Arm the system");
        System.out.println(DISARM + ": Disarm teh system");
        System.out.println(STOP_SYSTEM + ": Stop System\n");
        System.out.print("\n>>>> ");
    }

    private String getArmedStateAsString()
    {
        return monitor.isArmed() ? "ARMED" : "DISARMED";
    }

}
