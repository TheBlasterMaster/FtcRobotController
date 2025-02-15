package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
public class AutonomousOpMode extends LinearOpMode {
    //ElaspedTime Object allows us to use Time (Unused in this program)
    private ElapsedTime runtime = new ElapsedTime();

    //----------- HARDWARE
    private RobotHardware robot = new RobotHardware();

    //Creates New Autonomous Wheel Objects . 
    //Does the calculations for how many turns motor needs to do for robot to go X inches forward
    private AutonomousWheelMotor frontLeftDrive;
    private AutonomousWheelMotor frontRightDrive;
    private AutonomousWheelMotor backLeftDrive;
    private AutonomousWheelMotor backRightDrive;

    //The Autonomous Movement Command has been programmed to work with an array of motors so that it can work with
    //any amount of motors. The movement array is an array of all 4 of our drivetrain motors.
    private AutonomousWheelMotor[] movement;

    // --------- CONSTANTS
    //When a robot turns in place, its wheels trace a circle. The Diameter of the Circle is the distance between the wheels
    //The wheels have to move the distance along the circle to turn. The follwoing calculates that.
    private final double widthOfRobot = 2.1*16;
    private final double robotTurningCircumference = widthOfRobot * Math.PI;

    //Compensates for the gear ratio of the wheels and for slippage in the wheels
    private final double gearAndFrictionConstant = 0.31604;
    // ----------


    //------
    @Override
    public void runOpMode() throws InterruptedException { 
        robot.init(hardwareMap);

        robot.frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        robot.backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        robot.frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        robot.backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        //Creates New Autonomous Wheel Objects (The file to this class is in this same folder).  
        //Does the calculations for how many turns motor needs to do for robot to go X inches forward
        frontLeftDrive = new AutonomousWheelMotor(robot.frontLeftDrive, 2240, 2.95276);
        frontRightDrive = new AutonomousWheelMotor(robot.frontRightDrive, 2240, 2.95276);
        backLeftDrive = new AutonomousWheelMotor(robot.backLeftDrive, 2240, 2.95276);
        backRightDrive = new AutonomousWheelMotor(robot.backRightDrive, 2240, 2.95276);
    
        AutonomousWheelMotor[] Tempmovement = {frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive};
        
        movement = Tempmovement;
        telemetry.addData("Status", "Initialized");
        
        robot.gripper.setPosition(0);
        
        waitForStart();

        //EXAMPLE AUTONOMOUS CODE

        //Move Robot forward 8 inches at 50% speed
        //manualMove(8,8,8,8,0.5);

        //Turn Robot 90 Degrees at 50% speed
        //turn(90,0.5);

        //Move 8 Inches at -18 Degrees at 50% speed
        //move(-18,8,0.5);
        
    }


