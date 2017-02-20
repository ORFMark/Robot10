package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.ValveDA;
import Team4450.Lib.ValveSA;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gearbox {
	private Robot robot;
	public boolean highGear = false, lowGear = false, PTO = false, neutral = false, neutralSupport = false, SAneutral = false;
	public ValveDA shifter = new ValveDA(0);
	public ValveDA PTOvalve = new ValveDA(2);
	final ValveDA  neutralDAValve = new ValveDA(4);

	public Gearbox(Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		this.robot = robot;
		PTOoff();
		lowGear();
		BoxStatus();
	}
	public void BoxStatus()
	{
		Util.consoleLog("HighGear: " + highGear + ", lowGear: " + lowGear + ", NeutralSupport: " + neutralSupport + ", Neutral: " + neutral + ", PTO: " + PTO);
		SmartDashboard.putBoolean("Low", lowGear);
		SmartDashboard.putBoolean("High", highGear);
		SmartDashboard.putBoolean("PTO", PTO);
		SmartDashboard.putBoolean("Neutral", neutral);
	}

	public void dispose()
	{
		Util.consoleLog();
		if (shifter != null) shifter.dispose();
		if (PTOvalve != null) PTOvalve.dispose();
		if (neutralDAValve != null) neutralDAValve.dispose();
	}
	public void highGear()
	{
		Util.consoleLog();
		if (lowGear)
		{
			neutralDAValve.SetB();
			shifter.SetB();
		}
		else if (neutral)
		{

			shifter.SetA();
			Timer.delay(0.05);
			neutralDAValve.SetA();
			Timer.delay(0.05);
			neutralDAValve.SetB();
			shifter.SetB();
		}	
		else if (!lowGear)
			Util.consoleLog("Not Shifting, already set to Highgear");

		neutral = false;
		lowGear = false;
		highGear = true;
		BoxStatus();
	}
	public void lowGear()
	{
		Util.consoleLog();
		if (highGear)
		{
			neutralDAValve.SetB();
			Timer.delay(0.05);
			shifter.SetA();
			Timer.delay(0.05);
			neutralDAValve.SetA();

		}
		else if (neutral)
		{
			neutralDAValve.SetA();
		}
		else if (lowGear)
			Util.consoleLog("Not Shifting, already set to Lowgear");
		neutral = false;
		lowGear = true;
		highGear = false;
		BoxStatus();
	}
	public void neutral()
	{
		Util.consoleLog();
		if (highGear)
		{
			neutralDAValve.SetB();
			Timer.delay(0.05);
			shifter.SetA();
		}
		else if (lowGear)
		{

			shifter.SetB();
			Timer.delay(0.05);
			neutralDAValve.SetB();
			Timer.delay(0.5);
			shifter.SetA();


		}
		else if (neutral)
		{
			Util.consoleLog("Not Shifting, already in Neutral");
		}
		lowGear = false;
		neutral = true;
		highGear = false;
		BoxStatus();
	}
	public void PTOon()
	{
		Util.consoleLog();
		neutral();
		PTOvalve.SetA();
		PTO = true;
		BoxStatus();
	}
	public void PTOoff()
	{
		Util.consoleLog();
		PTO = false;
		PTOvalve.SetB();
		lowGear();
		BoxStatus();
	}

	public void transmission(String State)
	{
		switch(State)
		{
		case "High":
			Util.consoleLog();
			highGear();
			break;
		case "Low":
			Util.consoleLog();
			lowGear();
			break;
		case "Neutral":
			Util.consoleLog();
			neutral();
			break;
		default:
			Util.consoleLog("Invalid Gear");
			break;
		}

	}
}



