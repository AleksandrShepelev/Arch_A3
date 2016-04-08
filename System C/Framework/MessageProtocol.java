/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

public class MessageProtocol
{

    public static final class Type
    {
        // initial messages
        public static final int TEMPERATURE = 1; // message from temperature sensor
        public static final int HUMIDITY = 2; // message from humidity sensor

        public static final int ADJUST_HUMIDITY = -4; // message from humidity controller to humidity sensor
        public static final int CONTROL_HUMIDITY = 4; //
        public static final int ADJUST_TEMPERATURE = -5; //
        public static final int CONTROL_TEMPERATURE = 5; //

        public static final int TERMINATE = 99; //...

        // new messages
        public static final int REGISTER_DEVICE = 10; // register the device on connect
        public static final int ACKNOWLEDGEMENT = 30; // send the acknowledgement message for command receive

        public static final int WINDOW = 11; //
        public static final int DOOR = 12; //
        public static final int MOTION = 13; //

        public static final int FIRE = 14; //

        public static final int SECURITY_ALARM = 21; //
        public static final int SPRINKLER = 22; //
        public static final int FIRE_ALARM = 23; //

    }

    public static final class Body
    {
        // initial
        public static final String HEATER_ON = "H1";
        public static final String HEATER_OFF = "H0";

        public static final String CHILLER_ON = "C1";
        public static final String CHILLER_OFF = "C0";

        public static final String HUMIDIFIER_ON = "H1";
        public static final String HUMIDIFIER_OFF = "H0";

        public static final String DEHUMIDIFIER_ON = "D1";
        public static final String DEHUMIDIFIER_OFF = "D0";


        // Devices sign UP messages
        public static final String REG_WINDOW = "W"; // Window sensor
        public static final String REG_DOOR = "D"; // Door sensor
        public static final String REG_MOTION = "M"; // Motion sensor
        public static final String REG_TEMPERATURE = "T"; // Temperature sensor
        public static final String REG_HUMIDITY = "H"; // Humidity sensor
        public static final String REG_FIRE = "F"; // Fire sensor

        public static final String REG_TEMPERATURE_CONTROLLER = "TC";
        public static final String REG_HUMIDITY_CONTROLLER = "HC";
        public static final String REG_SECURITY_CONTROLLER = "SC";
        public static final String REG_FIRE_CONTROLLER = "FC";
        public static final String REG_SPRINKLER_CONTROLLER = "SPC";

        public static final String ACK_SPRINKLER_ON = "AS1"; //..
        //@TODO add messages for different ack.. (AS0, AWDM1, etc.)

        public static final String WDM_ALARM_ON = "SC1"; // ...
        //@TODO add other controller messages

    }

}
