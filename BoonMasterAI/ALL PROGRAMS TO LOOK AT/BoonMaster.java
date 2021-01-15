package edu.njit.cs370.benh.boonmaster;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Set;

import javax.imageio.ImageIO;

public class BoonMaster {
	private static final String PATH = "./clips";
	private static final int BOX_SIZE = 4;
    public static String[] itemArray = new String[194];
    public static Map<String, String[][]> allItems = new HashMap<String, String[][]>();
    public static Map<String, String> godAbb = new HashMap<String, String>();
    public static List<String> itemInput;
    public static List<String> inventory = new ArrayList<String>();
    public static Set<String> bigBoonSet = new HashSet<String>();
    
    
    public static void main(String[] args) throws Exception {
		System.setOut(new PrintStream(new FileOutputStream("./sig.txt")));
		new BoonMaster().doBoon();
	}

	public void doBoon() {
		initInventory();
		createItemArray();
		initBoonData();
		for (String item: itemInput) {
			Signature signature = new Signature(item);
			BufferedImage bi = loadImage(item, 1);
			makeSignature(bi, signature);
		}
		
		// Find Duo Boons Here For Now
		System.out.println("Inventory List:");
		for (String item: inventory) {
			System.out.print("Item: ");
			System.out.println(item);
			findBigBoons(item);			
		}
		int cnt = 0;
		godAbb.put("Aph", "Aphrodite");
		godAbb.put("Are", "Ares");
		godAbb.put("Art", "Artemis");
		godAbb.put("Ath", "Athena");
		godAbb.put("Dem", "Demeter");
		godAbb.put("Dio", "Dionysus");
		godAbb.put("Pos", "Poseidon");
		godAbb.put("Zeu", "Zeus");
		// Printing the boons you want to find
		List<String> bigBoonList = new ArrayList<>(bigBoonSet);
		Collections.sort(bigBoonList);
		if (bigBoonList.size() == 0) {
			System.out.println("\n\nThere are no big boons for you to get.");
		}
		for (String bigBoon: bigBoonList) {
			String[] boonArr = bigBoon.split("_");
		
			if (boonArr[0].equals("Duo")) {
				if (cnt == 0) {
					System.out.println("\n\nPOSSIBLE DUO BOONS YOU CAN GET\n===================================\n");
					cnt++;
				}	
				System.out.println("To get " + boonArr[3] + "...Find the Gods " + godAbb.get(boonArr[1]) + " or " + godAbb.get(boonArr[2]));
				continue;
			}
			if (cnt < 2) {
				System.out.println("\nLEGENDARY BOONS YOU CAN GET\n===================================\n");
				cnt++;
			}				
			System.out.println("To get " + boonArr[2] + "...Find the God " + boonArr[1]);
		}
	}
	
	private void initInventory() { 
		File f = new File(PATH);
		String[] files = f.list((dir, name) -> name.endsWith(".png"));
		itemInput = Arrays.asList(files);
	}
	
