package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "ExampleGoalBlue", preselectTeleOp = "Gamepad")
//@Disabled
public class ExampleGoalBlue extends LinearOpMode {
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
        sleep(8000);
        robotDriver.gyroDrive(0.5d, -42d, 0d, 10d, null);
        robotDriver.gyroTurn(0.2d, 4d , 5d);
        sleep(500);

        // intake to secure element
        robot.intake.setPower(0.9d);
        robot.gateServoLeft.setPosition(0d);
        robot.gateServoRight.setPosition(1d);
        sleep(500);
        //spin up shooter
        robot.intake.setPower(0d);
        sleep(500);
        robot.shooter.setVelocity(1700d);

        //wait for shooter to spin up
        while(robot.shooter.getVelocity() < 1600d) {
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
        robotDriver.gyroTurn(0.2d, 35d, 5d);
        //turn
        robotDriver.gyroSlide(0.5d, 18d, 35d, 5d, null);
        sleep(500);
        robot.intake.setPower(0.9d);
        robotDriver.gyroDrive(0.5d, 20d, 35d, 5d, null);
        sleep(500);
        robot.intake.setPower(0d);
        robotDriver.gyroDrive(0.5d, -25d, 35d, 5d, null);
        robotDriver.gyroTurn(0.2d, -1d, 5d);
        robotDriver.gyroDrive(0.5d, 13d, -0.5d, 5d, null);


        robot.intake.setPower(0.9d);
        sleep(500);
        robot.intake.setPower(0d);

        robot.shooter.setVelocity(1700d);

        //wait for shooter to spin up
        while(robot.shooter.getVelocity() < 1600d) {
            sleep(500);
        }

        //release element
        robot.gateServoLeft.setPosition(1d);
        robot.gateServoRight.setPosition(0d);
        sleep(500);
        robot.shooter.setPower(0d);
        robot.gateServoLeft.setPosition(0d);
        robot.gateServoRight.setPosition(1d);
        robotDriver.gyroSlide(0.5d, 20d, 35d, 5d, null);


    }
}
