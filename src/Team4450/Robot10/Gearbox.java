package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.ValveDA;
import Team4450.Lib.ValveSA;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gearbox {
	private Robot robot;
	public boolean highGear, lowGear, PTO, neutral;
	public ValveDA shifter = new ValveDA(0);
	public ValveDA PTOvalve = new ValveDA(2);
	public Gearbox(Robot robot)
	{
		Util.consoleLog();
		this.robot = robot;
		PTOoff();
		lowGear();
		dashboardSet();
	}
	public void BoxStatus()
	{
		Util.consoleLog("HighGear: " + highGear, "lowGear: " + lowGear, "Neutral: " + neutral, "PTO: " + PTO);
	}
	public void dispose()
	{
		Util.consoleLog();
		if (shifter != null) shifter.dispose();
		if (PTOvalve != null) PTOvalve.dispose();
		dashboardSet();
		BoxStatus();
	}
	public void highGear()
	{
		Util.consoleLog();
		shifter.SetA();  //FIXME get actual side
		highGear = true;
		lowGear = false;
		neutral = false;
		dashboardSet();
		BoxStatus();
	}
	public void lowGear()
	{
		Util.consoleLog();
		shifter.SetB(); //FIXME get actual side
		highGear = false;
		lowGear = true;
		neutral = false;
		dashboardSet();
		BoxStatus();
	}
	public void neutral()
	{
		Util.consoleLog();
		highGear = false;
		lowGear = false;
		neutral = true;
		dashboardSet();
		BoxStatus();
	}
	public void PTOon()
	{
		Util.consoleLog();
		PTOvalve.SetA(); //FIXME get actual side
		PTO = true;
		dashboardSet();
		BoxStatus();
	}
	public void PTOoff()
	{
		Util.consoleLog();
		PTOvalve.SetB();
		PTO = false;
		dashboardSet();
		BoxStatus();
	}
	public void dashboardSet()
	{
		Util.consoleLog();
		if (lowGear)
		{
			SmartDashboard.putBoolean("LowSpeed", true); 
		}
		if (highGear)
		{
			SmartDashboard.putBoolean("LowSpeed", false);
		}
		if (PTO)
		{
			SmartDashboard.putBoolean("PTO", true);
		}
		if (!PTO)
		{
			SmartDashboard.putBoolean("PTO", false);
		}
		if (neutral)
		{
			SmartDashboard.putBoolean("Neutral", true);
		}
		if (!neutral)
		{
			SmartDashboard.putBoolean("Neutral", false); 
		}
			
	}
}
