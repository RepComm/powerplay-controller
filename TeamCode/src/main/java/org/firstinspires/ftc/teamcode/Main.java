package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Main extends OpMode {
  
  //variables for motors
  DcMotor left_drive = null;
  DcMotor right_drive = null;
  
  //variables for sensors
  BNO055IMU gyro = null;
  
  
  void init_motors () {
    this.left_drive = this.hardwareMap.get(DcMotor.class, "left_drive");
    this.right_drive = this.hardwareMap.get(DcMotor.class, "right_drive");
  
    this.left_drive.setDirection(DcMotor.Direction.REVERSE);
    this.right_drive.setDirection(DcMotor.Direction.FORWARD);
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
  
  @Override
  public void init() {
    
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
    
    //check if A is pressed on gamepad 1
    if (this.gamepad1.a) {
      //log some text back so we can see
      this.telemetry.addData("some caption here", "A is pressed, neat stuff..");
      //notify system that we added that text and that it can log it for us
      this.telemetry.update();
    }
  
    //get direction
    Orientation o = this.gyro.getAngularOrientation();
    
    //send orientation as text so we can see it
    this.telemetry.addData(
        "Orientation", "{ x: %2.0f , y: %2.0f , z: %2.0f }",
        o.firstAngle, o.secondAngle, o.thirdAngle
    );
    //update system to display text
    this.telemetry.update();
    
  }
}
