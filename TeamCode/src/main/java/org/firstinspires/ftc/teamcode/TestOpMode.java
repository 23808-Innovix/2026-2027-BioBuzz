package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class TestOpMode extends OpMode {
    private Servo spindexerServo;
    private DcMotorEx intakeMotor;
    private SpindexerSubsystem spindexerSubsystem;
    private DcMotorEx shooterMotor;
    private Servo serializerServo;

    private ShooterSubsystem shooterSubsystem;
    public void init() {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
    }
    public void start() {

    }
    public void loop() {

        if (gamepad2.b) {
            intakeMotor.setPower(-0.65);
        } else if(gamepad2.left_bumper){
            intakeMotor.setPower(0.3);
        }
        else {
            intakeMotor.setPower(0);
        }

        if(gamepad2.right_trigger>0){
            shooterSubsystem.setPowerTo(0.8);
        }
        else{
            shooterSubsystem.stop();
        }

    }
    public void stop() {

    }
}
