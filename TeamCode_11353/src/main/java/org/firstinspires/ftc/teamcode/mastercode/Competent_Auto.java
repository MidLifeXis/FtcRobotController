// Simple autonomous program that drives bot forward until end of period
// or touch sensor is hit. If touched, backs up a bit and turns 90 degrees
// right and keeps going. Demonstrates obstacle avoidance and use of the
// REV Hub's built in IMU in place of a gyro. Also uses gamepad1 buttons to
// simulate touch sensor press and supports left as well as right turn.
//
// Also uses PID controller to drive in a straight line when not
// avoiding an obstacle.
//
// Use PID controller to manage motor power during 90 degree turn to reduce
// overshoot.

package org.firstinspires.ftc.teamcode.mastercode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robot.Robot;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.robotutils.MathFunctions;
import org.firstinspires.ftc.teamcode.robotutils.MiniPID;
import org.firstinspires.ftc.teamcode.robotutils.RobotMovement;

import static org.firstinspires.ftc.teamcode.robotutils.RobotMovement.globalAngle;

/*
@Autonomous(name="Actually Competent Auto", group="PID")
//@Disabled
public class Competent_Auto extends LinearOpMode {

    public static double z_angle;
    public static double globalAngle;
    public Orientation angles;


    public static UltimategoalHardware robot = new UltimategoalHardware();
    public static Competent_Auto auto = new Competent_Auto();


    public static MiniPID controllerAngle = new MiniPID(0.035, 0, 0.03); //.025
    public static MiniPID controllerDrive = new MiniPID(0.035, 0, 0); //.025

    public static double OpModeRuntime = System.currentTimeMillis();

    public static double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.
        telemetry.addData("getangel.pre","yes");
        telemetry.update();
        angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle;
        telemetry.addData("getanglepost", angles.firstAngle);
        telemetry.update();

        return -RobotMovement.AngleWrap(deltaAngle - globalAngle);
    }

    public void setAngle() {
        telemetry.addData("SetAngle.pre", "yes");

        telemetry.update();
        angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        telemetry.addData("Setangle.Post", angles.firstAngle);
        telemetry.update();

        //double deltaAngle = angles.firstAngle;
        //globalAngle = deltaAngle;
    }
    public static void stopDrive(){
        robot.frontleftDrive.setPower(0);
        robot.frontrightDrive.setPower(0);
        robot.backleftDrive.setPower(0);
        robot.backrightDrive.setPower(0);
    }

    public static void drivePID(double power, double goalAngle, int direction, double goal, int sensor, Telemetry telemetry) {//-180 to 180
        double starTime = System.currentTimeMillis();
        controllerDrive.setOutputLimits(-1, 1);
        while (true) {
            double correction = controllerDrive.getOutput(getAngle(), goalAngle);
            telemetry.addData("Distance", MathFunctions.getDistance(sensor));
            telemetry.update();
            double y = -direction * power;
            double x = 0;
            double z = correction;
            robot.frontleftDrive.setPower(y + x + z);
            robot.frontrightDrive.setPower(-y + x + z);
            robot.backleftDrive.setPower(y - x + z);
            robot.backrightDrive.setPower(-y - x + z);
            if (MathFunctions.getDistance(sensor) <= goal) {
                stopDrive();
                break;
            }
        }
    }




    @Override
    public void runOpMode() {

        //Initialize servos to starting positions
        robot.init(hardwareMap);
        telemetry.update();


        angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        telemetry.addData("Mode", "calibrating...");
        telemetry.update();

        while (!isStopRequested() && !robot.imu.isGyroCalibrated()) {
            sleep(50);
            idle();

        }
        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", robot.imu.getCalibrationStatus().toString());

        telemetry.update();
        angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        telemetry.addData("test", angles.firstAngle);
        telemetry.update();
        waitForStart();
        setAngle();
        telemetry.addData("POstPost", "yes");
        telemetry.update();


        //Code above here should never change
        //Sets the initial position of the robot, the bottom right, looking at the target
        RobotMovement.turnToAnglePID(-90, telemetry);
        RobotMovement.drivePID(.5, -90, 1, 10, 2, telemetry);
        RobotMovement.turnToAnglePID(-180, telemetry);
        RobotMovement.drivePID(.5, -180, 1, 10, 2, telemetry);
        RobotMovement.turnToAnglePID(0, telemetry);


        //While the stop button isn't pressed, run this code
        while (!isStopRequested()) {
            RobotMovement.drivePID(.5, 0, 1, 10, 2, telemetry);
            RobotMovement.turnToAnglePID(90, telemetry);
            RobotMovement.drivePID(.5, 90, 1, 10, 2, telemetry);
            RobotMovement.turnToAnglePID(180, telemetry);
            RobotMovement.drivePID(.5, 180, 1, 10, 2, telemetry);
            RobotMovement.turnToAnglePID(-90, telemetry);
            RobotMovement.drivePID(.5, -90, 1, 10, 2, telemetry);




            /*
            //Park
            RobotMovement.drivePIDtime(.5, 90, -1, 250, 0);
            RobotMovement.strafeLeft(1, 1250);

            // Dont put code below here
            RobotMovement.stopDrive();
            break;
            */
/*
        }
        RobotMovement.stopDrive();
        stop();
    }


}
*/