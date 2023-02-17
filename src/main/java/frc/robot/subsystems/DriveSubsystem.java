// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.sensors.Pigeon2Configuration;
import com.ctre.phoenix.sensors.WPI_Pigeon2;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.ModuleConstants;
import frc.robot.subsystems.Util.AimUtil;

public class DriveSubsystem extends SubsystemBase {
  // SwerveDrivePoseEstimator
  // Robot swerve modules
  private final SwerveModule m_frontLeft =
      new SwerveModule(
          DriveConstants.kFrontLeftDriveMotorPort,
          DriveConstants.kFrontLeftTurningMotorPort,
          DriveConstants.kFrontLeftTurningMotorEncoderChannel,
          DriveConstants.kFrontLeftDriveEncoderReversed,
          DriveConstants.kFrontLeftTurningEncoderReversed,
          DriveConstants.kOutputRever1,
          DriveConstants.kDriveReverse1,
          ModuleConstants.kCancoderOffset1,
          0.12,
          0.01
          );

  private final SwerveModule m_frontRight =
      new SwerveModule(
          DriveConstants.kFrontRightDriveMotorPort,
          DriveConstants.kFrontRightTurningMotorPort,
          DriveConstants.kFrontRightTurningMotorEncoderChannel,
          DriveConstants.kFrontRightDriveEncoderReversed,
          DriveConstants.kFrontRightTurningEncoderReversed,
          DriveConstants.kOutputRever2,
          DriveConstants.kDriveReverse2,
          ModuleConstants.kCancoderOffset2,
          0.12,
          0.01
          );
  private final SwerveModule m_rearLeft =
      new SwerveModule(
          DriveConstants.kRearLeftDriveMotorPort,
          DriveConstants.kRearLeftTurningMotorPort,
          DriveConstants.kRearLeftTurningMotorEncoderChannel,
          DriveConstants.kRearLeftDriveEncoderReversed,
          DriveConstants.kRearLeftTurningEncoderReversed,
          DriveConstants.kOutputRever3,
          DriveConstants.kDriveReverse3,
          ModuleConstants.kCancoderOffset3,
          0.12,
          0.01
          );


  private final SwerveModule m_rearRight =
      new SwerveModule(
          DriveConstants.kRearRightDriveMotorPort,
          DriveConstants.kRearRightTurningMotorPort,
          DriveConstants.kRearRightTurningMotorEncoderChannel,
          DriveConstants.kRearRightDriveEncoderReversed,
          DriveConstants.kRearRightTurningEncoderReversed,
          DriveConstants.kOutputRever4,
          DriveConstants.kDriveReverse4,
          ModuleConstants.kCancoderOffset4,
          0.12,
          0.01
          );
                    


  public HolonomicDriveController controller = new HolonomicDriveController(
    new PIDController(0.14, 0, 0.24), //x
    new PIDController(0.8, 0, 0.187), //y
    new ProfiledPIDController(0.13, 0, 0, AutoConstants.kThetaControllerConstraints));
  // The gyro sensor
  private final WPI_Pigeon2 m_gyro = new WPI_Pigeon2(DriveConstants.kPigeon2Port);
  // Odometry class for tracking robot 

    SwerveDrivePoseEstimator mDrivePoseEstimator =
      new SwerveDrivePoseEstimator(       
      DriveConstants.kDriveKinematics,
      m_gyro.getRotation2d(),
      new SwerveModulePosition[] {
        m_frontLeft.getPosition(),
        m_frontRight.getPosition(),
        m_rearLeft.getPosition(),
        m_rearRight.getPosition()},
       new Pose2d()
      );
      

  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    m_gyro.configFactoryDefault();
    Pigeon2Configuration config = new Pigeon2Configuration();
    config.MountPosePitch = 0;
    config.MountPoseRoll = 0;
    config.MountPoseYaw = 0;
    config.EnableCompass = false;
    config.DisableNoMotionCalibration=false;
    config.DisableTemperatureCompensation=false;
    config.enableOptimizations= false;
    m_gyro.configAllSettings(config);
    m_gyro.setYaw(0);
  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    mDrivePoseEstimator.update(
        m_gyro.getRotation2d(),
        new SwerveModulePosition[] {
          m_frontLeft.getPosition(),
          m_frontRight.getPosition(),
          m_rearLeft.getPosition(),
          m_rearRight.getPosition()
        });
        
