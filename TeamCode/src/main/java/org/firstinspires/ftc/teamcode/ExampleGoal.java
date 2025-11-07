package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "ExampleGoal", preselectTeleOp = "Gamepad")
//@Disabled
public class ExampleGoal extends LinearOpMode {
    final protected Robot robot = new Robot();
    protected RobotDriver robotDriver;

    final protected void setupAndWait() {
        robot.init(hardwareMap);
        robotDriver = new RobotDriver(robot, this);

        telemetry.addData("Heading", "%.4f", robot::getHeading);

        // Wait for the game to start (driver presses PLAY)
        // Abort this loop is started or stopped.
        while (!(isStarted() || isStopRequested())) {
            telemetry.update();
            idle();
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");

        // Wait for the game to start (driver presses PLAY)
        // Abort this loop is started or stopped.
        setupAndWait();
        // drive backwards
        robotDriver.gyroDrive(0.2d, -40d, 0d, 10d, null);
        sleep(500);

        // intake to secure element
        robot.intake.setPower(0.9d);
        robot.gateServoLeft.setPosition(0d);
        robot.gateServoRight.setPosition(1d);
        sleep(500);
        //spin up shooter
        robot.intake.setPower(0d);
        sleep(500);
        robot.shooter.setVelocity(2200d);

        //wait for shooter to spin up
        while(robot.shooter.getVelocity() < 1900d) {
            sleep(500);
        }

        //release element
        robot.gateServoLeft.setPosition(1d);
        robot.gateServoRight.setPosition(0d);
        sleep(500);
        robot.shooter.setPower(0d);
        robot.gateServoLeft.setPosition(0d);
        robot.gateServoRight.setPosition(1d);

        // move away from the line
        robotDriver.gyroTurn(0.2d, 85d, 5d);
        sleep(500);
        robot.intake.setPower(0.9d);
        robotDriver.gyroDrive(0.2d, 25d, 90d, 5d, null);
        sleep(500);
        robot.intake.setPower(0d);



    }
}
