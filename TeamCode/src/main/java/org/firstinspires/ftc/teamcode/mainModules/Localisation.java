package org.firstinspires.ftc.teamcode.mainModules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainModules.visionModules.ExternalVision;
import org.firstinspires.ftc.teamcode.mainModules.visionModules.OnBoardVision;
import org.firstinspires.ftc.teamcode.maps.AprilTag;
import org.firstinspires.ftc.teamcode.maps.AprilTagMapping;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.opencv.core.Mat;

import java.util.List;
import java.util.Map;

public class Localisation {

    private final ElapsedTime clock = new ElapsedTime();
    private double elapsedTime = 0;

    private OnBoardVision onBoardVision;
    private ExternalVision externalVision;

    //private Map<Integer, AprilTag> aprilTags = AprilTagMapping.getMap();
    private List<AprilTagDetection> aprilTagDetections = null;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private boolean onBoardInitError = false;
    private boolean externalInitError = false;

    private AprilTagMapping aprilTagMapping;

    public void initVision(HardwareMap hardwareMapPorted, Telemetry telemetryPorted) {
        hardwareMap = hardwareMapPorted;
        telemetry = telemetryPorted;

        aprilTagMapping  = new AprilTagMapping();

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

    public double[] returnPositionData(boolean forceOnBoardProcessor, double pitchAngle) {
        boolean isUpdated = false;
        double[] robotPosition = new double[]{-1, -1, -1}; //set deafult value to -1 if not detected because robots position cant be negative
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
                    telemetry.addLine("tag data");
                    telemetry.addData("Pose X", poseX);
                    telemetry.addData("Pose Y", poseY);
                    telemetry.addData("Pose Z", poseZ);



                    robotPosition = calculateRobotPosition(
                            detection,
                            pitchAngle
                            );
                }
            }

        } catch (Exception e) {
            telemetry.addData("error", e.toString());
            telemetry.addData("VisionError", true);

        }
        return new double[]{
                robotPosition[0], robotPosition[1], robotPosition[2],
                poseX, poseY, poseZ, isPositionData
        };
    }
    private double[] calculateRobotPosition(AprilTagDetection detection, double potentiometer) throws Exception{

            // Get the tag's position from the map based on its ID
            telemetry.addData("ID", detection.id);
            //telemetry.addLine();

            double[] tagPosition = aprilTagMapping.getTagLocation(detection.id);
        telemetry.addData("2", true);
            telemetry.addData("Tag Position (X, Y, Z, Yaw)",
                String.format("%.2f, %.2f, %.2f, %.2f", tagPosition[0], tagPosition[1], tagPosition[2], tagPosition[3]));


            double[] cameraPosition = relativeToCamera(
                    tagPosition[0], tagPosition[1], tagPosition[2], tagPosition[3],
                    detection.ftcPose.range, detection.ftcPose.yaw
            );
            telemetry.addData("Camera Position (X, Y, yaw)",
                String.format("%.2f, %.2f, %.2f", cameraPosition[0], cameraPosition[1], cameraPosition[2]));


        double[] robotPosition = compensateForCameraPosition(
                    cameraPosition[0], cameraPosition[1],
                    cameraPosition[2], potentiometer
            );
        telemetry.addData("Robot Position (X, Y, rot)",
                String.format("%.2f, %.2f, %.2f", robotPosition[0], robotPosition[1], robotPosition[2]));

        telemetry.addData("Potentiometer", potentiometer);

        return robotPosition;
    }


    private double[] relativeToCamera(double tagX, double tagY, double tagZ, double tagRotation, double distanceFromTag, double yawFromTag) {
        // Calculate the x and y offset from the apriltag on the field
        double xOffsetFromTag = Math.sin(yawFromTag) * distanceFromTag;
        double yOffsetFromTag = Math.cos(yawFromTag) * distanceFromTag;

        // Add telemetry for offsets
        telemetry.addData("xOffsetFromTag", xOffsetFromTag);
        telemetry.addData("yOffsetFromTag", yOffsetFromTag);

        // Determine apriltag direction based on rotation
        double apriltagDirectionVariable;
        if (tagRotation == 0) {
            apriltagDirectionVariable = 1; // If the tag is rotated 180 degrees, the tag values will be subtracted, not added
        } else {
            apriltagDirectionVariable = -1;
        }
        telemetry.addData("apriltagDirectionVariable", apriltagDirectionVariable);

        // Calculate camera position
        double cameraX = tagX + xOffsetFromTag * apriltagDirectionVariable;
        double cameraY = tagY + yOffsetFromTag * apriltagDirectionVariable;
        double cameraDirection = tagRotation + yawFromTag;

        // Add telemetry for camera position
        telemetry.addData("Camera X", cameraX);
        telemetry.addData("Camera Y", cameraY);
        telemetry.addData("Camera Direction", cameraDirection);

        // Update telemetry


        return new double[]{cameraX, cameraY, cameraDirection};
    }

    private double[] compensateForCameraPosition(double cameraX, double cameraY, double tagRotation, double cameraRotation) {

        // Fixed distance from the camera to the robot's center
        double cameraDistanceFromCenter = 119.2686; // Derived from the Pythagorean theorem (a = 100, b = 65, c = √(a² + b²))

        // Calculate robot rotation
        double robotRotation = tagRotation + cameraRotation;

        // Add telemetry for robot rotation
        telemetry.addData("Robot Rotation", robotRotation);

        // Calculate offsets
        double xOffsetFromTag = Math.sin(robotRotation) * cameraDistanceFromCenter;
        double yOffsetFromTag = Math.cos(robotRotation) * cameraDistanceFromCenter;

        // Add telemetry for offsets
        telemetry.addData("xOffsetFromTag", xOffsetFromTag);
        telemetry.addData("yOffsetFromTag", yOffsetFromTag);

        // Update telemetry
        double robotX = xOffsetFromTag + cameraX;
        double robotY = yOffsetFromTag + cameraY;



        return new double[]{robotX, robotY, robotRotation};
    }

}
