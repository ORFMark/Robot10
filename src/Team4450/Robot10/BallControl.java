package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.JoyStick.JoyStickButtonIDs;
import Team4450.Lib.LaunchPad.LaunchPadControlIDs;
import Team4450.Robot10.Robot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.*;

public class BallControl {
	private final Robot robot;
	private final Talon	intakeMotor = new Talon(0); //TODO get Port numbers;
	private final Talon shooterMotor1 = new Talon(2);
	private final Talon shooterMotor2 = new Talon (3);
	private final Talon shooterFeederMotor = new Talon(4);
	public double Intake_Power, Shooter_Power;
	BallControl (Robot robot, Teleop teleop)
	
	{
		Util.consoleLog();
		
		this.robot = robot;
		Intake_Power= 0.75; //TODO Get true power readouts
		Shooter_Power = 0.75;
		ceaseFire();
		intakeStop();
		choke();
	}
	public void dispose()
	{
		Util.consoleLog();
		if (shooterMotor1 != null) shooterMotor1.free();
		if (shooterMotor2 != null) shooterMotor2.free();
		if (shooterFeederMotor !=null) shooterFeederMotor.free();
		if (intakeMotor != null) intakeMotor.free();
		
	}
	public void intakeSet(double power)
	{
		Util.consoleLog("%f", power);
		intakeMotor.set(power);
		if (power != 0)
		{
			Util.consoleLog("Ball Intake Motor Active");
			SmartDashboard.putBoolean("BallPickupMotor", true);
		}
		else
		{
			Util.consoleLog("Ball Intake Motor Stopped");
			SmartDashboard.putBoolean("BallPickupMotor", false);
		}
	}
	public void shooterSet(double power)
	{
		Util.consoleLog("%f", power);
		shooterMotor1.set(power);
		shooterMotor2.set(power);
		if (power != 0)
		{
			Util.consoleLog("Shooter Motors Active");
			SmartDashboard.putBoolean("ShooterMotor", true);
		}
		else
		{
			Util.consoleLog("Shooter Motors Stopped");
			SmartDashboard.putBoolean("ShooterMotor", false);
		}
	}
	public void intakeIn()
	{
		Util.consoleLog();
		intakeSet(Intake_Power);
	}
	public void intakeOut()
	{
		Util.consoleLog();
		intakeSet(-Intake_Power);
	}
	public void intakeStop()
	{
		Util.consoleLog();
		intakeSet(0);
	}
	public void fire()
	{
		Util.consoleLog();
		shooterSet(Shooter_Power);
	}
	public void ceaseFire()
	{
		Util.consoleLog();
		shooterSet(0);
	}
	public void feed()
	{
		Util.consoleLog();
		shooterFeederMotor.set(1);
	}
	public void choke()
	{
		Util.consoleLog();
		shooterFeederMotor.set(0);
	}
}
