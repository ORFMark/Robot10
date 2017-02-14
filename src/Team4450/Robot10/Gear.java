package Team4450.Robot10;

import Team4450.Lib.ValveDA;
import Team4450.Robot10.Robot;
import edu.wpi.first.wpilibj.Talon;

import com.ctre.*;
import Team4450.Lib.Util;
public class Gear {
	private final Robot robot;
	private final Talon gearIntake = new Talon(3);
	private final ValveDA	gearAcutuation = new ValveDA(6);
	public double gearIntakePower = 0.75; //FIXME Get actual ID
	Gear (Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		this.robot=robot;
		gearIntakeStop();
		gearDown();
	}
public void dispose()
{
	if (gearIntake != null) gearIntake.free();
	if (gearAcutuation != null) gearAcutuation.dispose();
}
public void gearIntakeSet(double power)
{
	Util.consoleLog();
	gearIntake.set(power);
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
public void gearIntakeStop()
{
	Util.consoleLog();
	gearIntakeSet(0);
}
public void gearDown()
{
	Util.consoleLog();
	gearAcutuation.SetA(); //FIXME Get actual ID
}
public void gearUp()
{
	Util.consoleLog();
	gearAcutuation.SetB(); //FIXME get actual ID
}
}
