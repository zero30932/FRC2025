package frc.robot.subsystems;


import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Flags;

public class CoralIntakeSubsystem extends SubsystemBase {
    private static final double BACK_LIMIT = 0.03;
    private static final double FRONT_LIMIT = 0.55;

    private final SparkMax coralPivotMotor;
    private final RelativeEncoder coralPivotEncoder;
    private final AbsoluteEncoder coralPivotAbsoluteEncoder;

    private final SparkClosedLoopController coralPivotPIDController;

    private final SparkMax coralIntakeMotor;

    public CoralIntakeSubsystem() {
        coralPivotMotor = new SparkMax(Constants.PortConstants.CAN.CORAL_PIVOT_MOTOR_ID, MotorType.kBrushless);
        coralPivotEncoder = coralPivotMotor.getEncoder();
        coralPivotAbsoluteEncoder = coralPivotMotor.getAbsoluteEncoder();
        // throughboreEncoder = new ThroughboreEncoder(Constants.PortConstants.DIO.CORAL_ABSOLUTE_ENCODER_ABS_ID, 0, false);
        coralPivotPIDController = coralPivotMotor.getClosedLoopController();

        SparkMaxConfig coralPivotMotorConfig = new SparkMaxConfig();

        coralPivotMotorConfig
                .inverted(false)
                .idleMode(SparkBaseConfig.IdleMode.kBrake)
                .voltageCompensation(12);
        coralPivotMotorConfig.absoluteEncoder
                .setSparkMaxDataPortConfig()
                .zeroOffset(0.6);
        coralPivotMotorConfig.encoder
                .positionConversionFactor(1d/48)
                .velocityConversionFactor(1d/48/60);
        coralPivotMotorConfig.closedLoop
                .feedbackSensor(ClosedLoopConfig.FeedbackSensor.kAbsoluteEncoder)
                .pidf(2.5, 0, 0, 0)
                .outputRange(-0.4, 0.4);
        coralPivotMotorConfig.closedLoop.maxMotion
                .maxVelocity(0.5 * 60)
                .maxAcceleration(0.5 * 60);

        coralPivotEncoder.setPosition(coralPivotAbsoluteEncoder.getPosition());
        coralPivotMotor.configure(coralPivotMotorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);


        coralIntakeMotor = new SparkMax(Constants.PortConstants.CAN.CORAL_INTAKE_MOTOR_ID, MotorType.kBrushless);
        SparkMaxConfig coralIntakeMotorConfig = new SparkMaxConfig();
        coralIntakeMotorConfig
                .inverted(true)
                .idleMode(SparkBaseConfig.IdleMode.kBrake)
                .smartCurrentLimit(20)
                .voltageCompensation(12);
        coralIntakeMotor.configure(coralIntakeMotorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);

        // throughboreEncoder.name = "climber";
        // front limit 0.55 rot
        // back limit 0.01 rot
    }

    @Override
    public void periodic() {
        // System.out.println(coralPivotEncoder.getPosition());
        System.out.println(this.coralPivotAbsoluteEncoder.getPosition());
        // throughboreEncoder.periodic();
    }

    public void setPivotTargetAngle(Rotation2d target) {
        if (Flags.CoralIntake.ENABLED) {
            double rot = MathUtil.clamp(target.getRotations(), BACK_LIMIT, FRONT_LIMIT);
            // System.out.println("i set the target angle to " + rot);
            coralPivotPIDController.setReference(rot, SparkBase.ControlType.kPosition);
        }
    }

    public void setIntakeSpeed(double speed) {
        if(Flags.CoralIntake.ENABLED) {
            coralIntakeMotor.set(speed);
        }
    }

    public void setRawSpeed(double speed) {
        if (Flags.CoralIntake.ENABLED) {
            coralPivotMotor.set(speed);
        }
    }

    public Rotation2d getThroughboreEncoderDistance() {
        return new Rotation2d();// throughboreEncoder.getTotalDistance();
    }
}
