package Sensors;


import Framework.BaseSensor;
import Framework.MessageProtocol;
import MessagePackage.Message;

public class DoorSensor extends BaseSensor {

    private boolean _currentDoorState = false; //door state: false - window is not opened, true - door is opened

    /*
    constants for simulation
     */
    private static final float EVENT_PROBABILITY = 0.05f; //probability that door will be opened
    private static final float BREAK_DURATION = 4; //if door is broken, we will repeat this message (BREAK_DURATION times + 1).
    private int _currentDuration = 0; //counter for repeated messages (See previous option)


    protected DoorSensor (String[] args) {
        super(args);
    }

    @Override
    protected String getName() {
        return "Door sensor";
    }

    @Override
    protected float getWinPosX() {
        return 0.5f;
    }

    @Override
    protected float getWinPosY() {
        return 0.4f;
    }

    @Override
    protected void messageWindowAfterCreate() {
        _mw.WriteMessage("\nInitializing Door Simulation::");

        _mw.WriteMessage("   Initial Door State set :: " + _currentDoorState);
    }

    public static void main(String args[]) {
        DoorSensor sensor = new DoorSensor(args);
        sensor.run();
    }

    @Override
    protected void beforeHandle() {
        // Here we create the message.
        Message msg = new Message(MessageProtocol.Type.WINDOW, String.valueOf(_currentDoorState));

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
            _mw.WriteMessage("Current Door State::  " + _currentDoorState);

        } catch (Exception e) {
            System.out.println("Error Posting Door State:: " + e);
        }
    }

    @Override
    protected void afterHandle() {
        //if door is opened then repeat it BREAK_DURATION times and randomize according to probability, otherwise randomize
        if (_currentState == true) {
            if (_currentDuration >= BREAK_DURATION) {
                _currentDuration = 0;
                _currentDoorState = probablisticCoinToss(EVENT_PROBABILITY);
            } else {
                _currentDuration++;
            }
        } else {
            _currentDoorState = probablisticCoinToss(EVENT_PROBABILITY);
        }

    }

}
