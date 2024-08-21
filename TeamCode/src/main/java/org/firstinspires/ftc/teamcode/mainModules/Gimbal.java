package org.firstinspires.ftc.teamcode.mainModules;  //place where the code is located

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainModules.GimbalModules.PotentiometerHelper;

public class Gimbal {


    private AnalogInput potentiometer = null;
    private Servo gimbalPitch = null;
    private Servo gimbalYaw = null;
    private Servo gimbalPos = null;

    SlowUpDate pitchUpDate;
    SlowUpDate yawUpDate;

    PotentiometerHelper potentiometerHelper;

    /*
    private double wantedPitch = 1; //initial deafult values
    private double wantedYaw = 0.9;
    */
    private double wantedPitch = 0.91; //initial deafult values
    private double wantedYaw = 0.71;
    private Telemetry telemetry;

    private final double servoRom = 245.64; // the actual rom of a smart robot servo derived from testing
    private final double servoRomRAD = 4.28722677;
    private final double maxPotentiometerVoltage = 3.278;
    private final double degreesPerVolt = servoRom / maxPotentiometerVoltage;


    public void initGimbal(HardwareMap hardwareMapPorted, Telemetry telemetryPorted) {

        telemetry = telemetryPorted;

        pitchUpDate = new SlowUpDate();
        pitchUpDate.initSlowUpDate(10);
        yawUpDate = new SlowUpDate();
        yawUpDate.initSlowUpDate(10);

        potentiometerHelper = new PotentiometerHelper(telemetry);

        potentiometer = hardwareMapPorted.get(AnalogInput.class, "Analog_Port_0_CH");
        gimbalPitch = hardwareMapPorted.get(Servo.class, "Servo_Port_0_CH");
        gimbalYaw = hardwareMapPorted.get(Servo.class, "Servo_Port_1_CH");
        gimbalPos = hardwareMapPorted.get(Servo.class, "Servo_Port_2_CH");
    }

    public double getPotentiometerRatio(){
        return 1-potentiometer.getVoltage()/maxPotentiometerVoltage;
    }

    public double anglePosition() {return (getPotentiometerRatio()*servoRom);}

    public void telemetryGimbal() {
        telemetry.addData("Potentiometer Angle", anglePosition());
    }

    public void untuck() {
        wantedYaw = 0.71;
        wantedPitch = 0.91;
    }

    public void tuck() {
        wantedYaw = 0.9;
        wantedPitch = 1;

    }


    public double moveGimbal(boolean automatic, boolean moveManual, double manualX,
                           double manualY, double ftcPoseX, double ftcPoseY, double ftcPoseZ, boolean upDateAutomatic){// https://ftc-docs.firstinspires.org/en/latest/apriltag/vision_portal/apriltag_intro/apriltag-intro.html
        //telemetry.addData("getPotentiometerRatio()", getPotentiometerRatio());
        //telemetry.addData("getPotentiometerRatio()*servoRom", getPotentiometerRatio()*servoRom);
        //telemetry.addData("wantedYaw", wantedYaw);
        //telemetry.addData("wantedYaw*servoROM", wantedYaw*servoRom);
        //telemetry.addData("automaticGimbal", automatic);
        //telemetry.addData("moveManual", moveManual);
        //telemetry.addData("wantedYaw", wantedYaw*servoRom);
        telemetry.addData("wantedYaw", wantedYaw);
        telemetry.addData("wanted-pot", wantedYaw-getPotentiometerRatio());

        if (automatic && upDateAutomatic) { //upDateAutomatic is only true when the camera sees an apriltag

            wantedYaw = calculateServoAngle(ftcPoseX, ftcPoseY, potentiometer.getVoltage());
            /*
            if (false) {
                double checkNaN = Math.atan(ftcPoseY / ftcPoseZ);
                if (!Double.isNaN(checkNaN)){
                    wantedPitch = Math.atan(ftcPoseY / ftcPoseZ);
                }
            }*/
        }else{

            if (pitchUpDate.isTurn() ) {
                //wantedPitch -= manualY/500;
            }

            if (yawUpDate.isTurn()) {
                wantedYaw += manualX/200;
            }


        }

        wantedYaw = normalize(wantedYaw);
        wantedPitch = normalize(wantedPitch);

        gimbalPos.setPosition(wantedYaw);
        gimbalPitch.setPosition(wantedPitch);
        gimbalYaw.setPosition(wantedYaw);

        return anglePosition();

    }
    private double normalize (double tooBig){
        if (tooBig<0){
            return 0;
        } if (tooBig>1){
            return 1;
        }
        return tooBig;
    }

    private double calculateServoAngle (double xz, double y, double potMeter) {

        double angleDifference = Math.atan(xz/y);
        telemetry.addData("anglediff", angleDifference);
        double potAngleRatio = potentiometerHelper.compareFromTables(potMeter); //create a function to return the actual angle of the potentiometer from /sdcard/potentiometerLogs1000/potentiometer_data_12.99V.txt

        telemetry.addData("comparedratio", potAngleRatio);
        telemetry.addData("wantedYaw",wantedYaw);
        double returnAngle = potAngleRatio + angleDifference/servoRomRAD;

        if (Double.isNaN(returnAngle)){
            return getPotentiometerRatio();
        } else {
            return returnAngle;
        }



    }

}

class SlowUpDate {
    // System.currentTimeMillis(); return milliseconds long so everything is in long to avoid type conflicts
    private long msBetween = 20;
    private long lastTime = System.currentTimeMillis();

    void initSlowUpDate(long msBetween){
        this.msBetween = msBetween;
    }

    boolean isTurn(){
        long currentDiff = System.currentTimeMillis() - lastTime;
        if (currentDiff > msBetween) {
            lastTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
}