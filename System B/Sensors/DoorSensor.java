package Sensors;

import Framework.MessageProtocol;

public class DoorSensor extends EventSensor {

    protected DoorSensor (String[] args) {
        super(args,MessageProtocol.Type.DOOR);
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


    public static void main(String args[]) {
        DoorSensor sensor = new DoorSensor(args);
        sensor.execute();
    }
}
