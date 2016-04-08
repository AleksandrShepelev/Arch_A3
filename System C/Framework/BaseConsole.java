/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

import TermioPackage.Termio;

abstract public class BaseConsole {

    protected static final String STOP_SYSTEM = "X";

    protected Termio _input = new Termio();
    protected BaseMonitor _monitor;

    protected abstract void initMonitor(String args[]);
    protected abstract void handleUserInput(String option);
    protected abstract void initStartMenu();

    protected void execute() {

        boolean done = false;                // Main loop flag
        String option;                // Menu choice from user

        // Here we check to see if registration worked. If ef is null then the
        // message manager interface was not properly created.

        if (_monitor.isRegistered()) {

            Thread monitor = new Thread(_monitor);
            monitor.start(); // Here we start the monitoring and control thread

            while (!done) {

                // Here, the main thread continues and provides the main menu
                initStartMenu();

                // Handle the user input separately
                option = _input.KeyboardReadString();
                handleUserInput(option);

                // handle exit separately
                if (option.equals(STOP_SYSTEM)) {
                    // Here the user is done, so we set the Done flag and halt
                    // the environmental control system. The monitor provides a method
                    // to do this. Its important to have processes release their queues
                    // with the message manager. If these queues are not released these
                    // become dead queues and they collect messages and will eventually
                    // cause problems for the message manager.

                    _monitor.halt();
                    done = true;
                    System.out.println("\nConsole Stopped... Exit monitor window to return to command prompt.");
                    _monitor.halt();
                }

            } // while

        } else {
            System.out.println("\n\nUnable start the monitor.\n\n");
        } // if
    }
}