    /**
     * autonomousMovementCommand moves all provided motors and servos while taking into mind gear rations and friction.
     * @param args a list of all motors to turn
     * @param inches the inches that each motor will move the robot. This array corresponds to args.
     * @param speed a value between 0 and 1 that represents the speed the motors move at to reach their goal
     */
    public void autonomousMovementCommand(AutonomousWheelMotor[] args, double[] inches, double speed) {

        //Deleted Code: if (opModeIsActive()) {

        //Holds the target encoder value for every motor
        double targets[] = new double[args.length];

        //Sets every motor to run to the correct end position
        for (int i = 0; i<args.length; i++) {
            AutonomousWheelMotor currentMotor = args[i];

            int newTarget = currentMotor.motor.getCurrentPosition() + (int) (inches[i] * gearAndFrictionConstant * currentMotor.COUNTS_PER_INCH);
            targets[i] = (int) (inches[i] * currentMotor.COUNTS_PER_INCH);
            currentMotor.motor.setTargetPosition(newTarget);
            currentMotor.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        //Turns every number into a proportional number between 0-1. Used for the speed values, and makes every motor finish at the same time
        targets = normalizeList(targets,speed);

        //Set The Speeds to each motor
        for (int i = 0; i<args.length; i++) {
            AutonomousWheelMotor currentMotor = args[i];
            currentMotor.motor.setPower(targets[i]);
        }
        
        //Wait for all motors to finish activity
        //Delted Code: whileOpModeIsActive
        while (args[0].motor.isBusy()) {

        }

        //Stop Each Motor
        for (AutonomousWheelMotor currentMotor : args) {
            currentMotor.motor.setPower(0);
            currentMotor.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        //}

    }
    /**
     * autonomousCommand moves all provided motors and servos.
     * @param args a list of all motors to turn
     * @param inches the inches that each motor will move the robot. This array corresponds to args.
     * @param speed a value between 0 and 1 that represents the speed the motors move at to reach their goal
     */
    public void autonomousCommand(AutonomousWheelMotor[] args, double[] inches, double speed) {
        
        //Deleted: if (opModeIsActive()) {

        //Calculate Speed and Encoder Target for each motor
        double targets[] = new double[args.length];

        //Sets every motor to run to the correct end position
        for (int i = 0; i<args.length; i++) {
            AutonomousWheelMotor currentMotor = args[i];

            int newTarget = currentMotor.motor.getCurrentPosition() + (int) (inches[i] * currentMotor.COUNTS_PER_INCH);
            targets[i] = (int) (inches[i] * currentMotor.COUNTS_PER_INCH);
            currentMotor.motor.setTargetPosition(newTarget);
            currentMotor.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        //Turns every number into a proportional number between 0-1. Used for the speed values, and makes every motor finish at the same time
        targets = normalizeList(targets,speed);

        //Set The Speeds to each motor
        for (int i = 0; i<args.length; i++) {
            AutonomousWheelMotor currentMotor = args[i];
            currentMotor.motor.setPower(targets[i]);
        }

        //Wait for all motors to finish activity
        //Deleted : whileOpModeIsActive
        while (args[0].motor.isBusy()) {

        }

        //Stop Each Motor
        for (AutonomousWheelMotor currentMotor : args) {
            currentMotor.motor.setPower(0);
            currentMotor.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        //}

    }

    /**
     * manualMove turns each individual wheel the given amount of inches.
     * @param topLeft the amount of inches the top left wheel covers
     * @param topRight the amount of inches the top left wheel covers
     * @param bottomLeft the amount of inches the top left wheel covers
     * @param bottomRight the amount of inches the top left wheel covers
     * @param speed the speed of all four wheels (0-1)
     */
    public void manualMove(double topLeft, double topRight, double bottomLeft, double bottomRight, double speed){
        double inches[] = {topLeft,topRight,bottomLeft,bottomRight};
        autonomousMovementCommand(movement,inches,speed);
    }

    /**
     * turn rotates the robot around it's center.
     * @param degrees the amount of rotation relavtive the current robots rotation
     * @param speed the speed of turning (0-1)
     */
    public void turn(double degrees,double speed){
        double turningInches = robotTurningCircumference * (degrees/360);
        turningInches /= 2;
        double inches[] = {turningInches,-turningInches,turningInches,-turningInches};
        autonomousMovementCommand(movement,inches,speed);
    }

    /**
     * move strafes the robot in a given amount of degrees
     * @param degrees the direction of movement relative to the robot's current rotation
     * @param inches the distance of movement
     * @param speed the speed of movement (0-1)
     */
    public void move(double degrees, double inches, double speed){
        double robotAngle = Math.toRadians(-degrees) + Math.PI / 4;
        double inchArray[] = {inches * Math.cos(robotAngle),
                              inches * Math.sin(robotAngle),
                              inches * Math.sin(robotAngle),
                              inches * Math.cos(robotAngle)};
        autonomousMovementCommand(movement,inchArray,speed);

    }

     /**
     * shoot Launches rings out of the robot
     * @param power power that is sent to flyWheel.
     */
    public void shoot(double power)
    {
        robot.flyWheel.setPower(-power);
        sleep(5000);
        for(int i = 0;i < 3;i++)
        {
            robot.trigger.setPosition(0.3);
            sleep(2000);
            robot.trigger.setPosition(0.1);
            sleep(2000);
        }
        robot.flyWheel.setPower(0);

    }
    
    /**
     * Helper method that converts an array into a 0-1 format
     */
    private double[] divideByLargestInArray(double[] array, double multiplier){
        double largest = array[0];
        for(double number: array)
            if(number>largest)
                largest = number;

        for(int i = 0; i<array.length; i++)
            array[i] = (array[i]/largest) * multiplier;

        return array;

    }
    
    /**
     * Helper method that converts an array into a 0-1 format
     */
    public double[] normalizeList (double[] input, double max){
         double biggestdouble = -2;
         for(double e: input)
            if (Math.abs(e)> biggestdouble)
                biggestdouble = Math.abs(e);
        if (biggestdouble != 0){
            biggestdouble/= max; // Multipliying in the max
            for(int i= 0; i<input.length; i++){
                input[i]/= biggestdouble;
            }
        }
        return input;
     }
     
     /**
     * A class that holds values for the encoders of each motor and the wheel attached to it.
     */
    private class AutonomousWheelMotor{
        DcMotor motor = null;

        double COUNTS_PER_MOTOR_REV;
        double WHEEL_DIAMETER_INCHES;
        double COUNTS_PER_INCH;

        /**
         * Initializes the autonomousWheelMotor
         * @param incomingMotor a DcMotor that the Autonomous Motor describes.
         * @param encoderCount the amount of counts that the encoder performs per motor rotation (Google this number).
         * @param diameter the diameter of the wheel attached to the motor.
         */
        public AutonomousWheelMotor(DcMotor incomingMotor, double encoderCount, double diameter){
            motor = incomingMotor;
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            COUNTS_PER_MOTOR_REV = encoderCount;
            WHEEL_DIAMETER_INCHES = diameter;


            COUNTS_PER_INCH=(COUNTS_PER_MOTOR_REV)/
                    (WHEEL_DIAMETER_INCHES*3.1415);

        }
    }

}