package org.firstinspires.ftc.teamcode.archive.gimbal;  //place where the code is located

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Gimbal {

    //initial deafult values
    private double wantedPitch = 0.91;
    private double wantedYaw = 0.71;

    private final double servoRom = 245.64; // the actual rom of a smart robot servo derived from testing
    private final double servoRomRAD = 4.28722677;
    private final double maxPotentiometerVoltage = 3.278;
    private final double degreesPerVolt = servoRom / maxPotentiometerVoltage;

    private boolean targetPositionYawReached = false;
    private boolean targetPositionPitchReached = false;

    //these are interacted with in multiple places so they need to be here.
    private AnalogInput potentiometer = null;
    private Servo gimbalPitch = null;
    private Servo gimbalYaw = null;
    private Servo gimbalPos = null;

    private final Telemetry telemetry;
    private final HardwareMap hardwareMap;

    SlowUpDate pitchServoUpdate;
    SlowUpDate yawUpDate;
    SlowUpDate pitchUpDate;



    public Gimbal(HardwareMap hardwareMap, Telemetry telemetry){
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        initGimbal();
    }

    public void initGimbal() {

        pitchUpDate = new SlowUpDate(10);
        yawUpDate = new SlowUpDate(10);
        pitchServoUpdate = new SlowUpDate(60);

        potentiometer = hardwareMap.get(AnalogInput.class, "Analog_Port_0_CH");
        gimbalPitch = hardwareMap.get(Servo.class, "Servo_Port_0_CH");
        gimbalYaw = hardwareMap.get(Servo.class, "Servo_Port_1_CH");
        gimbalPos = hardwareMap.get(Servo.class, "Servo_Port_2_CH");
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


    public void moveGimbal(boolean automatic, boolean moveManual, double manualX, double manualY, double ftcPoseX, double ftcPoseY, double ftcPoseZ, boolean upDateAutomatic){
        //for debuging
        telemetry.addData("ftcPoseY", ftcPoseY);
        telemetry.addData("ftcPoseX", ftcPoseX);
        telemetry.addData("wantedYaw", wantedYaw);
        telemetry.addData("atan", Math.atan(ftcPoseX/ftcPoseY));
        telemetry.addData("uus wantedYaw", calculateServoAngle(ftcPoseX, ftcPoseY));
        telemetry.addData("uus asukoha arvutus", (wantedYaw - calculateServoAngle(ftcPoseX, ftcPoseY))*servoRom);
        telemetry.addData("potentiometer", anglePosition());
        //actual code

        //automatic is true when button is pressed on controller
        //upDateAutomatic is true when camera sees an apriltag
        if (automatic && upDateAutomatic) {
            // checks that the servo does not move before giving new instructions
            //also checks that movement isn't pointlessly little
            if (targetPositionYawReached && (Math.abs(wantedYaw - (wantedYaw + calculateServoAngle(ftcPoseX, ftcPoseY))) < 1));
            {
                //adds the degrees to centre apriltag and sets the variable targetPositionYawReached to false so it ain't adding more so servo doesn't end up in one corners
                wantedYaw += calculateServoAngle(ftcPoseX, ftcPoseY);
                targetPositionYawReached = false;
            }
            
            //checks if the servo is reached it's set degrees
            if (wantedYaw*servoRom == anglePosition());{
                targetPositionYawReached = true;
            }

            // checks that the servo does not move before giving new instructions
            //also checks that movement isn't pointlessly little
            if (targetPositionPitchReached && (Math.abs(wantedPitch - (wantedPitch + calculateServoAngle(ftcPoseZ, ftcPoseY))) < 2));{
                wantedPitch += calculateServoAngle(ftcPoseZ, ftcPoseY);
                targetPositionPitchReached = false;
            }
            
            //ühe asemele tuleb panna võimalus kuidas kontrollida päris servo nurka
            //checks if the servo is reached it's set degrees
            if (pitchServoUpdate.isTurn()){
                targetPositionPitchReached = true;
            }
        }
        //for manually moving the gimbal
        else {
            //to move pitch axes
            if (pitchUpDate.isTurn()) {
                wantedPitch -= manualY/500; // Uncomment and use as needed
            }
            //to move yaw axes
            if (yawUpDate.isTurn()) {
                wantedYaw += manualX / 200;
            }
        }

        wantedYaw = normalize(wantedYaw);
        wantedPitch = normalize(wantedPitch);

        gimbalPos.setPosition(wantedYaw);
        gimbalPitch.setPosition(wantedPitch);
        gimbalYaw.setPosition(wantedYaw);


    }
    private double normalize (double tooBig){
        if (tooBig<0){
            return 0;
        } if (tooBig>1){
            return 1;
        }
        return tooBig;
    }

    //gives values from 0  to 1 that is used to move servo
    private double calculateServoAngle (double xz, double y) {
        //uses tangents to calculate radians then converts those to degrees and divides by 10 to get the actual degrees
        //and then dividing by 245.6 to get value 0 to 1 where 0 is 0 degrees and 1 is 245.6
        double angleDifference = Math.atan(xz/y);
        double degrees = Math.toDegrees(angleDifference)/10;
        return degrees/servoRom;
    }

}

class SlowUpDate {
    // System.currentTimeMillis(); return milliseconds long so everything is in long to avoid type conflicts
    private long msBetween = 20;
    private long lastTime = System.currentTimeMillis();

    SlowUpDate(long msBetween){
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
