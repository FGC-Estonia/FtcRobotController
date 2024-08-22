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

    SlowUpDate pitchServoUpdate;
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

        pitchUpDate = new SlowUpDate();
        pitchUpDate.initSlowUpDate(10);
        yawUpDate = new SlowUpDate();
        yawUpDate.initSlowUpDate(10);
        pitchServoUpdate = new SlowUpDate();
        pitchServoUpdate.initSlowUpDate(60);


        telemetry = telemetryPorted;

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


    public double moveGimbal(boolean automatic, boolean moveManual, double manualX, double manualY, double ftcPoseX, double ftcPoseY, double ftcPoseZ, boolean upDateAutomatic){
        telemetry.addData("ftcPoseZ", ftcPoseZ);
        telemetry.addData("ftcPoseX", ftcPoseX);
        telemetry.addData("wantedYaw", wantedYaw);
        telemetry.addData("atan", Math.atan(ftcPoseX/ftcPoseZ));
        telemetry.addData("uus wantedYaw ",calculateServoAngle(ftcPoseX, ftcPoseZ, true));
        telemetry.addData("uus asukoha arvutus", wantedYaw + calculateServoAngle(ftcPoseX, ftcPoseZ, true));

        if (automatic && upDateAutomatic) { //upDateAutomatic is only true when the camera sees an apriltag
            if(!(Math.abs(calculateServoAngle(ftcPoseX, ftcPoseZ, true)) < 2)){
                double servoPosition = calculateServoAngle(ftcPoseX, ftcPoseZ, true)/245.6;
                wantedYaw += servoPosition;
            }

        }
        else{

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

    //add comments when ready
    private double calculateServoAngle (double xz, double y, boolean potentiometer) {
        if (potentiometer){
            double angleDifference = Math.atan(xz/y);
            return Math.toDegrees(angleDifference)/10;
        }
        return 1;
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
