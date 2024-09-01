package org.firstinspires.ftc.teamcode.mainModules;  //place where the code is located

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MoveRobot {

    //these need to be defined here because they are used in multiple methods
    private DcMotorEx leftFrontDriveEx = null;  //  Used to control the left front drive wheel
    private DcMotorEx rightFrontDriveEx = null;  //  Used to control the right front drive wheel
    private DcMotorEx leftBackDriveEx = null;  //  Used to control the left back drive wheel
    private DcMotorEx rightBackDriveEx = null;  //  Used to control the right back drive wheel

    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;

    private final boolean useVelocity;


    public MoveRobot(HardwareMap hardwareMap, Telemetry telemetry, boolean useVelocity){
        //Pass required objects and a setting to the class
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        this.useVelocity = useVelocity;
        mapMotors();
    }

    private void mapMotors() {

        // Mapping motors
        rightFrontDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_0_CH");
        leftFrontDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_1_CH");
        leftBackDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_2_CH");
        rightBackDriveEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_3_CH");

        //set the correct directions for the motors
        leftFrontDriveEx.setDirection(DcMotorEx.Direction.FORWARD);
        leftBackDriveEx.setDirection(DcMotorEx.Direction.FORWARD);
        rightFrontDriveEx.setDirection(DcMotorEx.Direction.REVERSE);
        rightBackDriveEx.setDirection(DcMotorEx.Direction.REVERSE);


        // Depending on settings, the robot will run using velocity or power
        if (useVelocity) {
            leftFrontDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBackDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDriveEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            leftFrontDriveEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftBackDriveEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightFrontDriveEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightBackDriveEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // the main function for moving the robot
    public void move(boolean disable, double heading, double drive, double strafe, double turn, boolean fieldCentric) {
        if (!disable) {
            double x;
            double y;

            // The operator can choose to move the robot relative to the field or to the robot
            if (fieldCentric) {
                // we have had problems with the imu, this prevents the code from crashing during a game if something goes wrong
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
            double maxRawPower = Math.max(
                    Math.max(Math.abs(leftFrontPowerRaw), Math.abs(leftBackPowerRaw)),
                    Math.max(Math.abs(rightFrontPowerRaw), Math.abs(rightBackPowerRaw))
            );
            // if the power is not over 1, the code will divide by 1, which doesn't affect the end result
            double max = Math.max(maxRawPower, 1.0);
            double maxAngularVelocityRadians = 1972.92;
            double wheelSizeCorrection = 1.2039; // we use 2 different sizes of wheels. We double the wheels on each motor for better grip and there are only 4 of both sizes

            if (useVelocity) {
                // Calculate wheel speeds normalized to the wheels.
                double leftFrontRawSpeed = (leftFrontPowerRaw / max * maxAngularVelocityRadians);
                double leftBackRawSpeed = (leftBackPowerRaw / max * maxAngularVelocityRadians / wheelSizeCorrection);
                double rightFrontRawSpeed = (rightFrontPowerRaw / max * maxAngularVelocityRadians / wheelSizeCorrection);
                double rightBackRawSpeed = (rightBackPowerRaw / max * maxAngularVelocityRadians);

                leftFrontDriveEx.setVelocity(leftFrontRawSpeed);
                leftBackDriveEx.setVelocity(leftBackRawSpeed);
                rightFrontDriveEx.setVelocity(rightFrontRawSpeed);
                rightBackDriveEx.setVelocity(rightBackRawSpeed);
            } else {
                // Set motor power directly
                leftFrontDriveEx.setPower(leftFrontPowerRaw / max);
                leftBackDriveEx.setPower(leftBackPowerRaw / max);
                rightFrontDriveEx.setPower(rightFrontPowerRaw / max);
                rightBackDriveEx.setPower(rightBackPowerRaw / max);
            }
        }
    }
}
