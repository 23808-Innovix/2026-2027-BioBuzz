package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


@TeleOp(name = "Combined TeleOp", group = "TeleOp")
public class CombinedTeleOp extends OpMode {
    private Follower follower;
    public static Pose startingPose;
    private boolean driveArmed;
    private TelemetryManager telemetryM;
    private boolean slowMode;
    private final double slowModeMultiplier = 0.5;
    private static final double TURN_SENSITIVITY = 1.5;

    private DcMotorEx driveLf;
    private DcMotorEx driveRf;
    private DcMotorEx driveLr;
    private DcMotorEx driveRr;
    private DcMotorEx intakeMotorLeft;
    private DcMotorEx intakeMotorRight;
    private DcMotorEx transMotor;
    private ShooterSubsystem shooterSubsystem;
    private static final double SHOOTER_TARGET_TICKS_PER_SECOND = 1250.0;
    private boolean transferFeeding;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose(72, 72, 0) : startingPose);
        follower.update();
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        driveLf = hardwareMap.get(DcMotorEx.class, "lf");
        driveRf = hardwareMap.get(DcMotorEx.class, "rf");
        driveLr = hardwareMap.get(DcMotorEx.class, "lr");
        driveRr = hardwareMap.get(DcMotorEx.class, "rr");
        intakeMotorLeft = hardwareMap.get(DcMotorEx.class, "intakeMotorLeft");
        intakeMotorRight = hardwareMap.get(DcMotorEx.class, "intakeMotorRight");
        // Reverse one side so both intake rollers turn in the same physical direction.
        intakeMotorRight.setDirection(DcMotorSimple.Direction.REVERSE);
        transMotor = hardwareMap.get(DcMotorEx.class, "transMotor");
        DcMotorEx shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        shooterSubsystem = new ShooterSubsystem(shooterMotor);
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        follower.update();
        telemetryM.update();

        //Rest pose
        if (gamepad1.leftBumperWasPressed()) {
            follower.setPose(new Pose(follower.getPose().getX(), follower.getPose().getY(), 0));
        }
        if (gamepad1.left_trigger > 0) {
            follower.setPose(new Pose(follower.getPose().getX(), follower.getPose().getY(), Math.PI));
        }

        if (gamepad1.rightBumperWasPressed()) {
            slowMode = !slowMode;
        }
        //Normal Teleop driving
        if (!automatedDrive) {
            if (!slowMode) follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    gamepad1.right_stick_x, //change turning (its opposite); turn faster
                    false // Robot Centric
            );
        }
//        // Press gamepad 1 Start to arm or disarm drivetrain control.
//        if (gamepad1.startWasPressed()) {
//            driveArmed = !driveArmed;
//            follower.startTeleopDrive();
//        }
//
//        if (driveArmed) {
//            double driveScale = slowMode ? slowModeMultiplier : 1.0;
//            double forward = gamepad1.dpad_up ? 1.0
//                    : gamepad1.dpad_down ? -1.0
//                      : -DriveInput.applyDeadzone(gamepad1.left_stick_x);
//            double strafe = gamepad1.dpad_right ? 1.0
//                    : gamepad1.dpad_left ? -1.0
//                      : -DriveInput.applyDeadzone(gamepad1.left_stick_y);
//            double turn = Math.max(-1.0, Math.min(1.0,
//                    -DriveInput.applyDeadzone(gamepad1.right_stick_x) * TURN_SENSITIVITY));
//            follower.setTeleOpDrive(
//                    forward * driveScale,
//                    strafe * driveScale,
//                    turn * driveScale,
//                    true
//            );
//        } else {
//            follower.setTeleOpDrive(0.0, 0.0, 0.0, true);
//        }

        // Gamepad 2: left bumper intakes, right bumper reverses both intake motors.
        double intakePower = gamepad2.left_bumper ? 0.70 : (gamepad2.right_bumper ? -0.4 : 0.0);
        intakeMotorLeft.setPower(intakePower);
        intakeMotorRight.setPower(intakePower);

        // Hold the right trigger to spin up. Feed automatically once the shooter is near target speed.
        boolean shooterRequested = gamepad2.right_trigger > 0;
        if (shooterRequested) {
            shooterSubsystem.setVelocityTicksPerSecond(SHOOTER_TARGET_TICKS_PER_SECOND);
        } else {
            shooterSubsystem.stop();
        }

        double shooterVelocity = Math.abs(shooterSubsystem.getVelocityTicksPerSecond()); //change shooter speed - too fast
        if (!shooterRequested) {
            transferFeeding = false;
        } else if (!transferFeeding && shooterVelocity >= SHOOTER_TARGET_TICKS_PER_SECOND * 0.96) {
            transferFeeding = true;
        } else if (transferFeeding && shooterVelocity < SHOOTER_TARGET_TICKS_PER_SECOND * 0.96) {
            transferFeeding = false;
        }
        transMotor.setPower(transferFeeding ? 0.65 : 0.0);
        //2nd/3rd less high

        telemetryM.debug("position", follower.getPose());
        telemetryM.debug("velocity", follower.getVelocity());
        telemetry.addData("LF power/speed", "%.2f / %.0f", driveLf.getPower(), Math.abs(driveLf.getVelocity()));
        telemetry.addData("RF power/speed", "%.2f / %.0f", driveRf.getPower(), Math.abs(driveRf.getVelocity()));
        telemetry.addData("LR power/speed", "%.2f / %.0f", driveLr.getPower(), Math.abs(driveLr.getVelocity()));
        telemetry.addData("RR power/speed", "%.2f / %.0f", driveRr.getPower(), Math.abs(driveRr.getVelocity()));
        telemetry.addData("drive armed (press Start)", driveArmed);
        telemetry.addData("slow mode", slowMode);
        telemetry.addData("intake power", intakePower);
        telemetry.addData("shooter velocity", shooterSubsystem.getVelocityTicksPerSecond());
        telemetry.addData("shooter target", SHOOTER_TARGET_TICKS_PER_SECOND);
        telemetry.addData("transfer feeding", transferFeeding);
        telemetry.update();
    }

    @Override
    public void stop() {
        intakeMotorLeft.setPower(0);
        intakeMotorRight.setPower(0);
        transMotor.setPower(0);
        shooterSubsystem.stop();
    }
}
