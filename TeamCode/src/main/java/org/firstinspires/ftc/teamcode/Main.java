package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.MathEx.DEG2RAD;
import static org.firstinspires.ftc.teamcode.MathEx.boolToInt;

import org.firstinspires.ftc.teamcode.Omnidrive;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name = "Main OpMode", group = "Main")
public class Main extends OpMode {
  
  //variables for motors
  DcMotor left_drive = null;
  DcMotor right_drive = null;
  
  //variables for sensors
  BNO055IMU gyro = null;
  
  Omnidrive drive;
  DcMotor[] driveMotors;
  
  void init_motors() {
    int motorCount = 3;
    
    this.drive = new Omnidrive(motorCount);
    this.driveMotors = new DcMotor[motorCount];
    
    for (int i = 0; i < motorCount; i++) {
      this.driveMotors[i] = this.hardwareMap.get(DcMotor.class, String.format("drive_%d", i, 0.1f));
    }
  }
  
  void init_sensors() {
    
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
  
  //controller left stick x,y
  private Vec2 inputDirection;
  //magnitude of left stick x,y
  private float inputMagnitude;
  
  private float desiredHeadingRadians;
  private float actualHeadingRadians;
  
  //stop buttons firing too fast to toggle
  private Debounce dAlign;
  //align mode, toggled with A button in loop()
  private boolean align;
  
  @Override
  public void init() {
    
    //create a vector to store our left stick x,y
    this.inputDirection = new Vec2();
    this.inputMagnitude = 0;
    
    //300 milliseconds between A being pressed before triggering again
    this.dAlign = new Debounce(300);
    
    //align mode, keeps to desired orientation (turn) when set to true
    //essentially compass based turning
    this.align = false;
    
    //where we want to be facing
    this.desiredHeadingRadians = 0;
    //where we're actually facing
    this.actualHeadingRadians = 0;
    
    //say hello
    this.telemetry.addData("main", "Hello World");
    this.telemetry.update();
    
    //init motors so we can use them later
    this.init_motors();
    
    //init sensors we read from
    this.init_sensors();
  }
  
  Orientation o;
  
  void update_sensors() {
    this.o = this.gyro.getAngularOrientation();
    
    this.actualHeadingRadians = this.o.firstAngle;
  }
  
  float driveDirectionRadians = 0;
  float headingTolerance = MathEx.DEG2RAD * 5f;
  float headingAdjust = 0;
  float turnRaw = 0;
  float rad360 = MathEx.DEG2RAD * 360;
  
  Debounce dPerpTurn = new Debounce(200);
  
  void handle_input() {
    
    //DRIVING DIRECTION / SPEED
    //copy stick direction
    this.inputDirection.set(
      this.gamepad1.left_stick_x,
      this.gamepad1.left_stick_y
    );
    
    //calculate magnitude of direction
    this.inputMagnitude = this.inputDirection.magnitude();
    
    //get rid of magnitude from actual direction vector
    this.inputDirection.normalize();
    
    this.driveDirectionRadians = this.inputDirection.arctan2();
    
    
    //HEADING / TURNING
    this.turnRaw = this.gamepad1.right_stick_x;
    
    if (Float.isNaN(this.turnRaw)) this.turnRaw = 0.0f; //TODO - check if necessary
    
    //change desired heading based on joystick
    this.desiredHeadingRadians -= this.turnRaw / 25.0f; //divide by 25 to slow the rate down, TODO - make adjustable
    this.desiredHeadingRadians %= MathEx.DEG2RAD * 360;
    
    //left/right bumper change heading angle by 90deg
    int turn = boolToInt(this.gamepad1.right_bumper) - boolToInt(this.gamepad1.left_bumper);
    if (turn != 0 && this.dPerpTurn.update()) {
      this.desiredHeadingRadians -= (90f * turn)*DEG2RAD;
    }
    
    //calculate align mode heading (even if not used while updating motors)
    this.headingAdjust = 0;
    float headingDiff = (float) Math.atan2(
      Math.sin(
        this.desiredHeadingRadians - this.actualHeadingRadians
      ),
      Math.cos(this.desiredHeadingRadians - this.actualHeadingRadians)
    );
    if (Math.abs(headingDiff) > headingTolerance) this.headingAdjust = (headingDiff / rad360) * 2;
    if (this.headingAdjust < -1) this.headingAdjust = -1;
    if (this.headingAdjust > 1) this.headingAdjust = 1;
    
    //toggle align mode based on gamepad A button and debounce settings
    if (this.gamepad1.a && this.dAlign.update()) this.align = !this.align;
    
    //log stuff if in align mode
    if (this.align) {
      this.telemetry.addData("Heading", "Desired: %.2f, Actual: %.2f, Adjust: %.2f",
        this.desiredHeadingRadians,
        this.actualHeadingRadians,
        headingAdjust
      );
    }
  }
  
  void update_motors() {
    //send orientation as text so we can see it
    this.telemetry.addData(
      "Orientation", "{ x: %.2f, y: %.2f, z: %.2f }",
      o.firstAngle,
      o.secondAngle,
      o.thirdAngle
    );
    
    //calculate drive.motorOutput array
    this.drive.update(
      this.driveDirectionRadians, this.inputMagnitude
    );
    
    //store variable for motor power
    float motorPower = 0;
    
    //apply drive calculations to physical motors
    for (int i = 0; i < this.drive.motorOutput.length; i++) {
      
      //if in align mode, add motor output to headingAdjust so we can match gyro
      if (this.align) {
        motorPower = (this.drive.motorOutput[i] / 2) + this.headingAdjust;
        if (Float.isNaN(motorPower)) motorPower = this.headingAdjust;
        
      } else { //otherwise just bias the motor output with the raw turn value from the joystick
        motorPower = (this.drive.motorOutput[i] / 2) - (this.turnRaw / 2);
        if (Float.isNaN(motorPower)) motorPower = -(this.turnRaw / 2);
      }
      
      //finally output the motor power to the specific motor
      this.driveMotors[i].setPower(motorPower);
    }
  }
  
  @Override
  public void loop() {
    //read from sensors (gyro)
    this.update_sensors();
    
    //read and calculate gamepad inputs
    this.handle_input();
    
    //output to motors
    this.update_motors();
    
    //anything that needs logged will now do so
    this.telemetry.update();
    
  }
}
