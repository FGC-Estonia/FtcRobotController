package org.firstinspires.ftc.teamcode.mainModules;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.archive.localisation.ExternalVision;
import org.firstinspires.ftc.teamcode.archive.localisation.OnBoardVision;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

public class Localisation {

    private OnBoardVision onBoardVision;
    private ExternalVision externalVision;

    private List<AprilTagDetection> aprilTagDetections = null;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private boolean onBoardInitError = false;
    private boolean externalInitError = false;


    //init the external and internal visionProcessors with safeguards
    public void initVision(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        onBoardVision = new OnBoardVision();

        try {
            onBoardVision.initProcessor(hardwareMap, telemetry);
        } catch (Exception e1) {
            onBoardInitError = true;
            telemetry.addData("OnBoard Vision Init Error", e1.getMessage());
        }

        externalVision = new ExternalVision();
        try {
            externalVision.initExternalVision();
        } catch (Exception e2) {
            externalInitError = true;
            telemetry.addData("External Vision Init Error", e2.getMessage());
        }
    }

    public double[] returnPositionData(boolean forceOnBoardProcessor) {
        telemetry.clear();
        boolean isUpdated = false;
        double poseX = 0; //if 0:0 the gimbal will not move so 0:0 is the deafult return
        double poseY = 0;
        double poseZ = 0;
        double isPositionData = 0;

        try {
            //try to get telemetry from external computer
            if (!forceOnBoardProcessor && !externalInitError) {
                try {
                    aprilTagDetections = externalVision.returnAprilTagData();
                    isUpdated = true;
                } catch (Exception e3) {
                    telemetry.addData("External Vision Error", e3.getMessage());
                }
            }
            // if no data is returned try the onboard processor
            if (!isUpdated && !onBoardInitError) {
                try {
                    aprilTagDetections = onBoardVision.returnAprilTagData();
                    isUpdated = true;
                } catch (Exception e4) {
                    telemetry.addData("OnBoard Vision Error", e4.getMessage());
                }
            }

            if (isUpdated && aprilTagDetections != null) {
                for (AprilTagDetection detection : aprilTagDetections) {
                    isPositionData = 1;

                    poseX = detection.ftcPose.x;
                    poseY = detection.ftcPose.y;
                    poseZ = detection.ftcPose.z;

                }
            }

        } catch (Exception e) {
            telemetry.addData("VisionError", e.getMessage());

        }
        return new double[]{
                poseX, poseY, poseZ, isPositionData
        };
    }




}
