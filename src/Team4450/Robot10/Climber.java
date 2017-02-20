package Team4450.Robot10;
import com.ctre.CANTalon;

import Team4450.Lib.Util;


public class Climber {
	private final Robot robot;
	private final CANTalon climbMotor = new CANTalon(4); //FIXME get true ID
	private boolean climbReady=false;
	private final Gearbox gearbox;

	public Climber(Robot robot, Gearbox gearbox)
	{
		this.robot = robot;
		this.gearbox = gearbox;
	}
	public void dispose()
	{
		if (climbMotor != null) climbMotor.delete();
	}

	public void climbPrep()
	{
		gearbox.PTOon();
		climbReady = true;
	}
	public void cancelClimb()
	{
		gearbox.PTOoff();
		climbReady=false;
	}
	public void climb(double power)
	{
		if (climbReady == false)
		{
			Util.consoleLog();
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
}
