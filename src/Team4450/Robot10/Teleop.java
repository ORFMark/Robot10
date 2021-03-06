package Team4450.Robot10;

import java.lang.Math;

import Team4450.Lib.*;
import Team4450.Lib.JoyStick.*;
import Team4450.Lib.LaunchPad.*;

import Team4450.Robot10.BallControl;
import Team4450.Robot10.Gear;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;

class Teleop
{
	private final Robot 		robot;
	private final BallControl	ballControl;
	private final Gear			gear;
	private final Gearbox		gearbox;
	private final Vision 		vision;
	public  JoyStick			rightStick, leftStick, utilityStick;
	public  LaunchPad			launchPad;
	public AnalogInput PessureSensor = new AnalogInput(0);
	private boolean				autoTarget = false, invertDrive = false, jackRabbit = false;
	double output = 0;
	double oldstick =0;
	double newstick = 0;
	double change = 0;


	// Wheel encoder is plugged into dio port 1 - orange=+5v blue=signal, dio port 2 black=gnd yellow=signal. 
	//private Encoder				encoder = new Encoder(1, 2, true, EncodingType.k4X);

	// Encoder ribbon cable to dio ports: ribbon wire 2 = orange, 5 = yellow, 7 = blue, 10 = black

	// Constructor.

	Teleop(Robot robot)
	{
		Util.consoleLog();

		this.robot = robot;
		gear= new Gear(robot, this);
		ballControl= new BallControl(robot);
		gearbox = new Gearbox(robot);
		vision = Vision.getInstance(robot);
		//encoder.reset();
	}

	// Free all objects that need it.

	void dispose()
	{
		Util.consoleLog();

		if (leftStick != null) leftStick.dispose();
		if (rightStick != null) rightStick.dispose();
		if (utilityStick != null) utilityStick.dispose();
		if (launchPad != null) launchPad.dispose();
		if (gear != null) gear.dispose();
		if (ballControl != null) ballControl.dispose();
		if (gearbox != null) gearbox.dispose();
		if (PessureSensor != null) PessureSensor.free();
		//if (encoder != null) encoder.free();
	}

