package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Gamepad", group = "Robot")
public class GamepadOpMode extends LinearOpMode {
    DcMotor leftFront, leftRear, rightFront, rightRear, intake;
    DcMotorEx shooter;
    Servo gateServoLeft, gateServoRight;
    Boolean a_pressed, b_pressed, x_pressed, lb_pressed, rb_pressed;

    @Override
    public void runOpMode() throws InterruptedException {
        double driveScale = 0.6;

        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        leftRear = hardwareMap.get(DcMotor.class, "left_rear");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        rightRear = hardwareMap.get(DcMotor.class, "right_rear");

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        leftRear.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);

        intake = hardwareMap.dcMotor.get("intake");
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gateServoLeft = hardwareMap.servo.get("servoLeft");
        gateServoRight = hardwareMap.servo.get("servoRight");

        //region telemetry setup
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        //endregion

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //region a button
            if (gamepad1.a || gamepad2.a) {
                if (!a_pressed) {
                    a_pressed = true;
                    if (shooter.getVelocity() > 1900d) {
                        // Toggle servo
                        double servoPosition = gateServoRight.getPosition() == 0d ? 1d : 0d;
                        gateServoLeft.setPosition(1d - servoPosition);
                        gateServoRight.setPosition(servoPosition);
                    }
                }
            } else { // Reset the 'X' button press flag
                a_pressed = false;
            }
            //endregion

            //region b button
            if (gamepad1.b || gamepad2.b) {
                if (!b_pressed) {
                    b_pressed = true;
                    // code to run once when b press
                }
            } else { // Reset the 'X' button press flag
                b_pressed = false;
            }
            //endregion

            //region x button
            if (gamepad1.x || gamepad2.x) {
                if (!x_pressed) {
                    x_pressed = true;
                    // code to run once when x press
                }
            } else { // Reset the 'X' button press flag
                x_pressed = false;
            }
            //endregion

            //region left bumper
            if (gamepad1.left_bumper || gamepad2.left_bumper) {
                if (!lb_pressed) {
                    lb_pressed = true;
                    if (shooter.getVelocity() == 0d) {
                        double intakePower = intake.getPower() > 0d ? 0d : 0.9d;
                        intake.setPower(intakePower);
                        gateServoLeft.setPosition(0d);
                        gateServoRight.setPosition(1d);
                    }
                }
            } else {
                lb_pressed = false;
            }
            //endregion

            //region right bumper
            if (gamepad1.right_bumper || gamepad2.right_bumper) {
                if (!rb_pressed) {
                    rb_pressed = true;
                    // code to run once when right bumper press
                }
            } else {
                rb_pressed = false;
            }
            //endregion

            //region left_trigger
            if (gamepad1.left_trigger > 0d || gamepad2.left_trigger > 0d) {
                double shooterVelocity = Math.max(gamepad1.left_trigger, gamepad2.left_trigger) * 2200d;
                shooter.setVelocity(shooterVelocity);
                intake.setPower(0d);
            }
            else {
                shooter.setPower(0d);
            }
            //endregion

            //region drivetrain control
            double leftStickY = -gamepad1.left_stick_y;  // Invert if necessary
            double rightStickY = -gamepad1.right_stick_y;  // Invert if necessary
            double drive;

            if (leftStickY >= 0 && rightStickY >= 0) {
                // Both inputs are positive, so choose the maximum positive value.
                drive = Math.max(leftStickY, rightStickY);
            } else if (leftStickY <= 0 && rightStickY <= 0) {
                // Both inputs are negative, so choose the minimum negative value.
                drive = Math.min(leftStickY, rightStickY);
            } else {
                // The inputs have different signs, so set drive to the sum of both.
                drive = leftStickY + rightStickY;
            }

            if (Math.abs(drive) > 0.5) {
                // Adjust driveScale or perform other actions as needed
                if (driveScale < 1.0) {
                    driveScale += 0.01; // Adjust the increment value as needed
                }
            } else {
                // Reset driveScale or perform other actions as needed when |drive| is not greater than 0.5
                driveScale = 0.6;
            }
            // Apply the scaling factor (0.6 in this case):
            drive *= driveScale;

            double turn = gamepad1.left_stick_x * 0.4d;
            double side = gamepad1.right_stick_x * 0.8d;

            // Send calculated power to wheels
            drive(drive, turn, side);
            //endregion
        }
    }

    public void drive(double drive, double turn, double side) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double frontLeftPower = drive + turn + side;
        double backLeftPower = drive + turn - side;
        double frontRightPower = drive - turn - side;
        double backRightPower = drive - turn + side;

        double maxPower = 0.6;
        double maxSpeed = 0.6;

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        leftFront.setPower(maxSpeed * (frontLeftPower / maxPower));
        rightFront.setPower(maxSpeed * (frontRightPower / maxPower));
        leftRear.setPower(maxSpeed * (backLeftPower / maxPower));
        rightRear.setPower(maxSpeed * (backRightPower / maxPower));

        // Show the elapsed game time and wheel power.
        telemetry.clearAll();
        telemetry.addLine("motor | ")
                .addData("lf", "%.1f", frontLeftPower)
                .addData("lr", "%.1f", frontRightPower)
                .addData("rf", "%.1f", frontRightPower)
                .addData("rr", "%.1f", backRightPower);
        telemetry.addData("shooter", "%f", shooter.getVelocity());
        telemetry.addData("intake", "%.1f", intake.getPower());
        telemetry.addData("gate", "%.1f", gateServoRight.getPosition());
        telemetry.update();
    }
}