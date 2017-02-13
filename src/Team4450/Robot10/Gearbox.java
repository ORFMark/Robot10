package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.ValveDA;
import Team4450.Lib.ValveSA;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gearbox {
	private Robot robot;
	public boolean highGear = false, lowGear = false, PTO = false, neutral = false, neutralSupport = false;
	public ValveDA shifter = new ValveDA(0);
	public ValveDA PTOvalve = new ValveDA(2);
	public ValveSA  neutralValve = new ValveSA(4);
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
		shifter.SetB();
		neutral = false;
		lowGear = true;
		BoxStatus();
	}
	public void lowGear()
	{
		Util.consoleLog();
		shifter.SetA();
		neutral = false;
		lowGear = false;
		BoxStatus();
	}
	public void neutral()
	{
		Util.consoleLog();
		neutral = false;
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
}


	
