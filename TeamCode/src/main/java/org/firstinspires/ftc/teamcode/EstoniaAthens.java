package org.firstinspires.ftc.teamcode;  //place where the code is located

/* Damn,

There is a huge bug in our code:
    ,__                   __
    '~~****Nm_    _mZ*****~~
            _8@mm@K_
           W~@`  '@~W
          ][][    ][][
    gz    'W'W.  ,W`W`    es
  ,Wf    gZ****MA****Ns    VW.
 gA`   ,Wf     ][     VW.   'Ms
Wf    ,@`      ][      '@.    VW
M.    W`  _mm_ ][ _mm_  'W    ,A
'W   ][  i@@@@i][i@@@@i  ][   W`
 !b  @   !@@@@!][!@@@@!   @  d!
  VWmP    ~**~ ][ ~**~    YmWf
    ][         ][         ][
  ,mW[         ][         ]Wm.
 ,A` @  ,gms.  ][  ,gms.  @ 'M.
 W`  Yi W@@@W  ][  W@@@W iP  'W
d!   'W M@@@A  ][  M@@@A W`   !b
@.    !b'V*f`  ][  'V*f`d!    ,@
'Ms    VW.     ][     ,Wf    gA`
  VW.   'Ms.   ][   ,gA`   ,Wf
   'Ms    'V*mmWWmm*f`    gA`
*/

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mainModules.Alignment;
import org.firstinspires.ftc.teamcode.mainModules.Erection;
import org.firstinspires.ftc.teamcode.mainModules.ImuManager;
import org.firstinspires.ftc.teamcode.mainModules.MoveRobot;
import org.firstinspires.ftc.teamcode.mainModules.Presses;

@TeleOp(name = "Main code Estonia Athens")
// allows to display the code in the driver station, comment out to remove

public class EstoniaAthens extends LinearOpMode { //file name is EstoniaAthens.java    extends the prebuilt LinearOpMode by rev to run
    @Override
    public void runOpMode() {
        /*
         * map objects
         * objectName = new ClassName()
         * eg:
         * runMotor = new RunMotor();
         *
         * if te external classes require initialisation do it here
         * eg:
         * RunMotor runMotor = new RunMotor(hardwareMap, telemetry);
         */
        
        ImuManager imuManager = new ImuManager(hardwareMap, telemetry);
        MoveRobot moveRobot = new MoveRobot(hardwareMap, telemetry, false);
        Erection erection = new Erection(hardwareMap, telemetry);
        Alignment alignment = new Alignment(hardwareMap, telemetry);

        Presses gamepad1_a = new Presses();

        Presses.ToggleGroup gamepad2ToggleGroup = new Presses.ToggleGroup();
        Presses gamepad2_a = new Presses(gamepad2ToggleGroup);
        Presses gamepad2_b = new Presses(gamepad2ToggleGroup);
        Presses gamepad2_x = new Presses(gamepad2ToggleGroup);
        Presses gamepad2_y = new Presses(gamepad2ToggleGroup);

        double strafe;
        double distance1 = 404.0;
        double distance2 = 1167.0; // 3 calculated distances in mm from sensor to wall to align centre of drivebase to goal
        double distance3 = 1945.0;
        double target = distance1; // TODO make it a list selectable via the secondary controller maybe

        telemetry.update();
        waitForStart(); //everything has been initialized, waiting for the start button

        while (opModeIsActive()) { // main loop

            double imuAngle = imuManager.getYawRadians();
            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            if (gamepad1.left_bumper) {
                strafe = alignment.alignTarget(target);
            } else {
                strafe = gamepad1.left_stick_x;
            }
            boolean fieldCentric = gamepad1_a.toggle(gamepad1.a);

            boolean goToBottom = gamepad2_a.toggle(gamepad2.a);
            boolean goTo80 = gamepad2_x.toggle(gamepad2.x);
            boolean goTo100 = gamepad2_b.toggle(gamepad2.b);
            boolean goTo120 = gamepad2_y.toggle(gamepad2.y);
            double raiseManual = gamepad2.right_stick_y;

            boolean releaseLeft = gamepad2.dpad_left;
            boolean releaseRight = gamepad1.dpad_right;

            moveRobot.move(
                    imuAngle,
                    drive, strafe, turn,
                    fieldCentric
            );

            erection.raise(
                    raiseManual,
                    goToBottom,
                    goTo80,
                    goTo100,
                    goTo120
            );

            erection.release(
                    releaseLeft,
                    releaseRight
            );

            telemetry.update();
        }
    }
}
