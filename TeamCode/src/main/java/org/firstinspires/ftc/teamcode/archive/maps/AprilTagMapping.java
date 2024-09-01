package org.firstinspires.ftc.teamcode.archive.maps;

import java.util.HashMap;
import java.util.Map;

public class AprilTagMapping {

    private static final Map<Integer, AprilTag> aprilTags = new HashMap<>();
    static {
        /*
        facing the field edge nexus goals (back of the field) from the center of the field
        x grows to the right
        y grows towards the front of the field
        z grows from the field floor up towards the ceiling
        all locations are from the center of the tag relative to the back left corner of the field.
         */
        aprilTags.put(100, new AprilTag("Blue Nexus Goal - Field Center - Facing Platform", 4085, 3911.18, 1000, 0, new double[0]));
        aprilTags.put(101, new AprilTag("Red Nexus Goal - Field Center - Facing Platform", 2920, 3911.18, 1000, 0, new double[0]));
        aprilTags.put(102, new AprilTag("Red Nexus Goal - Field Center - Facing Food Warehouse", 2920, 3038.82, 1000, Math.PI, new double[0]));
        aprilTags.put(103, new AprilTag("Blue Nexus Goal - Field Center - Facing Food Warehouse", 4085, 3038.82, 1000, Math.PI, new double[0]));
        aprilTags.put(104, new AprilTag("Blue Nexus Goal - Field Edge - Alliance Station", 766.54, 506.18, 1000, 0, new double[0]));
        aprilTags.put(105, new AprilTag("Blue Nexus Goal - Field Edge - Center Field", 1615.09, 506.18, 760, 0, new double[0]));
        aprilTags.put(106, new AprilTag("Red Nexus Goal - Field Edge - Center Field", 5384.91, 506.18, 760, 0, new double[0]));
        aprilTags.put(107, new AprilTag("Red Nexus Goal - Field Edge - Alliance Station", 6233.46, 506.18, 1000, 0, new double[0]));
    }

    public static Map<Integer, AprilTag> getMap() {
        return aprilTags;
    }

    public double[] getTagLocation(int tagID) throws Exception{

        AprilTag tag = aprilTags.get(tagID);
        if (tag != null) {
            return new double[]{tag.getX(), tag.getY(), tag.getZ(), tag.getRotation()};
        } else {
            throw new Exception("tag does not exist");
        }
    }

}
