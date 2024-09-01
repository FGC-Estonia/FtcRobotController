package org.firstinspires.ftc.teamcode.mainModules;  //place where the code is located

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MoveRobot {
    double x;
    double y;
    double leftFrontRawSpeed;
    double leftBackRawSpeed;
    double rightFrontRawSpeed;
    double rightBackRawSpeed;
    private DcMotorEx leftFrontDriveEx = null;  //  Used to control the left front drive wheel
    private DcMotorEx rightFrontDriveEx = null;  //  Used to control the right front drive wheel
    private DcMotorEx leftBackDriveEx = null;  //  Used to control the left back drive wheel
    private DcMotorEx rightBackDriveEx = null;  //  Used to control the right back drive wheel

    private HardwareMap hardwareMap; //creating objects so that they could be mapped when initMoveRobot is called by the main program
    private Telemetry telemetry;


    public void initMoveRobot(HardwareMap hardwareMapPorted, Telemetry telemetryPorted) {

        //mapping hardwareMap and telemetry as they need to be connected thru the main program
        hardwareMap = hardwareMapPorted;
        telemetry = telemetryPorted;

        // Mapping motors
        rightFrontDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_0_CH");
        leftFrontDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_1_CH");
        leftBackDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_2_CH");
        rightBackDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_3_CH");

        leftFrontDriveEx.setDirection(DcMotorEx.Direction.FORWARD);
        leftBackDriveEx.setDirection(DcMotorEx.Direction.FORWARD);
        rightFrontDriveEx.setDirection(DcMotorEx.Direction.REVERSE);
        rightBackDriveEx.setDirection(DcMotorEx.Direction.REVERSE);

        leftFrontDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    // a test to return the apriltag(s) position for testing
    // the main function for moving the robot
    public void move(boolean disable, double heading, double drive, double strafe, double turn, boolean fieldCentric) {
        if (!disable) {

            {
                if (fieldCentric) {
                    try {
                        x = drive * Math.cos(heading) - strafe * Math.sin(heading);
                        y = drive * Math.sin(heading) + strafe * Math.cos(heading);
                    } catch (Exception e) {
                        x = drive;
                        y = strafe;
                    }
                } else {
                    x = drive;
                    y = strafe;
                }

                // Calculates raw power to motors
                double leftFrontPowerRaw = x + y + turn;
                double leftBackPowerRaw = x - y + turn;
                double rightFrontPowerRaw = x - y - turn;
                double rightBackPowerRaw = x + y - turn;

                // Calculate the maximum absolute power value for normalization
                double maxRawPower = Math.max(Math.max(Math.abs(leftFrontPowerRaw), Math.abs(leftBackPowerRaw)),
                        Math.max(Math.abs(rightFrontPowerRaw), Math.abs(rightBackPowerRaw)));

                double max = Math.max(maxRawPower, 1.0);
                double maxRadian = 1972.92;

                // Calculate wheel speeds normalized to the wheels.
                leftFrontRawSpeed = (leftFrontPowerRaw / max * maxRadian);
                leftBackRawSpeed = (leftBackPowerRaw / max * maxRadian / 1.2039);
                rightFrontRawSpeed = (rightFrontPowerRaw / max * maxRadian / 1.2039);
                rightBackRawSpeed = (rightBackPowerRaw / max * maxRadian);
            } // move robot


        }
    }
}
