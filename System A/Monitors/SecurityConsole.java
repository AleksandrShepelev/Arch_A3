/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Monitors;

import Framework.BaseConsole;

public class SecurityConsole extends BaseConsole{
    private boolean armed = true;

    public void main(String args[])
    {
        SecurityConsole console = new SecurityConsole();
        console.initMonitor(args);
        console.execute();
    }

    @Override
    protected void initMonitor(String[] args) {

    }

    @Override
    protected void handleUserInput(String option) {

    }

    @Override
    protected void initStartMenu() {

    }
}
