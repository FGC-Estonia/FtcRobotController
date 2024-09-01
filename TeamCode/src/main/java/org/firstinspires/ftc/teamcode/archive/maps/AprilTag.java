package org.firstinspires.ftc.teamcode.archive.maps;

public class AprilTag {
    private String description;
    private double x, y, z;
    private double rotation;
    private double[] detectionArea;

    public AprilTag(String description, double x, double y, double z, double rotation, double[] detectionArea) {
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.detectionArea = detectionArea;
    }
    @Deprecated
    public void setAttributes(String description, double x, double y, double z, double[] additionalAttributes) {
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
        this.detectionArea = additionalAttributes;
    }

    @Override
    public String toString() {
        return "AprilTag{" +
                "description='" + description + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rot=" + rotation +
                '}';
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getRotation(){return rotation;}

    public double[] getDetectionArea() {
        return detectionArea;
    }
}
