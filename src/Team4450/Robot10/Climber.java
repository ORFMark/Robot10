package Team4450.Robot10;
import com.ctre.CANTalon;

import Team4450.Lib.Util;
import Team4450.Lib.util;

public class Climber {
	private final Robot robot;
	private final CANTalon climbMotor = new CANTalon(4); //FIXME get true ID
	private boolean climbReady=false
	private final Teleop teleop;
}
public Climber(Robot robot, Teleop teleop)
{
	this.robot = robot;
	this.teleop = teleop;
}
public void dispose()
{
	if (climbMotor != null) climbMotor.delete();
}

public void climbPrep()
{
	teleop.EnablePTO();
	climbReady = true;
}
public void cancelClimp()
{
	teleop.disablePTO();
	climbReady=false;
}
public void climb(double power)
{
	if (climbReady == false)
	{
		util.consoleLog();
		Util.consoleLog("climbReady is False!");
	}
	else
	{
		robot.LFCanTalon.set(power);
		robot.RFCanTalon.set(power);
		robot.LRCanTalon.set(power);
		robot.RRCanTalon.set(power);
	}
}
