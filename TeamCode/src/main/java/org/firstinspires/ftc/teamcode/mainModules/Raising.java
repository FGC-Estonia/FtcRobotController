package org.firstinspires.ftc.teamcode.mainModules;  //place where the code is located


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Raising {

    private final Telemetry telemetry;
    private final HardwareMap hardwareMap;
    private Servo leftServo;
    private Servo rightServo;
    private DcMotorEx frontElevatorEx;
    private DcMotorEx backElevatorEx;
    private boolean isInitError = false;
    private int attemptedInitCount = 0;

    private final boolean protect;

    private void mapMotors() {
        if (protect) {
            try {

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
        else {
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
        }
    }

    public Raising(boolean protect, HardwareMap hardwareMap, Telemetry telemetry) {

        this.protect = protect;
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        mapMotors();
    }


    public double raise(double manualRaise, boolean goIf, boolean bottom, boolean height80, boolean height100, boolean height120) {
        if (protect) {
            if (!isInitError) {
                try {
                    if (goIf) {
                        if (bottom) {
                            runToHeight(300);
                        }
                        if (height80) {
                            runToHeight(3200);
                        }
                        if (height100) {
                            runToHeight(4000);
                        }
                        if (height120) {
                            runToHeight(4900);
                        }
                    }
                    if (!(height80 || height100 || bottom || height120)) {

                        frontElevatorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //runs using speed
                        backElevatorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                        frontElevatorEx.setPower(-manualRaise); // set max power
                        backElevatorEx.setPower(-manualRaise);
                        /*
                        //if (!(height() >= 13000)) {  bit bad lähenemine
                        frontElevatorEx.setVelocity(rightStick * 1972.92);
                        backElevatorEx.setVelocity(-rightStick * 1972.92);*/
                    }

                } catch (Exception e) {
                    telemetry.addData("raising  disfunction", true);
                }
            } else {
                telemetry.addData("raising initialization disfunction", true);
                tryMapMotors();
            }
        } else {
            telemetry.addData("Raising level:", (frontElevatorEx.getCurrentPosition() + backElevatorEx.getCurrentPosition()) / 2);
            if (goIf) {
                if (bottom) {
                    runToHeight(300);
                }
                if (height80) {
                    runToHeight(3200);
                }
                if (height100) {
                    runToHeight(4000);
                }
                if (height120) {
                    runToHeight(4900);
                }
            }
            if (!(height80 || height100 || bottom || height120)) {

                frontElevatorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //runs using speed
                backElevatorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                frontElevatorEx.setPower(-manualRaise); // set max power
                backElevatorEx.setPower(-manualRaise);
                        /*
                        //if (!(height() >= 13000)) {  bit bad lähenemine
                        frontElevatorEx.setVelocity(rightStick * 1972.92);
                        backElevatorEx.setVelocity(-rightStick * 1972.92);*/
            }
        }
        double currentHeight = frontElevatorEx.getCurrentPosition();
        if (currentHeight > 3200){
            return 1- currentHeight/4900*0.75;
        } else{
            return 1;
        }
    }

    public void tryMapMotors(){ //reduce failed attempted mappings = reduce useless computing
        attemptedInitCount++;
        if (attemptedInitCount < 100000){
            telemetry.addLine("attempting mapping");
            mapMotors();
        }
    }
    public void release(boolean left, boolean right) {
        if (protect) {
            try {
                if (!isInitError) {
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
            } catch (Exception e) {
                telemetry.addData("raising initialization disfunction", true);
                telemetry.addData("error", e.toString());
                tryMapMotors();
            }
        }
        else {
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