package org.firstinspires.ftc.teamcode;  //place where the code is located

//     o
//        o      ______/~/~/~/__           /((
//        o  // __            ====__    /_((
//        o  //  @))       ))))      ===/__((
//       ))           )))))))        __((
//       \\     \)     ))))    __===\ _((
//        \\_______________====      \_((
//        \((

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mainModules.Alignment;
import org.firstinspires.ftc.teamcode.mainModules.Erection;
import org.firstinspires.ftc.teamcode.mainModules.ImuManager;
import org.firstinspires.ftc.teamcode.mainModules.MoveRobot;
import org.firstinspires.ftc.teamcode.mainModules.Presses;
import org.firstinspires.ftc.teamcode.mainModules.gimbal.Gimbal;
import org.firstinspires.ftc.teamcode.mainModules.VisionManager;

@TeleOp(name = "Main code EstoniaAthens")
// allows to display the code in the driver station, comment out to remove
public class EstoniaAthens extends LinearOpMode { //file name is EstoniaAthens.java    extends the prebuilt LinearOpMode by rev to run
    @Override
    public void runOpMode() {
        //created a deafult value to avoid errors
        double[] positionData = {
                0, //PoseX
                0, //PoseY
                0, //PoseZ
                0, //updatedFrame
        };
        /*
         * map objects
         * objectName = new ClassName()
         * eg:
         * runMotor = new RunMotor();
         *
         * if te external classes require initialisation do it here
         * eg:
         * runMotor.initRunMotor(hardwareMap);
         */
        
        ImuManager imuManager = new ImuManager(hardwareMap, telemetry);
        MoveRobot moveRobot = new MoveRobot(hardwareMap, telemetry, false);
        VisionManager visionManager = new VisionManager(hardwareMap, telemetry);
        Gimbal gimbal = new Gimbal(hardwareMap, telemetry);
        Erection erection = new Erection(hardwareMap, telemetry);
        Alignment alignment = new Alignment(hardwareMap, telemetry);

        Presses gamepad1_a = new Presses();
        Presses gamepad2_dpad_up = new Presses();

        Presses.ToggleGroup gamepad2ToggleGroup = new Presses.ToggleGroup();

        Presses gamepad2_a = new Presses(gamepad2ToggleGroup);
        Presses gamepad2_b = new Presses(gamepad2ToggleGroup);
        Presses gamepad2_x = new Presses(gamepad2ToggleGroup);
        Presses gamepad2_y = new Presses(gamepad2ToggleGroup);


        telemetry.update();
        waitForStart(); //everything has been initialized, waiting for the start button

        while (opModeIsActive()) { // main loop
            
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            boolean disableMovement = false;

            moveRobot.move(
                    disableMovement,
                    imuManager.getYawRadians(),
                    drive, strafe, turn, // drive
                    gamepad1_a.toggle(gamepad1.a)// toggle field centric
            );

            erection.raise(
                    disableMovement,
                    gamepad2.right_stick_y, //raise back
                    gamepad2_a.toggle(gamepad2.a),
                    gamepad2_x.toggle(gamepad2.x),
                    gamepad2_b.toggle(gamepad2.b),
                    gamepad2_y.toggle(gamepad2.y)
            );

            erection.release(
                    disableMovement,
                    gamepad2.dpad_left,
                    gamepad2.dpad_right
            );

            gimbal.moveGimbal(
                    gamepad2_dpad_up.toggle(gamepad2.dpad_up),
                    gamepad2.dpad_down,
                    gamepad2.left_stick_x,
                    gamepad2.left_stick_y,
                    positionData[0],
                    positionData[1],
                    positionData[2],
                    positionData[3]==1
                    );


            if (gamepad2.left_bumper) { gimbal.untuck(); }
            if (gamepad2.left_trigger > 0.5) { gimbal.tuck(); }

            gimbal.telemetryGimbal();

            positionData = visionManager.returnPositionData(true);

            telemetry.update();
        }
    }
}