	private void createItemArray() {
		try {
		      File myObj = new File("./signatures\\signatures.txt");
		      Scanner myReader = new Scanner(myObj);
		      int count = 0;
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        //for (int i = 0)
		        itemArray[count] = data;
		        count++;
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		  
	}
	
	
	public void findBigBoons(String item) {
		String[][] boons = allItems.get(item);		
		for (String[] boonData: boons) {
			String bigBoonName = boonData[0];
			if (inventory.contains(bigBoonName)) {
				continue;
			}
			// add flag for Vengeful Mood
			for (int i = 1; i < boonData.length; i++) {
				String currRequiredBoon = boonData[i];
				if (inventory.contains(currRequiredBoon)) {
					bigBoonSet.add(bigBoonName);
					break;
				}
			}
		} 
	}
		

	private BufferedImage loadImage(String s, int flag) {
		String currPath = "./images/boons/";
		if (flag == 1) {
			currPath = "./clips/";
		}
		BufferedImage bi = null;
		try (FileInputStream fis = new FileInputStream(new File(currPath + s))) {
			bi = ImageIO.read(fis);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
		BufferedImage bix = new BufferedImage(
			(int) (Math.ceil((bi.getWidth() < 1000 ? bi.getWidth() : 800) / BOX_SIZE) * BOX_SIZE),
			(int) (Math.ceil((bi.getHeight() < 1000 ? bi.getHeight() : 600) / BOX_SIZE) * BOX_SIZE),
			BufferedImage.TYPE_INT_BGR
		);
		Graphics2D g2d = bix.createGraphics();
		g2d.drawRenderedImage(bi, new AffineTransform());
		g2d.dispose();
		return bix;
	}

	private BufferedImage makeSignature(BufferedImage bi, Signature signature) {
		BufferedImage biw = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
		Graphics2D g2d = biw.createGraphics();
		g2d.drawRenderedImage(bi, new AffineTransform());
		g2d.dispose();
		for (int r = 0; r < biw.getHeight(); r += BOX_SIZE) {
			for (int c = 0; c < biw.getWidth(); c += BOX_SIZE) {
				
				int[] pixels = new int[BOX_SIZE * BOX_SIZE];
				biw.getRGB(c, r, BOX_SIZE, BOX_SIZE, pixels, 0, BOX_SIZE);
				int totalR = 0;
				int totalG = 0;
				int totalB = 0;
				for (int i = 0; i < pixels.length; i++) {
					int red = (pixels[i] >> 0) & 0x000000ff;
					int green = (pixels[i] >> 8) & 0x000000ff;
					int blue = (pixels[i] >> 16) & 0x000000ff;
					totalR += red;
					totalG += green;
					totalB += blue;
				}
				int valueR = totalR / pixels.length;
				int valueG = totalG / pixels.length;
				int valueB = totalB / pixels.length;
				RGB rgb = new RGB(valueR, valueG, valueB);
				signature.getContent().add(rgb);
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = 0xff000000 | (valueB << 16) | (valueG << 8) | (valueR << 0);
				}
				biw.setRGB(c, r, BOX_SIZE, BOX_SIZE, pixels, 0, BOX_SIZE);
				
			}
		}		
		int itemIndex = leastSquares(signature.toString());
		System.out.print("[");
		System.out.print(itemIndex);
		String fullName = itemArray[itemIndex];
		String nameArray[] = fullName.split(".png");
		inventory.add(nameArray[0]);
		System.out.println("] ||  MOST LIKELY ITEM: " + nameArray[0] + "\n");
		return biw;
	}
	
	private int leastSquares(String signature) {
		System.out.println(signature);
		int bestItemIndex = 1;
		int currentLeast = 100000;
		String currSignCords[]= signature.split("\\(");
		for (int tempItem = 0; tempItem < itemArray.length; tempItem++) {
			String currItemCords[] = itemArray[tempItem].split("\\(");
			
			int leastSquaresCount = 0;
			for (int i = 1; i < currItemCords.length; i++) {
				int currItemX = Integer.parseInt((currItemCords[i].substring(0, 3)).trim());
				int currItemY = Integer.parseInt((currItemCords[i].substring(4, 7)).trim());
				int currItemZ = Integer.parseInt((currItemCords[i].substring(8, 11)).trim());
				
				int currSignX = Integer.parseInt((currSignCords[i].substring(0, 3)).trim());
				int currSignY = Integer.parseInt((currSignCords[i].substring(4, 7)).trim());
				int currSignZ = Integer.parseInt((currSignCords[i].substring(8, 11)).trim());
				
				int diffX = Math.abs((currItemX - currSignX));
				int diffY = Math.abs((currItemY - currSignY));
				int diffZ = Math.abs((currItemZ - currSignZ));
				
				leastSquaresCount += diffX + diffY + diffZ;				
			}
			if (leastSquaresCount < currentLeast) {
				currentLeast = leastSquaresCount;
				bestItemIndex = tempItem;
			}
			
		}
		if (currentLeast > 20000) {
			System.out.println("Large Regression Number: " + Integer.toString(currentLeast));
			return 0;
		}
		
		
		return bestItemIndex;
	}
	private class Signature {
		
		private String id;
		private List<RGB> content = new LinkedList<>();

		public Signature(String id) {
			this.id = id;
		}

		@SuppressWarnings("unused")
		public String getId() {
			return id;
		}

		public List<RGB> getContent() {
			return content;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(id + " ");
			for (RGB rgb : content) {
				sb.append(rgb + " ");
			}
			sb.append(" ");
			return sb.toString();
		}
			
	}
	
	private class RGB {
		int red;
		int green;
		int blue;
		public RGB(int r, int g, int b) {
			this.red = r;
			this.green = g;
			this.blue = b;
		}

		@Override
		public String toString() {
			return String.format("(%3d,%3d,%3d)", this.red, this.green, this.blue);
		}
	}
	
private void initBoonData() { 
		
		//VENGEFUL MOOD DUO BOON, NEED A REVENGE EFFECT FLAG
		String[][] HeartbreakStrikeArr = {	{"Duo_Aph_Are_Curse of Longing", "Ares_Curse of Pain", "Ares_Curse of Agony"}, 
											{"Duo_Aph_Art_Heart Rend", "Artemis_Deadly Flourish", "Artemis_True Shot"},
											{"Duo_Aph_Ath_Parting Shot", "Athena_Divine Strike", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
											{"Duo_Aph_Dem_Cold Embrace", "Demeter_Crystal Beam"},
											{"Duo_Aph_Dio_Low Tolerance", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash", "Dionysus_Aid"},
											{"Duo_Aph_Pos_Sweet Nectar", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
											{"Duo_Aph_Zeu_Smoldering Air", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
											{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Empty Inside", "Aphrodite_Sweet Surrender", "Aphrodite_Broken Resolve"}
										 };
		String[][] HeartbreakFlourishArr = { 	{"Duo_Aph_Are_Curse of Longing", "Ares_Curse of Agony", "Ares_Curse of Pain"},
												{"Duo_Aph_Art_Heart Rend", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot"},
												{"Duo_Aph_Ath_Parting Shot", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
												{"Duo_Aph_Dem_Cold Embrace", "Demeter_Crystal Beam"},
												{"Duo_Aph_Dio_Low Tolerance", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash", "Dionysus_Aid"},
												{"Duo_Aph_Pos_Sweet Nectar", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
												{"Duo_Aph_Zeu_Smoldering Air", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
												{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Empty Inside", "Aphrodite_Sweet Surrender", "Aphrodite_Broken Resolve"}
										   };
		String[][] CrushShotArr = 	{	{"Duo_Aph_Are_Curse of Longing", "Ares_Curse of Pain", "Ares_Curse of Agony"},
										{"Duo_Aph_Art_Heart Rend", "Artemis_Deadly Flourish", "Artemis_True Shot"},
										{"Duo_Aph_Ath_Parting Shot", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Aph_Dem_Cold Embrace", "Demeter_Crystal Beam"},
										{"Duo_Aph_Dio_Low Tolerance", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash", "Dionysus_Aid"},
										{"Duo_Aph_Pos_Sweet Nectar", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Duo_Aph_Zeu_Smoldering Air", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Empty Inside", "Aphrodite_Sweet Surrender", "Aphrodite_Broken Resolve"}
									};
		String[][] PassionDashArr = {	{"Duo_Aph_Are_Curse of Longing", "Ares_Curse of Pain", "Ares_Curse of Agony"},
										{"Duo_Aph_Art_Heart Rend", "Artemis_Deadly Flourish", "Artemis_True Shot"},
										{"Duo_Aph_Ath_Parting Shot", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Aph_Dem_Cold Embrace", "Demeter_Crystal Beam"},
										{"Duo_Aph_Dio_Low Tolerance", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash", "Dionysus_Aid"},
										{"Duo_Aph_Pos_Sweet Nectar", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Duo_Aph_Zeu_Smoldering Air", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Empty Inside", "Aphrodite_Sweet Surrender", "Aphrodite_Broken Resolve"}
									};
		String[][] AphroditeAidArr = {	{"Duo_Aph_Ath_Parting Shot", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Aph_Dem_Cold Embrace", "Demeter_Crystal Beam"},
										{"Duo_Aph_Dio_Low Tolerance", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash", "Dionysus_Aid"},
										{"Duo_Aph_Pos_Sweet Nectar", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Duo_Aph_Zeu_Smoldering Air", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"}					
									 };
		String[][] EmptyInsideArr = {	{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Passion Dash", "Aphrodite_Crush Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Passion Flare"}				
									};
		String[][] SweetSurrenderArr = {	{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Passion Dash", "Aphrodite_Crush Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Passion Flare"}				
		};
		String[][] BrokenResolveArr = {	{"Leg_Aphrodite_Unhealthy Fixation", "Aphrodite_Passion Dash", "Aphrodite_Crush Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Passion Flare"}				
		};
		// ARES BOONS
		String[][] CurseOfPainArr = { 	{"Duo_Aph_Are_Curse of Longing", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
										{"Duo_Are_Ath_Merciful End", "Athena_Divine Strike", "Athena_Divine Flourish"},
										{"Duo_Are_Dio_Curse of Nausea", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash"},
										{"Duo_Are_Pos_Curse of Drowning", "Poseidon_Flood Shot"},
										{"Duo_Are_Zeu_Vengeful Mood", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid", "Zeus_Thunder Flare"},
									};
		String[][] CurseOfAgonyArr = {	{"Duo_Aph_Are_Curse of Longing", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
										{"Duo_Are_Ath_Merciful End", "Athena_Divine Strike", "Athena_Divine Flourish"},
										{"Duo_Are_Dio_Curse of Nausea", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash"},
										{"Duo_Are_Pos_Curse of Drowning", "Poseidon_Flood Shot"},
										{"Duo_Are_Zeu_Vengeful Mood", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid", "Zeus_Thunder Flare"},
									 };
		//CAN NOT BE COMBINED WITH EACH OTHER
		String[][] SlicingShotArr = {	{"Duo_Are_Art_Hunting Blades","Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_Hunter Dash", "Artemis_Aid"},
										{"Duo_Are_Dem_Freezing Vortex", "Demeter_Aid", "Demeter_Frost Flourish", "Demeter_Frost Strike", "Demeter_Mistral Dash"},
										{"Duo_Are_Zeu_Vengeful Mood", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid", "Zeus_Thunder Flare"},
										{"Leg_Ares_Vicious Cycle", "Ares_Black Metal", "Ares_Engulfing Vortex"}
									};
		String[][] CurseOfVengeanceArr = { 	{"Duo_Are_Dio_Curse of Nausea", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Drunken Dash"},
											{"Duo_Are_Zeu_Vengeful Mood", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid", "Zeus_Thunder Flare"}
										 };
		String[][] AresAidArr = {	{"Duo_Are_Pos_Curse of Drowning", "Poseidon_Flood Shot"},
									{"Duo_Are_Zeu_Vengeful Mood", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid", "Zeus_Thunder Flare"},
									{"Leg_Ares_Vicious Cycle", "Ares_Black Metal", "Ares_Engulfing Vortex"}
							 	};
		String[][] BladeDashArr = {	{"Duo_Are_Pos_Curse of Drowning", "Poseidon_Flood Shot"},
									{"Duo_Are_Zeu_Vengeful Mood", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid", "Zeus_Thunder Flare"},
									{"Leg_Ares_Vicious Cycle", "Ares_Black Metal", "Ares_Engulfing Vortex"}
			 					  };
		
		// ARTEMIS BOONS
		String[][] DeadlyStrikeArr = { 	{"Duo_Aph_Art_Heart Rend", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
										{"Duo_Are_Art_Hunting Blades", "Ares_Slicing Shot"},
										{"Duo_Art_Ath_Deadly Reversal", "Athena_Divine Strike", "Athena_Divine Flourish"},
										{"Duo_Art_Dem_Crystal Clarity", "Demeter_Crystal Beam"},
										{"Duo_Art_Dio_Splitting Headache", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"},
										{"Duo_Art_Pos_Mirage Shot", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Duo_Art_Zeu_Lightning Rod", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Thunder Dash", "Zeus_Electric Shot", "Zeus_Aid"}
									 };
		String[][] DeadlyFlourishArr = {	{"Duo_Aph_Art_Heart Rend", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
											{"Duo_Are_Art_Hunting Blades", "Ares_Slicing Shot"},
											{"Duo_Art_Ath_Deadly Reversal", "Athena_Divine Strike", "Athena_Divine Flourish"},
											{"Duo_Art_Dem_Crystal Clarity", "Demeter_Crystal Beam"},
											{"Duo_Art_Dio_Splitting Headache", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"},
											{"Duo_Art_Pos_Mirage Shot", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
											{"Duo_Art_Zeu_Lightning Rod", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Thunder Dash", "Zeus_Electric Shot", "Zeus_Aid"}
									   };
		String[][] TrueShotArr = {	{"Duo_Aph_Art_Heart Rend", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
									{"Duo_Art_Ath_Deadly Reversal", "Athena_Divine Strike", "Athena_Divine Flourish"},
									{"Duo_Art_Dio_Splitting Headache", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"},
									{"Duo_Art_Pos_Mirage Shot", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
									{"Duo_Art_Zeu_Lightning Rod", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Thunder Dash", "Zeus_Electric Shot", "Zeus_Aid"}
								  };
		String[][] HunterDashArr = {	{"Duo_Are_Art_Hunting Blades", "Ares_Slicing Shot"},
										{"Duo_Art_Dem_Crystal Clarity", "Demeter_Crystal Beam"},
										{"Duo_Art_Zeu_Lightning Rod", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Thunder Dash", "Zeus_Electric Shot", "Zeus_Aid"}
								   };
		String[][] ArtemisAidArr = {	{"Duo_Art_Ath_Deadly Reversal", "Athena_Divine Strike", "Athena_Divine Flourish"},
										{"Duo_Art_Dem_Crystal Clarity", "Demeter_Crystal Beam"},
										{"Duo_Art_Dio_Splitting Headache", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"},
										{"Duo_Art_Pos_Mirage Shot", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Duo_Art_Zeu_Lightning Rod", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Thunder Dash", "Zeus_Electric Shot", "Zeus_Aid"}								   
								   };
		String[][] SupportFireArr = { {"Leg_Artemis_Fully Loaded", "Artemis_Pressure Points", "Artemis_Exit Wounds"}
									};
		String[][] PressurePointsArr = { {"Leg_Artemis_Fully Loaded", "Artemis_Support Fire", "Artemis_Exit Wounds"}
									};
		String[][] ExitWoundsArr = { {"Leg_Artemis_Fully Loaded", "Artemis_Pressure Points", "Artemis_Support Fire"}
								   };							
		
		// ATHENA BOONS
		String[][] DivineStrikeArr = {	{"Duo_Aph_Ath_Parting Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Aid", "Aphrodite_Passion Dash"},
										{"Duo_Are_Ath_Merciful End", "Ares_Curse of Agony",  "Ares_Curse of Pain"},
										{"Duo_Art_Ath_Deadly Reversal", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunters Flare", "Artemis_Aid"},
										{"Duo_Ath_Dem_Stubborn Roots", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Crystal Beam", "Demeter_Icy Flare", "Demeter_Mistral Dash", "Demeter_Aid"},
										{"Duo_Ath_Dio_Calculated Risk", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"},
										{"Duo_Ath_Pos_Unshakable Mettle", "Poseidon_Tempest Strike", "Poseidon_Tidal Dash", "Poseidon_Flood Shot"},
										{"Leg_Athena_Divine Protection", "Athena_Brilliant Riposte"}
									 };
		String[][] DivineFlourishArr = {	{"Duo_Aph_Ath_Parting Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Aid", "Aphrodite_Passion Dash"},
											{"Duo_Are_Ath_Merciful End", "Ares_Curse of Agony",  "Ares_Curse of Pain"},
											{"Duo_Art_Ath_Deadly Reversal", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunters Flare", "Artemis_Aid"},
											{"Duo_Ath_Dem_Stubborn Roots", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Crystal Beam", "Demeter_Icy Flare", "Demeter_Mistral Dash", "Demeter_Aid"},
											{"Duo_Ath_Dio_Calculated Risk", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"}				
									   };
		String[][] PhalanxShotArr = {	{"Duo_Aph_Ath_Parting Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Aid", "Aphrodite_Passion Dash"},
										{"Duo_Ath_Dem_Stubborn Roots", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Crystal Beam", "Demeter_Icy Flare", "Demeter_Mistral Dash", "Demeter_Aid"},
										{"Duo_Ath_Zeu_Lightning Phalanx", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Thunder Dash", "Zeus_Electric Shot", "Zeus_Aid"}			
									};
		String[][] DivineDashArr = {	{"Duo_Aph_Ath_Parting Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Aid", "Aphrodite_Passion Dash"},
										{"Duo_Ath_Dem_Stubborn Roots", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Crystal Beam", "Demeter_Icy Flare", "Demeter_Mistral Dash", "Demeter_Aid"},
										{"Duo_Ath_Dio_Calculated Risk", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"},
										{"Duo_Ath_Pos_Unshakable Mettle", "Poseidon_Tempest Strike", "Poseidon_Tidal Dash", "Poseidon_Flood Shot"}				
								   };
		String[][] AthenaAidArr = {	{"Duo_Aph_Ath_Parting Shot", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Aid", "Aphrodite_Passion Dash"},
									{"Duo_Ath_Dem_Stubborn Roots", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Crystal Beam", "Demeter_Icy Flare", "Demeter_Mistral Dash", "Demeter_Aid"},
									{"Duo_Ath_Dio_Calculated Risk", "Dionysus_Aid", "Dionysus_Drunken Dash", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike"}
								  };
		// MAYBE ISSUE
		String[][] BrilliantRiposteArr = {	{"Leg_Athena_Divine Protection", "Athena_Brilliant Riposte"} 
										 };
		
		// Demeter
		String[][] CrystalBeamArr = {	{"Duo_Dem_Aph_Cold Embrace", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Aid", "Aphrodite_Passion Dash"},
										{"Duo_Art_Dem_Crystal Clarity", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_Hunter Dash", "Artemis_Aid"},
										{"Duo_Ath_Dem_Stubborn Roots", "Athena_Divine Strike", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"}
			  						}; 
		String[][] DemeterAidArr = {	{"Duo_Are_Dem_Freezing Vortex", "Ares_Slicing Shot"},
										{"Duo_Ath_Dem_Stubborn Roots", "Athena_Divine Strike", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Dem_Dio_Ice Wine", "Dionysus_Trippy Shot"},
										{"Duo_Dem_Zeu_Cold Fusion", "Zeus_Static Discharge"}		
								   };
		String[][] FrostFlourishArr = {	{"Duo_Are_Dem_Freezing Vortex", "Ares_Slicing Shot"},
										{"Duo_Ath_Dem_Stubborn Roots", "Athena_Divine Strike", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Dem_Dio_Ice Wine", "Dionysus_Trippy Shot"},
										{"Duo_Dem_Pos_Blizzard Shot", "Poseidon_Flood Shot"},
										{"Duo_Dem_Zeu_Cold Fusion", "Zeus_Static Discharge"}
									  };
		String[][] FrostStrikeArr = {	{"Duo_Are_Dem_Freezing Vortex", "Ares_Slicing Shot"},
										{"Duo_Ath_Dem_Stubborn Roots", "Athena_Divine Strike", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Dem_Dio_Ice Wine", "Dionysus_Trippy Shot"},
										{"Duo_Dem_Pos_Blizzard Shot", "Poseidon_Flood Shot"},
										{"Duo_Dem_Zeu_Cold Fusion", "Zeus_Static Discharge"}
			  						};		
		String[][] MistralDashArr = {	{"Duo_Are_Dem_Freezing Vortex", "Ares_Slicing Shot"},
										{"Duo_Ath_Dem_Stubborn Roots", "Athena_Divine Strike", "Athena_Divine Flourish", "Athena_Phalanx Shot", "Athena_Divine Dash", "Athena_Aid"},
										{"Duo_Dem_Dio_Ice Wine", "Dionysus_Trippy Shot"},
										{"Duo_Dem_Pos_Blizzard Shot", "Poseidon_Flood Shot"},
										{"Duo_Dem_Zeu_Cold Fusion", "Zeus_Static Discharge"}
									};
		String[][] SnowBurstArr = {	{"Duo_Dem_Pos_Blizzard Shot", "Poseidon_Flood Shot"}
								  };
		String[][] KillingFreezeArr = {	{"Leg_Demeter_Winter Harvest", "Demeter_Ravenous Will", "Demeter_Arctic Blast"}	
		};
		String[][] RavenousWillArr = {	{"Leg_Demeter_Winter Harvest", "Demeter_Killing Freeze", "Demeter_Arctic Blast"}	
		};
		String[][] ArcticBlastArr = {	{"Leg_Demeter_Winter Harvest", "Demeter_Ravenous Will", "Demeter_Killing Freeze"}	
		};
		// Dionysus BOONS
		String[][] DrunkenStrikeArr = {	{"Duo_Aph_Dio_Low Tolerance", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
										{"Duo_Are_Dio_Curse of Nausea", "Ares_Curse of Agony", "Ares_Curse of Vengeance", "Ares_Curse of Pain"},
										{"Duo_Art_Dio_Splitting Headache", "Artemis_Aid", "Artemis_Deadly Flourish", "Artemis_Deadly Strike", "Artemis_True Shot"},
										{"Duo_Ath_Dio_Calculated Risk", "Athena_Aid", "Athena_Divine Dash", "Athena_Divine Flourish", "Athena_Divine Strike"},
										{"Duo_Dio_Pos_Exclusive Access", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Leg_Dionysus_Black Out", "Dionysus_High Tolerance", "Dionysus_Trippy Shot"}
									  };
		String[][] DrunkenFlourishArr = {	{"Duo_Aph_Dio_Low Tolerance", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
											{"Duo_Are_Dio_Curse of Nausea", "Ares_Curse of Agony", "Ares_Curse of Vengeance", "Ares_Curse of Pain"},
											{"Duo_Art_Dio_Splitting Headache", "Artemis_Aid", "Artemis_Deadly Flourish", "Artemis_Deadly Strike", "Artemis_True Shot"},
											{"Duo_Ath_Dio_Calculated Risk", "Athena_Aid", "Athena_Divine Dash", "Athena_Divine Flourish", "Athena_Divine Strike"},
											{"Duo_Dio_Pos_Exclusive Access", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
											{"Leg_Dionysus_Black Out", "Dionysus_High Tolerance", "Dionysus_Trippy Shot"}
			  							};
		String[][] DrunkenDashArr = {	{"Duo_Aph_Dio_Low Tolerance", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
										{"Duo_Are_Dio_Curse of Nausea", "Ares_Curse of Agony", "Ares_Curse of Vengeance", "Ares_Curse of Pain"},
										{"Duo_Art_Dio_Splitting Headache", "Artemis_Aid", "Artemis_Deadly Flourish", "Artemis_Deadly Strike", "Artemis_True Shot"},
										{"Duo_Ath_Dio_Calculated Risk", "Athena_Aid", "Athena_Divine Dash", "Athena_Divine Flourish", "Athena_Divine Strike"},
										{"Duo_Dio_Pos_Exclusive Access", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"}
									};
		String[][] DionysusAidArr = {	{"Duo_Aph_Dio_Low Tolerance", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash"},
										{"Duo_Art_Dio_Splitting Headache", "Artemis_Aid", "Artemis_Deadly Flourish", "Artemis_Deadly Strike", "Artemis_True Shot"},
										{"Duo_Ath_Dio_Calculated Risk", "Athena_Aid", "Athena_Divine Dash", "Athena_Divine Flourish", "Athena_Divine Strike"},
										{"Duo_Dio_Pos_Exclusive Access", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Leg_Dionysus_Black Out", "Dionysus_High Tolerance", "Dionysus_Trippy Shot"}
									};	
		String[][] TrippyShotArr = {	{"Duo_Dio_Pos_Exclusive Access", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"},
										{"Duo_Dem_Dio_Ice Wine", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Mistral Dash", "Demeter_Aid"},
										{"Duo_Dio_Zeu_Scintillating Feast", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Dionysus_Black Out", "Dionysus_Bad Influence", "Dionysus_Aid", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike", "Dionysus_Numbing Sensation", "Dionysus_Peer Pressure"}
								   };
		String[][] HighToleranceArr = {	{"Duo_Dio_Zeu_Scintillating Feast", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Dionysus_Black Out", "Dionysus_Bad Influence", "Dionysus_Aid", "Dionysus_Drunken Flourish", "Dionysus_Drunken Strike", "Dionysus_Numbing Sensation", "Dionysus_Peer Pressure"}
		   							  };
		String[][] BadInfluenceArr = {	{"Leg_Dionysus_Black Out", "Dionysus_High Tolerance", "Dionysus_Trippy Shot"}
									 };	
		String[][] NumbingSensationArr = {	{"Leg_Dionysus_Black Out", "Dionysus_High Tolerance", "Dionysus_Trippy Shot"}
		 };
		String[][] PeerPressureArr = {	{"Leg_Dionysus_Black Out", "Dionysus_High Tolerance", "Dionysus_Trippy Shot"}
		 };
		
		// HERMES BOONS
		String[][] FlurryCastArr = { {"Leg_Hermes_Greater Recall", "Hermes_Flurry Cast"}			
								   };
		
		String[][] QuickReloadArr = { {"Leg_Hermes_Greater Recall", "Hermes_Quick Reload"}			
		   						   };
		// NEED TO ADD PLUME LATER
		//String[][] AutoReloadArr = {	{"Leg_Hermes_Bad News", "Hermes_Auto Reload", "Keepsake_Lambent Flume"}			
		//						   };
		
		// POSEIDON BOONS
		// FloodShot arr = {"Duo_Are_Pos_Curse of Drowning", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Blade Dash", "Ares_Aid"} 
		String[][] TempestStrikeArr = {	{"Duo_Aph_Pos_Sweet Nectar", "Aphrodite_Heartbreak Strike", "Aphrodite_Hertbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash",  "Aphrodite_Aid"},
										{"Duo_Art_Pos_Mirage Shot", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter's Flare", "Artemis_Aid"},
										{"Duo_Ath_Pos_Unshakable Mettle", "Athena_Divine Strike", "Athena_Divine Dash"},
										{"Duo_Dio_Pos_Exclusive Access", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Trippy Shot", "Dionysus_Drunken Dash", "Dionysus_Aid"},
										{"Duo_Pos_Zeu_Sea Storm", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Poseidon_Second Wave", "Poseidon_Breaking Wave", "Poseidon_Razor Shoals", "Poseidon_Typhoons Fury"}
									  };
		String[][] TempestFlourishArr = {	{"Duo_Aph_Pos_Sweet Nectar", "Aphrodite_Heartbreak Strike", "Aphrodite_Hertbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash",  "Aphrodite_Aid"},
											{"Duo_Art_Pos_Mirage Shot", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter's Flare", "Artemis_Aid"},
											{"Duo_Dio_Pos_Exclusive Access", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Trippy Shot", "Dionysus_Drunken Dash", "Dionysus_Aid"},
											{"Duo_Pos_Zeu_Sea Storm", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
											{"Leg_Poseidon_Second Wave", "Poseidon_Breaking Wave", "Poseidon_Razor Shoals", "Poseidon_Typhoons Fury"}
										};
		String[][] FloodShotArr = {		{"Duo_Aph_Pos_Sweet Nectar", "Aphrodite_Heartbreak Strike", "Aphrodite_Hertbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash",  "Aphrodite_Aid"},
										{"Duo_Are_Pos_Curse of Drowning", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Blade Dash", "Ares_Aid"},
										{"Duo_Art_Pos_Mirage Shot", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter's Flare", "Artemis_Aid"},
										{"Duo_Ath_Pos_Unshakable Mettle", "Athena_Divine Strike", "Athena_Divine Dash"},
										{"Duo_Dem_Pos_Blizzard Shot", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Mistral Dash", "Demeter_Snow Burst"},
										{"Duo_Dio_Pos_Exclusive Access", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Trippy Shot", "Dionysus_Drunken Dash", "Dionysus_Aid"},
										{"Duo_Pos_Zeu_Sea Storm", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Poseidon_Second Wave", "Poseidon_Breaking Wave", "Poseidon_Razor Shoals", "Poseidon_Typhoons Fury"}
								  };
		String[][] TidalDashArr = {	{"Duo_Aph_Pos_Sweet Nectar", "Aphrodite_Heartbreak Strike", "Aphrodite_Hertbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash",  "Aphrodite_Aid"},
									{"Duo_Art_Pos_Mirage Shot", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter's Flare", "Artemis_Aid"},
									{"Duo_Ath_Pos_Unshakable Mettle", "Athena_Divine Strike", "Athena_Divine Dash"},
									{"Duo_Dio_Pos_Exclusive Access", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Trippy Shot", "Dionysus_Drunken Dash", "Dionysus_Aid"},
									{"Duo_Pos_Zeu_Sea Storm", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
									{"Leg_Poseidon_Second Wave", "Poseidon_Breaking Wave", "Poseidon_Razor Shoals", "Poseidon_Typhoons Fury"}
								  };
		String[][] PoseidonAidArr = {	{"Duo_Aph_Pos_Sweet Nectar", "Aphrodite_Heartbreak Strike", "Aphrodite_Hertbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash",  "Aphrodite_Aid"},
										{"Duo_Dio_Pos_Exclusive Access", "Dionysus_Drunken Strike", "Dionysus_Drunken Flourish", "Dionysus_Trippy Shot", "Dionysus_Drunken Dash", "Dionysus_Aid"},
										{"Duo_Pos_Zeu_Sea Storm", "Zeus_Lightning Strike", "Zeus_Thunder Flourish", "Zeus_Electric Shot", "Zeus_Thunder Dash", "Zeus_Aid"},
										{"Leg_Poseidon_Second Wave", "Poseidon_Breaking Wave", "Poseidon_Razor Shoals", "Poseidon_Typhoons Fury"}
									};
		String[][] BreakingWaveArr = {	{"Leg_Poseidon_Second Wave", "Poseidon_Flood Shot", "Poseidon_Aid", "Poseidon_Tempest Flourish", "Poseidon_Tempest Strike", "Poseidon_Tidal Dash"}
									 };
		String[][] RazorShoalsArr = {	{"Leg_Poseidon_Second Wave", "Poseidon_Flood Shot", "Poseidon_Aid", "Poseidon_Tempest Flourish", "Poseidon_Tempest Strike", "Poseidon_Tidal Dash"}
		 							};
		String[][] TyphoonsFuryArr = {	{"Leg_Poseidon_Second Wave", "Poseidon_Flood Shot", "Poseidon_Aid", "Poseidon_Tempest Flourish", "Poseidon_Tempest Strike", "Poseidon_Tidal Dash"}
		 							 };
		String[][] OceansBountyArr = {	{"Leg_Poseidon_Huge Catch", "Poseidon_Sunken Treasure"}			
									 };
		String[][] SunkenTreasureArr = {	{"Leg_Poseidon_Huge Catch", "Poseidon_Oceans Bounty"}			
									   };
		// ZEUS BOONS
		String[][] LightningStrikeArr = { 	{"Duo_Aph_Zeu_Smoldering Air", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash", "Aphrodite_Aid"},
											{"Duo_Are_Zeu_Vengeful Mood", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Slicing Shot", "Ares_Blade Dash", "Ares_Aid", "Ares_Slicing Flare"},
											{"Duo_Art_Zeu_Lightning Rod", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter Dash", "Artemis_Aid"},
											{"Duo_Ath_Zeu_Lightning Phalanx", "Athena_Phalanx Shot"},
											{"Duo_Dio_Zeu_Scintillating Feast", "Dionysus_Trippy Shot", "Dionysus_High Tolerance"},
											{"Duo_Pos_Zeu_Sea Storm", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"}
										};
		String[][] ThunderFlourishArr = { 	{"Duo_Aph_Zeu_Smoldering Air", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash", "Aphrodite_Aid"},
											{"Duo_Are_Zeu_Vengeful Mood", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Slicing Shot", "Ares_Blade Dash", "Ares_Aid", "Ares_Slicing Flare"},
											{"Duo_Art_Zeu_Lightning Rod", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter Dash", "Artemis_Aid"},
											{"Duo_Ath_Zeu_Lightning Phalanx", "Athena_Phalanx Shot"},
											{"Duo_Dio_Zeu_Scintillating Feast", "Dionysus_Trippy Shot", "Dionysus_High Tolerance"},
											{"Duo_Pos_Zeu_Sea Storm", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"}
										};
		String[][] ElectricShotArr = { 	{"Duo_Aph_Zeu_Smoldering Air", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash", "Aphrodite_Aid"},
										{"Duo_Are_Zeu_Vengeful Mood", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Slicing Shot", "Ares_Blade Dash", "Ares_Aid", "Ares_Slicing Flare"},
										{"Duo_Art_Zeu_Lightning Rod", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter Dash", "Artemis_Aid"},
										{"Duo_Ath_Zeu_Lightning Phalanx", "Athena_Phalanx Shot"},
										{"Duo_Dio_Zeu_Scintillating Feast", "Dionysus_Trippy Shot", "Dionysus_High Tolerance"},
										{"Duo_Pos_Zeu_Sea Storm", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"}
									 };
		String[][] ThunderDashArr = { 	{"Duo_Aph_Zeu_Smoldering Air", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash", "Aphrodite_Aid"},
										{"Duo_Are_Zeu_Vengeful Mood", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Slicing Shot", "Ares_Blade Dash", "Ares_Aid", "Ares_Slicing Flare"},
										{"Duo_Art_Zeu_Lightning Rod", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter Dash", "Artemis_Aid"},
										{"Duo_Ath_Zeu_Lightning Phalanx", "Athena_Phalanx Shot"},
										{"Duo_Dio_Zeu_Scintillating Feast", "Dionysus_Trippy Shot", "Dionysus_High Tolerance"},
										{"Duo_Pos_Zeu_Sea Storm", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"}
			 						};
		String[][] ZeusAidArr = { 	{"Duo_Aph_Zeu_Smoldering Air", "Aphrodite_Heartbreak Strike", "Aphrodite_Heartbreak Flourish", "Aphrodite_Crush Shot", "Aphrodite_Passion Dash", "Aphrodite_Aid"},
									{"Duo_Are_Zeu_Vengeful Mood", "Ares_Curse of Agony", "Ares_Curse of Pain", "Ares_Slicing Shot", "Ares_Blade Dash", "Ares_Aid", "Ares_Slicing Flare"},
									{"Duo_Art_Zeu_Lightning Rod", "Artemis_Deadly Strike", "Artemis_Deadly Flourish", "Artemis_True Shot", "Artemis_Hunter Dash", "Artemis_Aid"},
									{"Duo_Ath_Zeu_Lightning Phalanx", "Athena_Phalanx Shot"},
									{"Duo_Dio_Zeu_Scintillating Feast", "Dionysus_Trippy Shot", "Dionysus_High Tolerance"},
									{"Duo_Pos_Zeu_Sea Storm", "Poseidon_Tempest Strike", "Poseidon_Tempest Flourish", "Poseidon_Flood Shot", "Poseidon_Tidal Dash", "Poseidon_Aid"}
								};
		String[][] StaticDischargeArr = { {"Duo_Dem_Zeu_Cold Fusion", "Demeter_Frost Strike", "Demeter_Frost Flourish", "Demeter_Mistral Dash", "Demeter_Aid"}		
										};
		String[][] StormLightningArr = {	{"Leg_Zeus_Splitting Bolt", "Zeus_Double Strike", "Zeus_High Voltage"}				
									   };
		String[][] DoubleStrikeArr = {	{"Leg_Zeus_Splitting Bolt", "Zeus_Storm Lightning"}				
		   							 };
		String[][] HighVoltageArr = {	{"Leg_Zeus_Splitting Bolt", "Zeus_Storm Lightning"}				
			 						};
		
		// LEGEND CHAOS BOON
		//String[][] ChaosAffluenceArr = { {"Leg_Chaos_Blessing Defiance", "Chaos_Blessing Affluence"}};
		
		String[][] None = {{"NONE", "NONE"}};
		
		allItems.put("1 EMPTY", None);
		allItems.put("Aphrodite_Aid", AphroditeAidArr);
		allItems.put("Aphrodite_Blown Kiss", None);
		allItems.put("Aphrodite_Broken Resolve", BrokenResolveArr);
		allItems.put("Aphrodite_Crush Shot", CrushShotArr);
		allItems.put("Aphrodite_Different League", None);
		allItems.put("Aphrodite_Dying Lament", None);
		allItems.put("Aphrodite_Empty Inside", EmptyInsideArr);
		allItems.put("Aphrodite_Heartbreak Flourish", HeartbreakFlourishArr);
		allItems.put("Aphrodite_Heartbreak Strike", HeartbreakStrikeArr);
		allItems.put("Aphrodite_Life Affirmation", None);
		allItems.put("Aphrodite_Passion Dash", PassionDashArr);
		allItems.put("Aphrodite_Sweet Surrender", SweetSurrenderArr);
		allItems.put("Aphrodite_Wave of Despair", None);
		allItems.put("Ares_Aid", AresAidArr);
		allItems.put("Ares_Battle Rage", None);
		allItems.put("Ares_Black Metal", None);
		allItems.put("Ares_Blade Dash", BladeDashArr);
		allItems.put("Ares_Blood Frenzy", None);
		allItems.put("Ares_Curse of Agony", CurseOfAgonyArr);
		allItems.put("Ares_Curse of Pain", CurseOfPainArr);
		allItems.put("Ares_Curse of Vengeance", CurseOfVengeanceArr);
		allItems.put("Ares_Dire Misfortune", None);
		allItems.put("Ares_Engulfing Vortex", None);
		allItems.put("Ares_Impending Doom", None);
		allItems.put("Ares_Slicing Shot", SlicingShotArr);
		allItems.put("Ares_Urge to Kill", None);
		allItems.put("Artemis_Aid", ArtemisAidArr);
		allItems.put("Artemis_Clean Kill", None);
		allItems.put("Artemis_Deadly Flourish", DeadlyFlourishArr);
		allItems.put("Artemis_Deadly Strike", DeadlyStrikeArr);
		allItems.put("Artemis_Exit Wounds", ExitWoundsArr);
		allItems.put("Artemis_Hide Breaker", None);
		allItems.put("Artemis_Hunters Mark", None);
		allItems.put("Artemis_Hunter Dash", HunterDashArr);
		allItems.put("Artemis_Hunter Instinct", None);
		allItems.put("Artemis_Pressure Points", PressurePointsArr);
		allItems.put("Artemis_Support Fire", SupportFireArr);
		allItems.put("Artemis_True Shot", TrueShotArr);
		allItems.put("Athena_Aid", AthenaAidArr);
		allItems.put("Athena_Blinding Flash", None);
		allItems.put("Athena_Brilliant Riposte", BrilliantRiposteArr);
		allItems.put("Athena_Bronze Skin", None);
		allItems.put("Athena_Deathless Stand", None);
		allItems.put("Athena_Divine Dash", DivineDashArr);
		allItems.put("Athena_Divine Flourish", DivineFlourishArr);
		allItems.put("Athena_Divine Strike", DivineStrikeArr);
		allItems.put("Athena_Holy Shield", None);
		allItems.put("Athena_Last Stand", None);
		allItems.put("Athena_Phalanx Shot", PhalanxShotArr);
		allItems.put("Athena_Proud Bearing", None);
		allItems.put("Athena_Sure Footing", None);
		allItems.put("Bouldy_Heart of Stone", None);
		allItems.put("Chaos_Blessing Affluence", None);
		allItems.put("Chaos_Blessing Ambush", None);
		allItems.put("Chaos_Blessing Assault", None);
		allItems.put("Chaos_Blessing Eclipse", None);
		allItems.put("Chaos_Blessing Favor", None);
		allItems.put("Chaos_Blessing Flourish", None);
		allItems.put("Chaos_Blessing Grasp", None);
		allItems.put("Chaos_Blessing Lunge", None);
		allItems.put("Chaos_Blessing Shot", None);
		allItems.put("Chaos_Blessing Soul", None);
		allItems.put("Chaos_Blessing Strike", None);
		allItems.put("Demeter_Aid", DemeterAidArr);
		allItems.put("Demeter_Arctic Blast", ArcticBlastArr);
		allItems.put("Demeter_Crystal Beam", CrystalBeamArr);
		allItems.put("Demeter_Frost Flourish", FrostFlourishArr);
		allItems.put("Demeter_Frost Strike", FrostStrikeArr);
		allItems.put("Demeter_Frozen Touch", None);
		allItems.put("Demeter_Glacial Glare", None);
		allItems.put("Demeter_Killing Freeze", KillingFreezeArr);
		allItems.put("Demeter_Mistral Dash", MistralDashArr);
		allItems.put("Demeter_Nourished Soul", None);
		allItems.put("Demeter_Rare Crop", None);
		allItems.put("Demeter_Ravenous Will", RavenousWillArr);
		allItems.put("Demeter_Snow Burst", SnowBurstArr);
		allItems.put("Dionysus_After Party", None);
		allItems.put("Dionysus_Aid", DionysusAidArr);
		allItems.put("Dionysus_Bad Influence", BadInfluenceArr);
		allItems.put("Dionysus_Drunken Dash", DrunkenDashArr);
		allItems.put("Dionysus_Drunken Flourish", DrunkenFlourishArr);
		allItems.put("Dionysus_Drunken Strike", DrunkenStrikeArr);
		allItems.put("Dionysus_High Tolerance", HighToleranceArr);
		allItems.put("Dionysus_Numbing Sensation", NumbingSensationArr);
		allItems.put("Dionysus_Peer Pressure", PeerPressureArr);
		allItems.put("Dionysus_Positive Outlook", None);
		allItems.put("Dionysus_Premium Vintage", None);
		allItems.put("Dionysus_Strong Drink", None);
		allItems.put("Dionysus_Trippy Shot", TrippyShotArr);
		allItems.put("Duo_Aph_Are_Curse of Longing", None);
		allItems.put("Duo_Aph_Art_Heart Rend", None);
		allItems.put("Duo_Aph_Ath_Parting Shot", None);
		allItems.put("Duo_Aph_Dio_Low Tolerance", None);
		allItems.put("Duo_Aph_Pos_Sweet Nectar", None);
		allItems.put("Duo_Aph_Zeu_Smoldering Air", None);
		allItems.put("Duo_Are_Art_Hunting Blades", None);
		allItems.put("Duo_Are_Ath_Merciful End", None);
		allItems.put("Duo_Are_Dem_Freezing Vortex", None);
		allItems.put("Duo_Are_Dio_Curse of Nausea", None);
		allItems.put("Duo_Are_Pos_Curse of Drowning", None);
		allItems.put("Duo_Are_Zeu_Vengeful Mood", None);
		allItems.put("Duo_Art_Ath_Deadly Reversal", None);
		allItems.put("Duo_Art_Dem_Crystal Clarity", None);
		allItems.put("Duo_Art_Dio_Splitting Headache", None);
		allItems.put("Duo_Art_Pos_Mirage Shot", None);
		allItems.put("Duo_Art_Zeu_Lightning Rod", None);
		allItems.put("Duo_Ath_Dem_Stubborn Roots", None);
		allItems.put("Duo_Ath_Dio_Calculated Risk", None);
		allItems.put("Duo_Ath_Pos_Unshakable Mettle", None);
		allItems.put("Duo_Ath_Zeu_Lightning Phalanx", None);
		allItems.put("Duo_Dem_Aph_Cold Embrace", None);
		allItems.put("Duo_Dem_Dio_Ice Wine", None);
		allItems.put("Duo_Dem_Pos_Blizzard Shot", None);
		allItems.put("Duo_Dem_Zeu_Cold Fusion", None);
		allItems.put("Duo_Dio_Pos_Exclusive Access", None);
		allItems.put("Duo_Dio_Zeu_Scintillating Feast", None);
		allItems.put("Duo_Pos_Zeu_Sea Storm", None);
		allItems.put("Hermes_Flurry Cast", FlurryCastArr);
		allItems.put("Hermes_Greater Evasion", None);
		allItems.put("Hermes_Greater Haste", None);
		allItems.put("Hermes_Greatest Reflex", None);
		allItems.put("Hermes_Hyper Sprint", None);
		allItems.put("Hermes_Quick Favor", None);
		allItems.put("Hermes_Quick Recovery", None);
		allItems.put("Hermes_Quick Reload", QuickReloadArr);
		allItems.put("Hermes_Rush Delivery", None);
		allItems.put("Hermes_Second Wind", None);
		allItems.put("Hermes_Side Hustle", None);
		allItems.put("Hermes_Swift Flourish", None);
		allItems.put("Hermes_Swift Strike", None);
		allItems.put("Leg_Aphrodite_Unhealthy Fixation", None);
		allItems.put("Leg_Ares_Vicious Cycle", None);
		allItems.put("Leg_Artemis_Fully Loaded", None);
		allItems.put("Leg_Athena_Divine Protection", None);
		allItems.put("Leg_Demeter_Winter Harvest", None);
		allItems.put("Leg_Dionysus_Black Out", None);
		allItems.put("Leg_Hermes_Greater Recall", None);
		allItems.put("Leg_Hermes_Bad News", None);
		allItems.put("Leg_Poseidon_Second Wave", None);
		allItems.put("Leg_Poseidon_Huge Catch", None);
		allItems.put("Leg_Zeus_Splitting Bolt", None);
		allItems.put("Leg_Chaos_Blessing Defiance", None);
		allItems.put("Poseidon_Aid", PoseidonAidArr);
		allItems.put("Poseidon_Boiling Point", None);
		allItems.put("Poseidon_Breaking Wave", BreakingWaveArr);
		allItems.put("Poseidon_Flood Shot", FloodShotArr);
		allItems.put("Poseidon_Hydraulic Might", None);
		allItems.put("Poseidon_Oceans Bounty", OceansBountyArr);
		allItems.put("Poseidon_Razor Shoals", RazorShoalsArr);
		allItems.put("Poseidon_Rip Current", None);
		allItems.put("Poseidon_Sunken Treasure", SunkenTreasureArr);
		allItems.put("Poseidon_Tempest Flourish", TempestFlourishArr);
		allItems.put("Poseidon_Tempest Strike", TempestStrikeArr);
		allItems.put("Poseidon_Tidal Dash", TidalDashArr);
		allItems.put("Poseidon_Typhoons Fury", TyphoonsFuryArr);
		allItems.put("Poseidon_Wave Pounding", None);
		allItems.put("Zeus_Aid", ZeusAidArr);
		allItems.put("Zeus_Billowing Strength", None);
		allItems.put("Zeus_Clouded Judgment", None);
		allItems.put("Zeus_Double Strike", DoubleStrikeArr);
		allItems.put("Zeus_Electric Shot", ElectricShotArr);
		allItems.put("Zeus_Heavens Vengeance", None);
		allItems.put("Zeus_Lightning Reflexes", None);
		allItems.put("Zeus_High Voltage", HighVoltageArr);
		allItems.put("Zeus_Lightning Strike", LightningStrikeArr);
		allItems.put("Zeus_Static Discharge", StaticDischargeArr);
		allItems.put("Zeus_Storm Lightning", StormLightningArr);
		allItems.put("Zeus_Thunder Dash", ThunderDashArr);
		allItems.put("Zeus_Thunder Flourish", ThunderFlourishArr);
		allItems.put("Bow Chiron Aspect", None);
		allItems.put("Bow Hera Aspect", None);
		allItems.put("Bow Rama Aspect", None);
		allItems.put("Bow Zagreus Aspect", None);
		allItems.put("Fists Demeter Aspect", None);
		allItems.put("Fists Gilgamesh Aspect", None);
		allItems.put("Fists Talos Aspect", None);
		allItems.put("Fists Zagreus Aspect", None);
		allItems.put("Rail Eris Aspect", None);
		allItems.put("Rail Hestia Aspect", None);
		allItems.put("Rail Lucifer Aspect", None);
		allItems.put("Rail Zagreus", None);
		allItems.put("Shield Chaos Aspect", None);
		allItems.put("Shield Shield Aspect Beuwulf", None);
		allItems.put("Shield Zagreus Aspect", None);
		allItems.put("Shield Zeus Aspect", None);
		allItems.put("Spear Achilles Aspect", None);
		allItems.put("Spear Guan Yu Aspect", None);
		allItems.put("Spear Hades Aspect", None);
		allItems.put("Spear Zagreus Aspect", None);
		allItems.put("Sword Arthur Aspect", None);
		allItems.put("Sword Nemesis Aspect", None);
		allItems.put("Sword Poseidon Aspect", None);
		allItems.put("Sword Zagreus Aspect", None);
	}
}
