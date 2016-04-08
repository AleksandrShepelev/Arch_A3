/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Framework;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

abstract class BaseComponent
{
    private static final int SLEEP_DELAY = 2500;    // The loop delay (2.5 seconds)
    private static final int RETRY_COUNT = 5;       // Number of retries for acknowledged delivery
    private boolean _registered = true;     // Signifies that this class is registered with an message manager.

    protected MessageManagerInterface _em = null;   // Interface object to the message manager
    protected MessageWindow _mw = null;

    public String getManagerAddress() {
        return managerAddress;
    }

    private String managerAddress;

    BaseComponent(String args[])
    {
        parseArguments(args);
    }

    BaseComponent() {}

    protected int getSleepDelay()
    {
        return SLEEP_DELAY;
    }

    protected int getRetryCount()
    {
        return RETRY_COUNT;
    }

    public boolean isRegistered() {
        return _registered;
    }

    private void parseArguments(String args[])
    {
        /////////////////////////////////////////////////////////////////////////////////
        // Get the IP address of the message manager
        /////////////////////////////////////////////////////////////////////////////////

        try {
            if (args.length == 0) {

                // message manager is on the local system
                System.out.println("\n\nAttempting to register on the local machine..." );
                _em = new MessageManagerInterface();
                managerAddress = "127.0.0.1";

            } else {

                // message manager is not on the local system
                String address = args[0];
                System.out.println("\n\nAttempting to register on the machine:: " + address );

                // Here we create an message manager interface object. This assumes
                // that the message manager is NOT on the local machine
                _em = new MessageManagerInterface(address);
                managerAddress = address;

            }
        } catch (Exception e) {
            System.out.println("Error instantiating message manager interface: " + e);
            _registered = false;
        }
    }

    protected void execute()
    {
        if (_em == null) {
            System.out.println("Unable to register with the message manager.\n\n" );
            return;
        }

        initMessageWindow();
        handle();
    }

    // not all sensors will handle the messages
    protected void handleMessage(TimeMessage msg){}

    private void initMessageWindow()
    {
        messageWindowCreate();
        messageWindowAfterCreate();
    }

    private void handle()
    {
        Message msg;				// Message object
        MessageQueue eq;			// Message Queue
        TimeMessage timeMessage;

        boolean done = false;			// Loop termination flag

        /********************************************************************
         ** Here we start the main simulation loop
         *********************************************************************/

        _mw.WriteMessage("Beginning Simulation... ");

        while (!done) {
            try {
                // Post the current measurement
                beforeHandle ();

                // Get the message queue
                eq = _em.GetMessageQueue();

                // If there are messages in the queue, we read through them.
                int qLen = eq.GetSize();

                for ( int i = 0; i < qLen; i++ ) {

                    msg = eq.GetMessage();
                    timeMessage = new TimeMessage(msg);

                    handleMessage(timeMessage);

                    // If the message ID == 99 then this is a signal that the simulation
                    // is to end. At this point, the loop termination flag is set to
                    // true and this process unregisters from the message manager.

                    if (timeMessage.GetMessageId() == MessageProtocol.Type.TERMINATE ) {
                        done = true;
                        _em.UnRegister();
                        _mw.WriteMessage("\n\nSimulation Stopped. \n");
                        unload();
                    }

                }

                afterHandle();

                // Here we wait for a 2.5 seconds before we start the next sample
                Thread.sleep(getSleepDelay());

            } catch (Exception e) {
                _mw.WriteMessage("An error occurred");
                e.printStackTrace();
            }
        }
    }

    abstract protected String getName();

    /**
     * This is the X position of the message window in terms
     * of a percentage of the screen height
     */
    abstract protected float getWinPosX();

    /**
     * This is the Y position of the message window in terms
     * of a percentage of the screen height
     */
    abstract protected float getWinPosY();

    /**
     * Should be overridden in the successor class if needed
     */
    protected void messageWindowAfterCreate() {}

    private void messageWindowCreate()
    {
        // We create a message window. Note that we place this panel about 1/2 across
        // and 1/3 down the screen
        _mw = new MessageWindow(getName(), getWinPosX(), getWinPosY() );
        _mw.WriteMessage("Registered with the message manager." );

        try {
            _mw.WriteMessage("   Participant id: " + _em.GetMyId() );
            _mw.WriteMessage("   Registration Time: " + _em.GetRegistrationTime() );

        } catch (Exception e) {
            _mw.WriteMessage("Error:: " + e);
        }
    }

    /**
     * Send some data if needed before message handling
     */
    protected void beforeHandle() {}

    /**
     * Do some after handle message processing
     */
    protected void afterHandle() {}

    protected void unload() {}

}
