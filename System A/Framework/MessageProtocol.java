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


        // new
        public static final String SECURITY_ALARM_ON = "SC1"; // ...
        public static final String SECURITY_ALARM_OFF = "SC0"; // ...

        public static final String ACK_SECURITY_ALARM_ON = "ASEC1";
        public static final String ACK_SECURITY_ALARM_OFF = "ASEC0";

        public static final String WINDOW_BROKEN = "1"; // ...
        public static final String WINDOW_OK = "0"; // ...
        public static final String DOOR_BROKEN = "1"; // ...
        public static final String DOOR_OK = "0"; // ...
        public static final String MOTION_DETECTED = "1"; // ...
        public static final String MOTION_OK = "0"; // ...
        //@TODO add messages for different sensors (F, T, H)
        
        public static final String ACK_HEATER_ON = "AH1";
        public static final String ACK_HEATER_OFF = "AH0";

        public static final String ACK_CHILLER_ON = "AC1";
        public static final String ACK_CHILLER_OFF = "AC0";

        public static final String ACK_DEHUMIDIFIER_ON = "AD1";
        public static final String ACK_DEHUMIDIFIER_OFF = "AD0";

        public static final String ACK_HUMIDIFIER_ON = "AHUM1";
        public static final String ACK_HUMIDIFIER_OFF = "AHUM0";



        public static final String ACK_SPRINKLER_ON = "AS1"; //..
        //@TODO add messages for different ack.. (AS0, AWDM1, etc.)
        
        //@TODO add other controller messages

    }

}