	void OperatorControl()
	{
		double	rightY, leftY, utilX;

		// Motor safety turned off during initialization.
		robot.robotDrive.setSafetyEnabled(false);

		Util.consoleLog();

		LCD.printLine(1, "Mode: OperatorControl");
		LCD.printLine(2, "All=%s, Start=%d, FMS=%b", robot.alliance.name(), robot.location, robot.ds.isFMSAttached());

		// Initial setting of air valves.



		// Configure LaunchPad and Joystick event handlers.

		launchPad = new LaunchPad(robot.launchPad, LaunchPadControlIDs.BUTTON_BLUE, this);

		LaunchPadControl lpControl = launchPad.AddControl(LaunchPadControlIDs.ROCKER_LEFT_BACK);
		lpControl.controlType = LaunchPadControlTypes.SWITCH;
		launchPad.AddControl(LaunchPadControlIDs.ROCKER_LEFT_BACK);


		launchPad.AddControl(LaunchPadControlIDs.BUTTON_YELLOW);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_RED_RIGHT);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_RED);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_BLUE_RIGHT);
		launchPad.addLaunchPadEventListener(new LaunchPadListener());
		launchPad.Start();

		leftStick = new JoyStick(robot.leftStick, "LeftStick", JoyStickButtonIDs.TRIGGER, this);
		leftStick.addJoyStickEventListener(new LeftStickListener());
		leftStick.Start();

		rightStick = new JoyStick(robot.rightStick, "RightStick", JoyStickButtonIDs.TOP_LEFT, this);
		rightStick.AddButton(JoyStickButtonIDs.TRIGGER);
		rightStick.AddButton(JoyStickButtonIDs.TOP_BACK);
		rightStick.AddButton(JoyStickButtonIDs.TOP_LEFT);
		rightStick.addJoyStickEventListener(new RightStickListener());
		rightStick.Start();

		utilityStick = new JoyStick(robot.utilityStick, "UtilityStick", JoyStickButtonIDs.TRIGGER, this);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_LEFT);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_RIGHT);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_MIDDLE);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_BACK);
		utilityStick.addJoyStickEventListener(new UtilityStickListener());
		utilityStick.Start();

		// Tighten up dead zone for smoother turrent movement.
		utilityStick.deadZone = .05;

		// Set CAN Talon brake mode by rocker switch setting.
		// We do this here so that the Utility stick thread has time to read the initial state
		// of the rocker switch.
		if (robot.isComp) robot.SetCANTalonBrakeMode(lpControl.latchedState);

		// Set gyro to heading 0.

		//robot.navx.resetYaw();
		//robot.navx.dumpValuesToNetworkTables();
		robot.navx.resetYaw();

		// Motor safety turned on.
		robot.robotDrive.setSafetyEnabled(true);

		// Driving loop runs until teleop is over.

		while (robot.isEnabled() && robot.isOperatorControl())
		{
			// Get joystick deflection and feed to robot drive object
			// using calls to our JoyStick class.

			if (gearbox.PTO)
			{
				rightY = stickLogCorrection(rightStick.GetY());
				leftY = climbLogCorrection(utilityStick.GetY());
			} 
			/* else if (invertDrive)
			{
				rightY = stickLogCorrection(rightStick.GetY()*-1);	// fwd/back right
				leftY = stickLogCorrection(leftStick.GetY()*-1);
			}*/
			else
			{
				rightY = stickLogCorrection(rightStick.GetY());	// fwd/back right
				leftY = stickLogCorrection(leftStick.GetY());	// fwd/back left
			}

			utilX = utilityStick.GetX();
			LCD.printLine(3, "Distance=%.2f", robot.monitorDistanceThread.getRangeInches());
			LCD.printLine(4, "leftY=%.4f  rightY=%.4f utilX=%.4f", leftY, rightY, utilX);
			LCD.printLine(5, "shootenc=%d", ballControl.tlEncoder.get()); 
			LCD.printLine(6, "yaw=%.0f, total=%.0f, rate=%.3f", robot.navx.getYaw(), robot.navx.getTotalYaw(), robot.navx.getYawRate());
			LCD.printLine(7, "shootenc=%d rpm=%.0f pwr=%.2f", ballControl.shooterSpeedSource.get(), ballControl.shooterSpeedSource.getRate() * 60, ballControl.shooterMotor1.get());
			LCD.printLine(8, "PSI = ", PSI(PessureSensor.getVoltage()));

			// Set wheel motors.
			// Do not feed JS input to robotDrive if we are controlling the motors in automatic functions.

			if (!autoTarget) robot.robotDrive.tankDrive(leftY, rightY);

			// End of driving loop.

			Timer.delay(.020);	// wait 20ms for update from driver station.
		}

		// End of teleop mode.

		Util.consoleLog("end");
	}

	// Map joystick y value of 0.0-1.0 to the motor working power range of approx 0.5-1.0

	private double stickCorrection(double joystickValue)
	{
		if (joystickValue != 0)
		{
			if (joystickValue > 0)
				joystickValue = joystickValue / 1.5 + .4;
			else
				joystickValue = joystickValue / 1.5 - .4;
		}

		return joystickValue;
	}
	
	private double PSI(double InputVoltage)
	{
	//returns the PSI based on a Andymark Pressure Sensor
		double SupplyVoltage = 5;
		return 250 * (InputVoltage/SupplyVoltage) - 25;
	}
	// Custom base logrithim.
	// Returns logrithim base of the value.

	private double baseLog(double base, double value)
	{
		return Math.log(value) / Math.log(base);
	}

	// Map joystick y value of 0.0 to 1.0 to the motor working power range of approx 0.5 to 1.0 using
	// logrithmic curve.

	private double stickLogCorrection(double joystickValue)
	{
		double base = Math.pow(2, 1/3) + Math.pow(2, 1/3);

		if (joystickValue > 0)
			joystickValue = baseLog(base, joystickValue + 1);
		else if (joystickValue < 0)
			joystickValue = -baseLog(base, -joystickValue + 1);
		if (jackRabbit == true)
		{
			output = joystickValue;
			if (output == 0) {
				newstick = 0;
			}
			else if (output >= 0)
			{
				if (change >= 0.05)
				{
					newstick = oldstick + 0.05;
				}
				else if (change <= -0.05)
				{
					newstick = oldstick - 0.05;
				}
				else newstick = output;
			}
			else if (output <= 0)
			{
				if (change >= 0.05)
				{
					newstick = oldstick - 0.05;
				}
				else if (change <= -0.05)
				{
					newstick = oldstick + 0.05;
				}
				else
				{
					newstick = output;
				}
			}
			else
				newstick = output;
			oldstick = newstick;
			return newstick;
		}
		else
			return joystickValue;
		//return joystickValue;
	}

	private double climbLogCorrection(double joystickValue)

	{
		double base = 2.2239800905693155211653633767222; //Math.pow(13.5, 1/3);
		if (joystickValue > 0)
			joystickValue = baseLog(base, joystickValue + 1);
		else if (joystickValue < 0)
			joystickValue = -baseLog(base, -joystickValue + 1);
		return joystickValue;

	}

	// Transmission control functions.

	//--------------------------------------


	// Handle LaunchPad control events.

	public class LaunchPadListener implements LaunchPadEventListener 
	{
		public void ButtonDown(LaunchPadEvent launchPadEvent) 
		{
			LaunchPadControl	control = launchPadEvent.control;

			Util.consoleLog("%s, latchedState=%b", control.id.name(),  control.latchedState);

			switch(control.id)
			{
			case BUTTON_YELLOW:
				if (launchPadEvent.control.latchedState)
				{
					Util.consoleLog("Starting AutopickUp");
					gear.AutoPickup();
				}
				else
				{
					Util.consoleLog("Interrupting Autopickup");
					gear.StopAutoPickup();
				}
				break;

			case BUTTON_BLUE:
				if (launchPadEvent.control.latchedState)
				{
					gearbox.PTOon();
				}
				else
					gearbox.PTOoff();

				break;

			case BUTTON_RED_RIGHT:
				robot.navx.resetYaw();
			case BUTTON_RED:
			{
				if (launchPadEvent.control.latchedState)
				{
					gear.gearDown();
				}
				else
				{
					gear.gearUp();
				}
				break;
			}
			case BUTTON_BLUE_RIGHT:
			{
				if (launchPadEvent.control.latchedState)
				{
					gear.gearElevatorDown();
				}
				else
				{
					gear.gearElevatorUp();
				}
			}

			default:
				break;
			}
		}

		public void ButtonUp(LaunchPadEvent launchPadEvent) 
		{
			//Util.consoleLog("%s, latchedState=%b", launchPadEvent.control.name(),  launchPadEvent.control.latchedState);
		}

		public void SwitchChange(LaunchPadEvent launchPadEvent) 
		{
			LaunchPadControl	control = launchPadEvent.control;

			Util.consoleLog("%s", control.id.name());

			switch(control.id)
			{
			// Set CAN Talon brake mmode.
			case ROCKER_LEFT_BACK:
			{
				if (control.latchedState)
					robot.SetCANTalonBrakeMode(false);	// coast
				else
					robot.SetCANTalonBrakeMode(true);	// brake

				break;
			}
			case ROCKER_LEFT_FRONT:
			{
				robot.cameraThread.ChangeCamera();
				//invertDrive = !invertDrive;
				break;
			}
			default:
				break;
			}
		}
	}

	// Handle Right JoyStick Button events.

	private class RightStickListener implements JoyStickEventListener 
	{

		public void ButtonDown(JoyStickEvent joyStickEvent) 
		{
			int angle;
			JoyStickButton	button = joyStickEvent.button;

			Util.consoleLog("%s, latchedState=%b", button.id.name(),  button.latchedState);

			switch(button.id)
			{
			case TRIGGER:
			{
				if (robot.cameraThread != null) robot.cameraThread.ChangeCamera();
			}
			case TOP_LEFT:
				robot.cameraThread.ChangeCamera();
				break;
			case TOP_BACK:
			{
				vision.SeekPegOffset();
				angle = vision.getPegOffset();
				Util.consoleLog("angle=%d", angle);
				break;
			}
			default:
				break;
			}
		}

		public void ButtonUp(JoyStickEvent joyStickEvent) 
		{
			//Util.consoleLog("%s", joyStickEvent.button.name());
		}
	}

	// Handle Left JoyStick Button events.

	private class LeftStickListener implements JoyStickEventListener 
	{
		public void ButtonDown(JoyStickEvent joyStickEvent) 
		{
			JoyStickButton	button = joyStickEvent.button;

			Util.consoleLog("%s, latchedState=%b", button.id.name(),  button.latchedState);

			switch(button.id)
			{
			case TRIGGER:
				if (button.latchedState)
					gearbox.transmission("High");
				else
					gearbox.transmission("Low");

				break;
			case TOP_LEFT:
			{
				if (button.latchedState)
					ballControl.fire();
				else
					ballControl.ceaseFire();
				break;
			}
			case TOP_RIGHT:
			{
				if (button.latchedState)
					ballControl.intakeIn();
				else
					ballControl.intakeStop();
				break;
			}
			case TOP_MIDDLE:
			{
				if (button.latchedState)
					gear.gearIntakeIn();
				else
					gear.gearIntakeStop();
				break;
			}
			case TOP_BACK:
			{
				if (button.latchedState)
					gear.gearIntakeOut();
				else
					gear.gearIntakeStop();
				break;
			}
			default:
				break;
			}
		}

		public void ButtonUp(JoyStickEvent joyStickEvent) 
		{
			//Util.consoleLog("%s", joyStickEvent.button.name());
		}
	}

	// Handle Utility JoyStick Button events.

	private class UtilityStickListener implements JoyStickEventListener 
	{
		public void ButtonDown(JoyStickEvent joyStickEvent) 
		{
			int angle;
			JoyStickButton	button = joyStickEvent.button;

			Util.consoleLog("%s, latchedState=%b", button.id.name(),  button.latchedState);

			switch(button.id)
			{
			// Trigger starts shoot sequence.
			case TRIGGER:
			{
				if (button.latchedState)
				{
					ballControl.load();
				}
				else
				{
					ballControl.Swab();
				}
				break;
			}
			case TOP_LEFT:
			{
				if (button.latchedState)
				{
					ballControl.fire();
				}
				else
				{
					ballControl.ceaseFire();
				}
				break;
			}
			case TOP_RIGHT:
			{
				if (button.latchedState)
				{
					ballControl.intakeIn();
					
				}
				else
				{
					ballControl.intakeStop();
				}
				break;
			}
			case TOP_BACK:
			{
				if (button.latchedState)
					gear.gearIntakeOut();
				else
					gear.gearIntakeStop();
				break;
			}
			case TOP_MIDDLE:
			{
				if (button.latchedState)
					gear.gearIntakeIn();
				else
					gear.gearIntakeStop();
				break;
			}

			default:
				break;
			}
		}

		public void ButtonUp(JoyStickEvent joyStickEvent) 
		{
			//Util.consoleLog("%s", joyStickEvent.button.id.name());
		}
	}
}