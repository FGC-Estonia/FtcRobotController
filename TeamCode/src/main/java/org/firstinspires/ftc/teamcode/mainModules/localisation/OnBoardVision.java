package org.firstinspires.ftc.teamcode.mainModules.localisation;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class OnBoardVision {


    private static final AprilTagLibrary feedingTheFutureTagLibrary = getFeedingTheFutureTagLibrary();
    private static final boolean USE_WEBCAM = true;
    //crating objects so that they could be mapped later on when initAprilTag is called
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private static AprilTagLibrary getFeedingTheFutureTagLibrary() {
        return (new AprilTagLibrary.Builder())
                .addTag(100, "Blue Nexus Goal - Field Center - Facing Platform", 160.0, DistanceUnit.MM)
                .addTag(101, "Red Nexus Goal - Field Center - Facing Platform", 160.0, DistanceUnit.MM)
                .addTag(102, "Red Nexus Goal - Field Center - Facing Food Warehouse", 160.0, DistanceUnit.MM)
                .addTag(103, "Blue Nexus Goal - Field Center - Facing Food Warehouse", 160.0, DistanceUnit.MM)
                .addTag(104, "Blue Nexus Goal - Field Edge - Alliance Station", 160.0, DistanceUnit.MM)
                .addTag(105, "Blue Nexus Goal - Field Edge - Center Field", 160.0, DistanceUnit.MM)
                .addTag(106, "Red Nexus Goal - Field Edge - Center Field", 160.0, DistanceUnit.MM)
                .addTag(107, "Red Nexus Goal - Field Edge - Alliance Station", 160.0, DistanceUnit.MM)
                .build();
    }



    public void initProcessor(HardwareMap hardwareMapPorted, Telemetry telemetryPorted) {

        hardwareMap = hardwareMapPorted;
        telemetry = telemetryPorted;

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(feedingTheFutureTagLibrary)
                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                .setOutputUnits(DistanceUnit.MM, AngleUnit.RADIANS)

                // The following default settings are available to un-comment and edit as needed.
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setDrawTagOutline(false) //save resources
                .setDrawTagOutline(false) //save resources

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                // ... these parameters are fx, fy, cx, cy.

                .build();
        aprilTag.setDecimation(3);

        VisionPortal.Builder builder = new VisionPortal.Builder();


        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
        visionPortal.setProcessorEnabled(aprilTag, true);

    }   // end method initAprilTag()

    public ArrayList<AprilTagDetection> returnAprilTagData() {

        return aprilTag.getDetections();
    }   // end method telemetryAprilTag()
}




