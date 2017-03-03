package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.JoyStick.JoyStickButtonIDs;
import Team4450.Lib.LaunchPad.LaunchPadControlIDs;
import Team4450.Robot10.Robot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.*;
import edu.wpi.first.wpilibj.Spark;

public class BallControl {
	private final Robot robot;
	private final Spark	intakeMotor = new Spark(0); //TODO get Port numbers;
	final Talon shooterMotor1 = new Talon(1);
	private final Talon ShooterIndexer = new Talon (3);
	private final Talon shooterFeederMotor = new Talon(2);
	final Encoder encoder = new Encoder(3, 4, true, EncodingType.k4X);
	public double Intake_Power, Shooter_Power, Indexer_Power, Feeder_Power;
	BallControl (Robot robot, Teleop teleop)

	{
		Util.consoleLog();
		this.robot = robot;
		Intake_Power= 0.5; //TODO Get true power readouts
		Shooter_Power = 1;
		Indexer_Power = -0.3;
		Feeder_Power =0.5;
		ceaseFire();
		intakeStop();
		choke();
		encoder.reset();
	}
	public void dispose()
	{
		Util.consoleLog();
		if (shooterMotor1 != null) shooterMotor1.free();
		if (ShooterIndexer != null) ShooterIndexer.free();
		if (shooterFeederMotor !=null) shooterFeederMotor.free();
		if (encoder != null) encoder.free();
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
	public void load()
	{
		Util.consoleLog("Loading the cannon");
		ShooterIndexer.set(Indexer_Power);
		shooterFeederMotor.set(Feeder_Power);
		SmartDashboard.putBoolean("DispenserMotor", true);
	}
	public void Swab()
	{
		Util.consoleLog("Ceasing Loading");
		ShooterIndexer.set(0);
		shooterFeederMotor.set(0);
		SmartDashboard.putBoolean("DispenserMotor", false);
	}
	public void fire()
	{
		Util.consoleLog();
		load();
		shooterSet(Shooter_Power);
	}
	public void ceaseFire()
	{
		Util.consoleLog();
		shooterSet(0);
		Swab();
	}
	public void feed()
	{
		Util.consoleLog();
		shooterFeederMotor.set(0.20);
	}
	public void choke()
	{
		Util.consoleLog();
		shooterFeederMotor.set(0);
	}
	public void vomit()
	{
		Util.consoleLog();
		shooterFeederMotor.set(-.20);
		SmartDashboard.putBoolean("DispenserMotor", true);
	}
}
