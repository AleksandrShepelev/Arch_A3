
package Framework;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.util.Random;

public abstract class BaseSensor
{
    private static final int SLEEP_DELAY = 2500;    // The loop delay (2.5 seconds)
    protected MessageManagerInterface _em = null;   // Interface object to the message manager
    protected MessageWindow _mw = null;

    private static final Random _random = new Random();

    protected BaseSensor(String args[])
    {
        parseArguments(args);
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

            } else {

                // message manager is not on the local system
                String address = args[0];
                System.out.println("\n\nAttempting to register on the machine:: " + address );

                // Here we create an message manager interface object. This assumes
                // that the message manager is NOT on the local machine
                _em = new MessageManagerInterface(address);

            }
        } catch (Exception e) {
            System.out.println("Error instantiating message manager interface: " + e);
        }
    }

    private void createMessageWindow()
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

        messageWindowAfterCreate();
    }

    protected void run()
    {
        if (_em == null) {
            System.out.println("Unable to register with the message manager.\n\n" );
            return;
        }

        createMessageWindow();
        handle();
    }

    // not all sensors will handle the messages
    protected void handleMessage(Message msg){}

    private void handle()
    {
        Message msg;				// Message object
        MessageQueue eq;			// Message Queue

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

                    handleMessage(msg);

                    // If the message ID == 99 then this is a signal that the simulation
                    // is to end. At this point, the loop termination flag is set to
                    // true and this process unregisters from the message manager.

                    if (msg.GetMessageId() == MessageProtocol.Type.TERMINATE ) {
                        done = true;
                        _em.UnRegister();
                        _mw.WriteMessage("\n\nSimulation Stopped. \n");
                    }

                }

                afterHandle();

                // Here we wait for a 2.5 seconds before we start the next sample
                Thread.sleep(SLEEP_DELAY);

            } catch (Exception e) {
                _mw.WriteMessage("An error occurred:: " + e);
            }
        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: GetRandomNumber
     * Purpose: This method provides the simulation with random floating point
     *		   temperature values between 0.1 and 0.9.
     *
     * Arguments: None.
     *
     * Returns: float
     *
     * Exceptions: None
     *
     ***************************************************************************/

    protected static float getRandomNumber()
    {
        Float val = -1.0f;

        while (val < 0.1) {
            val = _random.nextFloat();
        }

        return val;
    }

    /***************************************************************************
     * CONCRETE METHOD:: CoinToss
     * Purpose: This method provides a random true or false value used for
     * determining the positiveness or negativeness of the drift value.
     *
     * Arguments: None.
     *
     * Returns: boolean
     *
     * Exceptions: None
     *
     ***************************************************************************/

    protected static boolean coinToss()
    {
        return(_random.nextBoolean());
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

    /**
     * Send some data if needed before message handling
     */
    abstract protected void beforeHandle();

    /**
     * Do some after handle message processing
     */
    abstract protected void afterHandle();
}
