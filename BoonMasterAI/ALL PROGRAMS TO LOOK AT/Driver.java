package edu.njit.cs370.benh.driver;

import edu.njit.cs370.benh.boonmaster.BoonMaster;
import edu.njit.cs370.benh.clip.Clip;
import edu.njit.cs370.benh.screenshot.Screenshot;

public class Driver {

	public static void main(String[] args) throws Exception {
		new Driver().drive();
	}

	private void drive() throws Exception {
		Screenshot.main(new String[0]);
		Clip.main(new String[0]);
		BoonMaster.main(new String[0]);
	}	
}