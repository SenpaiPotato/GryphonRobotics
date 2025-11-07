package org.firstinspires.ftc.teamcode;

import android.content.Context;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class Robot {
    public DcMotorEx leftFront, leftRear, rightRear, rightFront, shooter;
    public DcMotor intake;
    public Servo gateServoLeft, gateServoRight;
    IMU imu;
    private Context _appContext;
    int beepSoundID;
    volatile boolean soundPlaying = false;
    SoundPlayer.PlaySoundParams soundParams = new SoundPlayer.PlaySoundParams(false);

    private static final double MAX_VELOCITY = 2800d;
    private static final double COUNTS_PER_MOTOR_REV = 537.68984d;    // eg: HD Hex Motor 20:1 560, core hex 288, 40:1 1120
    private static final double DRIVE_GEAR_REDUCTION = 1d;     // This is < 1.0 if geared UP, eg. 26d/10d
    private static final double WHEEL_DIAMETER_INCHES = 4.09448819d;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.14159265359d);

    public void init(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotorEx.class, "left_front");
        leftRear = hardwareMap.get(DcMotorEx.class, "left_rear");
        rightFront = hardwareMap.get(DcMotorEx.class, "right_front");
        rightRear = hardwareMap.get(DcMotorEx.class, "right_rear");

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        rightFront.setDirection(DcMotorEx.Direction.FORWARD);
        rightRear.setDirection(DcMotorEx.Direction.FORWARD);

        setDriveZeroPowerBehavior(ZeroPowerBehavior.FLOAT);

        intake = hardwareMap.dcMotor.get("intake");
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gateServoLeft = hardwareMap.servo.get("servoLeft");
        gateServoRight = hardwareMap.servo.get("servoRight");

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(LogoFacingDirection.UP, UsbFacingDirection.LEFT)));
        imu.resetYaw();

        _appContext = hardwareMap.appContext;
        beepSoundID = hardwareMap.appContext.getResources().getIdentifier("beep", "raw", hardwareMap.appContext.getPackageName());
        beep();
    }

    public final void beep() {
        if (!soundPlaying) {
            soundPlaying = true;
            SoundPlayer.getInstance().startPlaying(
                    _appContext,
                    beepSoundID,
                    soundParams,
                    null,
                    () -> soundPlaying = false);
        }
    }
    double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }

    boolean isDriveBusy() {
        return leftFront.isBusy() && leftRear.isBusy() &&
                rightFront.isBusy() && rightRear.isBusy();
    }

    void resetDrive() {
        setDriveMode(RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(RunMode.RUN_USING_ENCODER);
        setDriveZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
    }

    void setDriveMode(RunMode runMode) {
        leftFront.setMode(runMode);
        leftRear.setMode(runMode);
        rightFront.setMode(runMode);
        rightRear.setMode(runMode);
    }

    void setDriveZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        leftFront.setZeroPowerBehavior(zeroPowerBehavior);
        leftRear.setZeroPowerBehavior(zeroPowerBehavior);
        rightFront.setZeroPowerBehavior(zeroPowerBehavior);
        rightRear.setZeroPowerBehavior(zeroPowerBehavior);
    }

    void setDriveTarget(double distance, boolean moveSideway) {
        // Determine new target position, and pass to motor controller
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        int dirFL = moveSideway ? -1 : 1;
        int dirFR = 1;
        int dirRL = 1;
        int dirRR = moveSideway ? -1 : 1;

        int leftFrontTarget = leftFront.getCurrentPosition() + moveCounts * dirFL;
        int rightFrontTarget = rightFront.getCurrentPosition() + moveCounts * dirFR;
        int leftRearTarget = leftRear.getCurrentPosition() + moveCounts * dirRL;
        int rightRearTarget = rightRear.getCurrentPosition() + moveCounts * dirRR;

        // Set Target and Turn On RUN_TO_POSITION
        leftFront.setTargetPosition(leftFrontTarget);
        rightFront.setTargetPosition(rightFrontTarget);
        leftRear.setTargetPosition(leftRearTarget);
        rightRear.setTargetPosition(rightRearTarget);
    }

    public void setDrivePower(double pLeftFront, double pLeftRear, double pRightFront, double pRightRear) {
        leftFront.setPower(pLeftFront);
        leftRear.setPower(pLeftRear);
        rightFront.setPower(pRightFront);
        rightRear.setPower(pRightRear);
    }
}