    // addVisionMeasurement()
     SmartDashboard.putNumber("x", mDrivePoseEstimator.getEstimatedPosition().getX());
     SmartDashboard.putNumber("Y", mDrivePoseEstimator.getEstimatedPosition().getY());
     SmartDashboard.putNumber("Pitch",m_gyro.getPitch());
     SmartDashboard.putNumber("Yaw", m_gyro.getYaw());
     SmartDashboard.putNumber("roll", m_gyro.getRoll());
     SmartDashboard.putNumber("degrees", m_frontLeft.getTurningEncoderAngle());
     //SmartDashboard.putNumber("error", m_frontLeft.getError());
  }
  public boolean isLevel() {
    return Math.abs(m_gyro.getPitch()) < 2 && Math.abs(m_gyro.getRoll()) < 2;
  }
  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  
  public Pose2d getPose() {
    //return null;
    return mDrivePoseEstimator.getEstimatedPosition();
  }
  
  public void zeroyaw() {
    m_gyro.setYaw(0);
  }
  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    mDrivePoseEstimator.resetPosition(
        m_gyro.getRotation2d(),
        new SwerveModulePosition[] {
          m_frontLeft.getPosition(),
          m_frontRight.getPosition(),
          m_rearLeft.getPosition(),
          m_rearRight.getPosition()
        },
        pose);
  }
  SlewRateLimiter limiter1 = new SlewRateLimiter(1.5, -1.5, 0);
  SlewRateLimiter limiter2 = new SlewRateLimiter(1.5, -1.5, 0);

  public Runnable drive;

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed Speed of the robot in the x direction (forward).
   * @param ySpeed Speed of the robot in the y direction (sideways).
   * @param rot Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the field.
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    // deadzone
    if(Math.abs(xSpeed) < 0.1 && Math.abs(ySpeed) < 0.1 && Math.abs(rot) < 0.25) {
      xSpeed = 0;
      ySpeed = 0;
      rot = 0;
    }
    xSpeed = limiter1.calculate(xSpeed);
    ySpeed = limiter2.calculate(ySpeed);
    

    var swerveModuleStates =
        DriveConstants.kDriveKinematics.toSwerveModuleStates(
            fieldRelative
                ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, m_gyro.getRotation2d())
                : new ChassisSpeeds(xSpeed, ySpeed, rot));
                  
    setModuleStates(swerveModuleStates);
    // SmartDashboard.putNumber("error", xSpeed - m_frontLeft.getDriveEncoderVelocity());
  }
  

  /**
   * Sets the swerve ModuleStates.
   *
   * @param desiredStates The desired SwerveModule states.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.desaturateWheelSpeeds(
        desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(desiredStates[0]);
    m_frontRight.setDesiredState(desiredStates[1]);
    m_rearLeft.setDesiredState(desiredStates[2]);
    m_rearRight.setDesiredState(desiredStates[3]);
  }

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_rearLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_rearRight.resetEncoders();
  }
double x, y;
  public void level(){

    if (m_gyro.getRoll() > 2){
      y=-0.2;
    } 
    else if (m_gyro.getRoll() <-2){
      y=0.2;
    }
    else y=0;
  
    if (m_gyro.getPitch() > 2){
      x=-0.2;
    }
    else if (m_gyro.getPitch() <-2){
      x=0.2;
    }
    else x=0;

      drive(y, x, 0, false);
    }

  public boolean isleveled(){
    return Math.abs(m_gyro.getRoll())<2 && Math.abs(m_gyro.getPitch())<2;
  }
  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    m_gyro.reset();
  }

  /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeading() {
    return m_gyro.getRotation2d().getDegrees();
  }

  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return m_gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }
  public void leftTarget() {
    Pose2d curr = getPose();
    double y = AimUtil.getLeftTargetY(curr);
    Pose2d target = new Pose2d(curr.getX(), y, Rotation2d.fromDegrees(0));
    var chassisSpeed = controller.calculate(curr, target, 0.4, curr.getRotation());

    drive(chassisSpeed.vxMetersPerSecond, 
          chassisSpeed.vyMetersPerSecond, 
          chassisSpeed.omegaRadiansPerSecond, 
          false);
  }

  public void rightTarget() {
    Pose2d curr = getPose();
    double y = AimUtil.getRightTargetY(curr);
    Pose2d target = new Pose2d(curr.getX(), y, Rotation2d.fromDegrees(0));
    var chassisSpeed = controller.calculate(curr, target, 0.4, curr.getRotation());

    drive(chassisSpeed.vxMetersPerSecond, 
          chassisSpeed.vyMetersPerSecond, 
          chassisSpeed.omegaRadiansPerSecond, 
          false);
  }

  public void midTarget(){
    Pose2d curr = getPose();
    double y =AimUtil.getMidTargetY(curr);
    Pose2d target =new Pose2d(curr.getX(), y, Rotation2d.fromDegrees(0));
    var chassisSpeed = controller.calculate(curr, target, 0.4, curr.getRotation());

    drive(chassisSpeed.vxMetersPerSecond,
          chassisSpeed.vyMetersPerSecond,
          chassisSpeed.omegaRadiansPerSecond,
          false);
  }
}
