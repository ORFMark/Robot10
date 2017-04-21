package Team4450.Robot10;

import Team4450.Lib.ValveDA;
import Team4450.Robot10.Robot;
import Team4450.Lib.LaunchPad.LaunchPadControlIDs;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Team4450.Lib.JoyStick.JoyStickButtonIDs;
import Team4450.Lib.LCD;

import com.ctre.*;
import Team4450.Lib.Util;
public class Gear {
	private final Robot robot;
	private Teleop teleop;
	private final CANTalon gearIntake = new CANTalon(7);
	private final ValveDA	gearAcutuation = new ValveDA(1,0);
	private final ValveDA   gearElevator = new ValveDA(6);
	public double gearIntakePower = 0.9; //FIXME Get actual ID
	private Thread gearThread;
	public boolean gearOut = false;
	Gear (Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		this.robot=robot;
		this.teleop=teleop;
		robot.InitializeCANTalon(gearIntake);
		gearIntakeStop();
		gearUp();
		gearElevatorUp();

	}
	public void dispose()
	{
		if (gearIntake != null) gearIntake.delete();
		if (gearAcutuation != null) gearAcutuation.dispose();
		if (gearElevator != null) gearElevator.dispose();
	}
	public void gearIntakeSet(double power)
	{
		Util.consoleLog();
		gearIntake.set(power);
		if (power != 0)
		{
			Util.consoleLog("Gear Intake Motor Active");
			SmartDashboard.putBoolean("GearPickupMotor", true);
		}
		else
		{
			Util.consoleLog("Gear Intake Motor Stopped");
			SmartDashboard.putBoolean("GearPickupMotor", false);
		}
	}
	public void gearIntakeIn()
	{
		Util.consoleLog();
		gearIntakeSet(gearIntakePower);
	}
	public void gearIntakeOut()
	{
		Util.consoleLog();
		gearIntakeSet(-gearIntakePower);
	}
	public void gearHold()
	{
		Util.consoleLog();
		gearIntakeSet(.50);
	}
	public void gearIntakeStop()
	{
		Util.consoleLog();
		gearIntakeSet(0);
		if (teleop != null)
		{
			if (teleop.launchPad !=  null ) teleop.launchPad.FindButton(LaunchPadControlIDs.BUTTON_YELLOW).latchedState = false; 
			if (teleop.launchPad !=  null ) teleop.utilityStick.FindButton(JoyStickButtonIDs.TOP_BACK).latchedState = false; 
			if (teleop.launchPad !=  null ) teleop.utilityStick.FindButton(JoyStickButtonIDs.TOP_MIDDLE).latchedState = false; 
		}

	}
	public void gearDown()
	{
		Util.consoleLog();
		gearAcutuation.SetB(); //FIXME Get actual ID
		SmartDashboard.putBoolean("GearPickupDown", true);
		gearOut = true;

	}
	public void gearUp()
	{
		Util.consoleLog();
		gearAcutuation.SetA(); //FIXME get actual ID
		SmartDashboard.putBoolean("GearPickupDown", false);
		gearOut = false;
	}
	public void gearElevatorUp()
	{
		Util.consoleLog();
		gearElevator.SetA();
	}
	public void gearElevatorDown()
	{
		Util.consoleLog();
		gearElevator.SetB();
	}
	public void AutoPickup()
	{
		Util.consoleLog("Is gearThread null? " + (gearThread == null));
		if(gearThread != null) return;
		gearThread = new GearPickup();
		gearThread.start();
	}
	public void StopAutoPickup()
	{
		Util.consoleLog();
		if(gearThread != null) gearThread.interrupt();
		gearThread = null;
	}

	private class GearPickup extends Thread
	{
		GearPickup()
		{
			Util.consoleLog();
			this.setName("AutoGearPickup");
		}
		public void run()
		{
			Util.consoleLog();
			try
			{
				gearDown();
				gearElevatorDown();
				sleep(250);
				gearIntakeIn();
				sleep(250);
				while (!isInterrupted() && gearIntake.getOutputCurrent() < 10.1)
				{
					LCD.printLine(8, "gearmotor current=%f", gearIntake.getOutputCurrent()); 
					Util.consoleLog("Current: " + gearIntake.getOutputCurrent());
					sleep(50);
				}
				if(!isInterrupted()) Util.consoleLog("Gear Detected Current: " + gearIntake.getOutputCurrent());
				gearHold();
				sleep(500);
				gearUp();
				gearElevatorUp();
				sleep(1000);
				gearIntakeStop();

			}
			catch (InterruptedException e) {
				Util.consoleLog();
				gearIntakeStop();
				gearUp();
				gearElevatorUp();
				gearThread = null;
			} 
			catch (Throwable e) {e.printStackTrace(Util.logPrintStream);
			gearIntakeStop();
			gearUp();
			gearElevatorUp();
			gearThread = null;
			if (teleop != null) teleop.launchPad.FindButton(LaunchPadControlIDs.BUTTON_YELLOW).latchedState = false;
			}
		}

	}
}
