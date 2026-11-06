package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class ShooterSubsystem {
    private static final double MOTOR_TICKS_PER_REV = 28.0;
    // The 19.2:1 reduction means the motor turns 19.2 times per shooter-output revolution.
    private static final double MOTOR_REVS_PER_OUTPUT_REV = 19.2;

    private final DcMotorEx shooterMotor;

    public ShooterSubsystem(DcMotorEx shooterMotor) {
        this.shooterMotor = shooterMotor;
        this.shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void revToRPM(double rpm) {
        shooterMotor.setVelocity(rpmToTicksPerSecond(rpm));
    }

    public void setVelocityTicksPerSecond(double ticksPerSecond) {
        shooterMotor.setVelocity(ticksPerSecond);
    }

    public double getVelocityTicksPerSecond() {
        return shooterMotor.getVelocity();
    }

    public void setPowerTo(double power) {
        shooterMotor.setPower(Math.max(-1.0, Math.min(1.0, power)));
    }

    public void stop() {
        shooterMotor.setPower(0);
    }

    public double rpmToTicksPerSecond(double rpm) {
        return rpm * MOTOR_TICKS_PER_REV * MOTOR_REVS_PER_OUTPUT_REV / 60.0;
    }
}
