package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.vuforia.Vec2F;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.MotionDetection;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name="Main OpMode", group="Main")
public class Main extends OpMode {
  
  //variables for motors
  DcMotor left_drive = null;
  DcMotor right_drive = null;
  
  //variables for sensors
  BNO055IMU gyro = null;
  
  Omnidrive drive;
  DcMotor[] driveMotors;
  
  void init_motors () {
    int motorCount = 3;
    
    this.drive = new Omnidrive(motorCount);
    this.driveMotors = new DcMotor[motorCount];
    
    for (int i=0; i<motorCount; i++) {
      this.driveMotors[i] = this.hardwareMap.get(DcMotor.class, String.format("drive_%d", i) );
    }
    
    this.telemetry.addData("Drive", "Angle radians %.2f, %.2f, %.2f",
      this.drive.motorAnglesRadians[0],
      this.drive.motorAnglesRadians[1],
      this.drive.motorAnglesRadians[2]
    );
    this.telemetry.update();
    
  }
  
  void init_sensors () {
    
    //init 9 axis gyroscope (built into controller)
    
    //options on how the gyro should work
    BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
    
    //how the angles should be represented (degrees or radians)
    parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
    
    //get the gyro
    this.gyro = hardwareMap.get(BNO055IMU.class, "imu");
    
    //init with the options we want
    this.gyro.initialize(parameters);
    
  }
  
  private Vec2 inputDirection;
  private float inputMagnitude;
  
  @Override
  public void init() {
    
    this.inputDirection = new Vec2();
    this.inputMagnitude = 0;
    
    //say hello
    this.telemetry.addData("main", "Hello World");
    this.telemetry.update();
  
    //init motors so we can use them later
    this.init_motors();
    
    //init sensors we read from
    this.init_sensors();
  }
  
  
  
  @Override
  public void loop() {
    
    //get direction
    Orientation o = this.gyro.getAngularOrientation();
    
    //send orientation as text so we can see it
    this.telemetry.addData(
        "Orientation", "{ x: %.2f, y: %.2f, z: %.2f }", + o.firstAngle, o.secondAngle, o.thirdAngle
    );
    
    //copy stick direction
    this.inputDirection.set(
      this.gamepad1.left_stick_x,
      this.gamepad1.left_stick_y
    );
    
    //calculate magnitude of direction
    this.inputMagnitude = this.inputDirection.magnitude();
    
    //get rid of magnitude from actual direction vector
    this.inputDirection.normalize();
    
    float angleRadians = this.inputDirection.arctan2();
    
    this.drive.update(
      angleRadians, this.inputMagnitude
    );
    
    float turn = this.gamepad1.right_stick_x;
    
    this.telemetry.addData("Turn", "Value : %.2f", turn);
    this.telemetry.update();
    
    float motorPower = 0;
    
    //apply drive calculations to physical motors
    for (int i=0; i<this.drive.motorOutput.length; i++) {
      motorPower = (this.drive.motorOutput[i] / 2) - (turn / 2);
      
      
      this.driveMotors[i].setPower( motorPower );
    }
  
    this.telemetry.addData("Drive output", "%.2f, %.2f, %.2f",
      this.drive.motorOutput[0],
      this.drive.motorOutput[1],
      this.drive.motorOutput[2]
    );
    this.telemetry.update();
  
    
    
    //check if A is pressed on gamepad 1
    if (this.gamepad1.a) {
    
    }
  }
}
