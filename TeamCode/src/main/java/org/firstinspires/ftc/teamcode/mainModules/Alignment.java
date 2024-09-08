package org.firstinspires.ftc.teamcode.mainModules;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Alignment {
    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private DistanceSensor distance;

    private double distance1 = 404.0;
    private double distance2 = 1167.0; // 3 calculated distances in mm from sensor to wall to align centre of drivebase to goal
    private double distance3 = 1945.0;
    private double target = distance1; // TODO make it a list selectable via the secondary controller maybe
    private double tolerance = 10.0;

    private double Kp = 0.0019;

    private final boolean protect;

    public Alignment(boolean protect, HardwareMap hardwareMap, Telemetry telemetry) {
        this.protect = protect;
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        init();
    }

    private void init() {
        distance = hardwareMap.get(DistanceSensor.class, "Distance"); // TODO add a red/blue toggle
    }

    public double alignTarget(double target) {
        double currentDistance = distance.getDistance(DistanceUnit.MM);
        double error = target - currentDistance;
        telemetry.addData("Error", error);

        if (Math.abs(error) > tolerance) {
            double speed = -(Kp * error); // Calculate proportional speed

            // Ensure the speed is within an acceptable range (-1.0 to 1.0)
            speed = Math.max(-1.0, Math.min(1.0, speed));
            return speed;
        } else {
            return 0.0;
        }
    }
}
