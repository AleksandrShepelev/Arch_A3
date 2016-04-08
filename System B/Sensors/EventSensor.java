package Sensors;

import Framework.BaseSensor;
import Framework.TimeMessage;

public abstract class EventSensor extends BaseSensor {
    protected boolean _currentState = false; //state of sensor: false - usual situation, true - alarm situation
    protected int _messageType; //type of message
    /*
    constants for simulation
     */
    protected static final float EVENT_PROBABILITY = 0.1f; //probability that door will be opened
    protected static final float BREAK_DURATION = 19; //if door is broken, we will repeat this message (BREAK_DURATION times + 1).
    protected int _currentDuration = 0; //counter for repeated messages (See previous option)


    protected EventSensor(String[] args, int messageType) {
        super(args);
        _messageType = messageType;
    }

    @Override
    protected void messageWindowAfterCreate() {
        _mw.WriteMessage("\nInitializing "+ getName() +" Simulation::");

        _mw.WriteMessage("   Initial  "+ getName() +"  State set :: " + _currentState);
    }

    //Simulator of event
    @Override
    protected void afterHandle() {
        //if door is opened then repeat it BREAK_DURATION times and randomize according to probability, otherwise randomize
        if (_currentState) {
            if (_currentDuration >= BREAK_DURATION) {
                _currentDuration = 0;
                _currentState = probabilisticCoinToss(EVENT_PROBABILITY);
            } else {
                _currentDuration++;
            }
        } else {
            _currentState = probabilisticCoinToss(EVENT_PROBABILITY);
        }

    }

    @Override
    protected void beforeHandle() {
        // Here we create the message.
        int msgText=0;
        if (_currentState)
            msgText = 1;

        TimeMessage msg = new TimeMessage(_messageType, String.valueOf(msgText));

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg.getMessage());
            _mw.WriteMessage("Current "+ getName() +" State::  " + _currentState);

        } catch (Exception e) {
            System.out.println("Error Posting  "+ getName() +"  State:: " + e);
        }
    }
}
