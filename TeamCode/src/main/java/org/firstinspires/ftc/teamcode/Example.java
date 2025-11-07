package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Example", preselectTeleOp = "Gamepad")
//@Disabled
public class Example extends LinearOpMode {
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

        robotDriver.gyroDrive(0.2d, 24d, 0d, 10d, null);
        sleep(1000);
        robotDriver.gyroTurn(0.2d, 90d, 5d);
        sleep(1000);
        robotDriver.gyroDrive(0.2d, 20d, 90d, 5d, null);
        sleep(1000);
    }
}
