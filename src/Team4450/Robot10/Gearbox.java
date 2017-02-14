package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.ValveDA;
import Team4450.Lib.ValveSA;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gearbox {
	private Robot robot;
	public boolean highGear = false, lowGear = false, PTO = false, neutral = false, neutralSupport = false;
	public ValveDA shifter = new ValveDA(0);
	public ValveDA PTOvalve = new ValveDA(2);
	public ValveDA  neutralValve = new ValveDA(4);  //TODO check for DA
	public Gearbox(Robot robot)
	{
		Util.consoleLog();
		this.robot = robot;
		PTOoff();
		lowGear();
		BoxStatus();
	}
	public void BoxStatus()
	{
		Util.consoleLog("HighGear: " + highGear, "lowGear: " + lowGear, "Neutral Support" + neutralSupport, "Neutral: " + neutral, "PTO: " + PTO);
		SmartDashboard.putBoolean("LowSpeed", lowGear);
		SmartDashboard.putBoolean("PTO", PTO);
		SmartDashboard.putBoolean("Neutral", neutral);
	}

	public void dispose()
	{
		Util.consoleLog();
		if (shifter != null) shifter.dispose();
		if (PTOvalve != null) PTOvalve.dispose();
		if (neutralValve != null) neutralValve.dispose();
	}
	public void highGear()
	{
		Util.consoleLog();
		if (lowGear)
			shifter.SetA();
		else if (neutral)
		{
			neutralValve.SetA();
			Timer.delay(0.5);
			shifter.SetA();
		}	
		else if (!lowGear)
			Util.consoleLog("Not Shifting, already set to Lowgear");
			
		neutral = false;
		lowGear = false;
		BoxStatus();
	}
	public void lowGear()
	{
		Util.consoleLog();
		if (neutral)
		shifter.SetB();
		else if (!lowGear)
		{
			neutralValve.SetA();
			Timer.delay(0.5);
			shifter.SetB();
		}
		else if (lowGear)
			Util.consoleLog("Not Shifting, already set to Highgear");
		neutral = false;
		lowGear = true;
		BoxStatus();
	}
	public void neutral()
	{
		Util.consoleLog();
		if (!lowGear)
		{
			neutralValve.SetB();
		}
		else if (lowGear)
		{
			shifter.SetA();
			Timer.delay(0.5);
			neutralValve.SetB();
		}
		else if (neutral)
		{
			Util.consoleLog("Not Shifting, already in Neutral");
		}
		lowGear = true;
		neutral = true;
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
			PTOoff();
			highGear();
			break;
		case "Low":
			Util.consoleLog();
			PTOoff();
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


	
