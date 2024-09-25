package org.firstinspires.ftc.teamcode.mainModules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallPusher {

    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private final boolean protect;

    private Servo leftPusher;
    private Servo rightPusher;

    public BallPusher(boolean protect, HardwareMap hardwareMap, Telemetry telemetry){
        this.protect = protect;
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        mapMotors();
    }

    private void mapMotors(){
        leftPusher = hardwareMap.get(Servo.class, "Servo_Port_0_CH");
        rightPusher = hardwareMap.get(Servo.class, "Servo_Port_1_CH");
    }

    public void moveHands(boolean leftState, boolean rightState){

        double leftPosition;
        double rightPosition;

        if (leftState){
            leftPosition = 0;
        } else {
            leftPosition  = 1;
        }
        if (rightState){
            rightPosition = 1;
        } else {
            rightPosition = 0;
        }

        leftPusher.setPosition(leftPosition);
        rightPusher.setPosition(rightPosition);
    }
}
