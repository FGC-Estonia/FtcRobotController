package org.firstinspires.ftc.teamcode.mainModules;  //place where the code is located


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Erection {
    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private Servo leftServo;
    private Servo rightServo;
    private DcMotorEx frontElevatorEx;
    private DcMotorEx backElevatorEx;
    private TouchSensor limit;
    private boolean isInitError = false;
    private int attemptedInitCount = 0;


    private void mapMotors() {
        try {

            limit = hardwareMap.get(TouchSensor.class, "Digital_Port_0_EH");

            leftServo = hardwareMap.get(Servo.class, "Servo_Port_4_CH");
            rightServo = hardwareMap.get(Servo.class, "Servo_Port_3_CH");

            //map Dc motors with encoders, it is in a try, catch because if the expansion hub is not
            //properly connected the robot will throw an error and prevent the code from running

            frontElevatorEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_1_EH");
            backElevatorEx = hardwareMap.get(DcMotorEx.class, "Motor_Port_0_EH");

            frontElevatorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backElevatorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            backElevatorEx.setDirection(DcMotorSimple.Direction.FORWARD);

            frontElevatorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backElevatorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            frontElevatorEx.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            backElevatorEx.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

            isInitError = false;
        } catch (Exception eInit) {
            isInitError = true;
        }
    }

    public Erection(HardwareMap hardwareMapPorted, Telemetry telemetryPorted) {

            hardwareMap = hardwareMapPorted;
            telemetry = telemetryPorted;

            mapMotors();
    }


    public void raise(boolean disable, double rightStick, boolean bottom, boolean height80, boolean height100, boolean height120) {

        if (!isInitError && !disable) {
            try {
                telemetry.addData("Erection level:",(frontElevatorEx.getCurrentPosition()+backElevatorEx.getCurrentPosition())/2);
                if (bottom && !(height80 || height100 || height120)) {
                    runToHeight(300);
                }
                if (height80 && !(bottom || height100 || height120)) {
                    runToHeight(3200);
                }
                if (height100 && !(height80 || bottom || height120)) {
                    runToHeight(4000);
                }
                if (height120 && !(height80 || height100 || bottom)) {
                    runToHeight(4900);
                }
                if (!(height80 || height100 || bottom || height120)){

                        frontElevatorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //runs using speed
                        backElevatorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                        frontElevatorEx.setPower(-rightStick); // set max power
                        backElevatorEx.setPower(-rightStick);
                        /*
                        //if (!(height() >= 13000)) {  bit bad lähenemine
                        frontElevatorEx.setVelocity(rightStick * 1972.92);
                        backElevatorEx.setVelocity(-rightStick * 1972.92);*/


                }


                //telemetry.addData("front erector position", frontElevatorEx.getCurrentPosition());
                //telemetry.addData("back erector position", backElevatorEx.getCurrentPosition());
            } catch (Exception e){
                telemetry.addData("erectile  disfunction", true);
            }
        } else {
            telemetry.addData("erectile initialization disfunction", true);
            tryMapMotors();
        }
    }
    public void tryMapMotors(){ //reduce failed attempted mappings = reduce useless computing
        attemptedInitCount++;
        if (attemptedInitCount < 100000){
            telemetry.addLine("attempting mapping");
            mapMotors();
        }
    }
    public void release(boolean disable, boolean left, boolean right) {
        try {

            if (!disable && !isInitError) {
                if (left) {
                    leftServo.setPosition(0);
                } else {
                    leftServo.setPosition(0.5);
                }
                if (right) {
                    rightServo.setPosition(1);
                } else {
                    rightServo.setPosition(0.5);
                }

            }
        } catch (Exception e){
            telemetry.addData("erectile initialization disfunction", true);
            telemetry.addData("error", e.toString());
            tryMapMotors();
        }
    }

    public void runToHeight(int height) {
        frontElevatorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION); //runs to position
        backElevatorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontElevatorEx.setTargetPosition(height);//1000(height mm)/(6mm(hex shaft diameter)*3,14)*28(ticks per rotation)
        backElevatorEx.setTargetPosition(height);
        backElevatorEx.setPower(1);
        frontElevatorEx.setPower(1);
    }

}