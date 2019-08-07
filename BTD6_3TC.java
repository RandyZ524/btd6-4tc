import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class BTD6_3TC {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		ArrayList<String> combos = new ArrayList<>();
		combos = readFromComboFile("remaining-combos");
		
		if (combos == null) {
			System.exit(0);
		}
		
loop:	while (true) {
			System.out.print("\nCombos: " + combos.size());
			displayOptionsMenu(0, "Options",
							   "Remove all combos containing x-tower combos (reference \"x-tower-combos\")",
							   "Remove all combos not containing at least one of a list of towers",
							   "Remove specific tower combos",
							   "Display combos",
							   "Read/write to combo files",
							   "Exit");
			
			switch (getInput(0)) {
				case 1: {
					int removed = 0;
					ArrayList<String> toCompletedCombos = readFromComboFile("completed-combos");
					
					if (toCompletedCombos == null) {
						continue loop;
					}
					
					ArrayList<ArrayList<String>> xTowerCombos = new ArrayList<ArrayList<String>>();
					
					try (DataInputStream dataIn = new DataInputStream(new FileInputStream("x-tower-combos.txt"))) {
						byte[] inbuf = new byte[20500];
						dataIn.read(inbuf);
						String str = new String(inbuf);
						String[] strAr = str.split("\n");
						
						for (String s : strAr) {
							String[] towerCombo = s.trim().split("|");
							ArrayList<String> combo = new ArrayList<>();
							
							for (String tower : towerCombo) {
								combo.add(tower);
							}
							
							xTowerCombos.add(combo);
						}
					} catch (IOException ioe) {
						System.out.println("Read error. File \"x-tower-combos\" is probably missing from the folder.");
						continue loop;
					}
					
					for (int i = combos.size() - 1; i >= 0; i--) {
			comboLoop:	for (ArrayList<String> combo : xTowerCombos) {
							for (String tower : combo) {
								if (!combos.get(i).contains(tower)) {
									continue comboLoop;
								}
							}
							
							toCompletedCombos.add(combos.get(i));
							combos.remove(i);
							removed++;
							break;
						}
					}
					
					writeToComboFile("completed-combos", toCompletedCombos);
					writeToComboFile("remaining-combos", combos);
					System.out.println("\n\tRemoved: " + removed);
					break; }
				case 2: {
					ArrayList<String> useableTowers = new ArrayList<>();
					displayOptionsMenu(1, "Options",
									   "Reference \"starting-towers\"",
									   "Reference \"r24-defense-towers\"",
									   "Create manual list");
					
					switch (getInput(1)) {
						case 1:
							useableTowers = createUseableTowersList("starting-towers");
							break;
						case 2:
							useableTowers = createUseableTowersList("r24-defense-towers");
							break;
						case 3:
							int index = 1;
							System.out.println("\n\tEnter towers (press Enter without an entry to finish)");
							
							while (true) {
								System.out.print("\tTower " + index++ + ": ");
								String tower = scanner.nextLine();
								
								if (tower.isEmpty()) {
									break;
								} else {
									useableTowers.add(tower);
								}
							}
							
							break;
					}
					
					if (useableTowers == null) {
						continue loop;
					}
					
					int removed = 0;
					ArrayList<String> toImpossibleCombos = readFromComboFile("impossible-combos");
					
					if (toImpossibleCombos == null) {
						continue loop;
					}
					
		comboLoop:	for (int i = combos.size() - 1; i >= 0; i--) {
						for (String tower : useableTowers) {
							if (combos.get(i).contains(tower)) {
								continue comboLoop;
							}
						}
						
						toImpossibleCombos.add(combos.get(i));
						combos.remove(i);
						removed++;
					}
					
					writeToComboFile("impossible-combos", toImpossibleCombos);
					writeToComboFile("remaining-combos", combos);
					System.out.println("\n\tRemoved: " + removed);
					break; }
				case 3: {
					boolean toCompleted = false;
					int index = 1;
					displayOptionsMenu(1, "Options",
									   "Move to \"completed-combos\"",
									   "Move to \"impossible-combos\"");
					
					switch (getInput(1)) {
						case 1:
							toCompleted = true;
							break;
						case 2:
							toCompleted = false;
							break;
					}
					
					ArrayList<String> towerCombo = new ArrayList<>();
					ArrayList<String> moveToFile = readFromComboFile(toCompleted ? "completed-combos" : "impossible-combos");
					
					if (moveToFile == null) {
						continue loop;
					}
					
					System.out.println("\n\tEnter towers (press Enter without an entry to finish)");
					
					while (true) {
						System.out.print("\tTower " + index++ + ": ");
						String tower = scanner.nextLine();
						
						if (tower.isEmpty()) {
							break;
						} else {
							towerCombo.add(tower);
						}
					}
					
					boolean changed = false;
					int removed = 0;
					
		comboLoop:	for (int i = combos.size() - 1; i >= 0; i--) {
						for (String tower : towerCombo) {
							if (!combos.get(i).contains(tower)) {
								continue comboLoop;
							}
						}
						
						moveToFile.add(combos.get(i));
						combos.remove(i);
						changed = true;
						removed++;
					}
					
					if (changed) {
						writeToComboFile("remaining-combos", combos);
						writeToComboFile(toCompleted ? "completed-combos" : "impossible-combos", moveToFile);
					}
					
					System.out.println("\n\tRemoved: " + removed);
					break; }
				case 4: {
					String fileName = "";
					displayOptionsMenu(1, "Options",
									   "Display completed combos (reference \"completed-combos\")",
									   "Display remaining combos (reference \"remaining-combos\")",
									   "Display \"impossible\" combos (reference \"impossible-combos\")",
									   "Display all combos (reference \"all-combos\")");
									 
					switch (getInput(1)) {
						case 1:
							fileName = "completed-combos";
							break;
						case 2:
							fileName = "remaining-combos";
							break;
						case 3:
							fileName = "impossible-combos";
							break;
						case 4:
							fileName = "all-combos";
							break;
					}
					
					if (displayCombos(fileName) == null) {
						continue loop;
					}
					
					break; }
				case 5: {
					displayOptionsMenu(1, "Options",
									   "Update file \"all-combos\" (reference \"towers\")",
									   "Update file \"remaining-combos\" (append '!' for \"completed\" or '?' for \"impossible\" " +
										   "to combo first)",
									   "Reset files to default state");
									 
					switch (getInput(1)) {
						case 1: {
							ArrayList<String> comboList = createAllCombosList();
							
							if (comboList == null) {
								continue loop;
							}
							
							writeToComboFile("all-combos", comboList);
							break; }
						case 2: {
							ArrayList<String> remainingCombos = readFromComboFile("remaining-combos");
							ArrayList<String> completedCombos = readFromComboFile("completed-combos");
							ArrayList<String> impossibleCombos = readFromComboFile("impossible-combos");
							
							if (remainingCombos == null || completedCombos == null || impossibleCombos == null) {
								continue loop;
							}
							
							boolean remainingChanged = false;
							boolean completedChanged = false;
							boolean impossibleChanged = false;
							int movedToCompleted = 0;
							int movedToImpossible = 0;
							
							for (int i = remainingCombos.size() - 1; i >= 0; i--) {
								String[] strAr = remainingCombos.get(i).split("\\|");
								
								if (strAr.length >= 5) {
									String combo = remainingCombos.get(i);
									
									if (strAr[4].equals("!")) {
										completedCombos.add(combo.replace("!", ""));
										remainingCombos.remove(i);
										completedChanged = true;
										movedToCompleted++;
									} else if (strAr[4].equals("?")) {
										impossibleCombos.add(combo.replace("?", ""));
										remainingCombos.remove(i);
										impossibleChanged = true;
										movedToImpossible++;
									} else {
										remainingCombos.set(i, combo.substring(0, combo.lastIndexOf("|")));
										remainingChanged = true;
									}
								}
							}
							
							if (remainingChanged || (completedChanged || impossibleChanged)) {
								combos = remainingCombos;
								writeToComboFile("remaining-combos", remainingCombos);
								
								if (completedChanged) {
									writeToComboFile("completed-combos", completedCombos);
								}
								
								if (impossibleChanged) {
									writeToComboFile("impossible-combos", impossibleCombos);
								}
							}
							
							System.out.println("\n\tMoved to completed: " + movedToCompleted +
											   "\n\tMoved to impossible: " + movedToImpossible);
							break; }
						case 3: {
							displayOptionsMenu(2, "Are you sure you want to reset everything?",
											   "Yes",
											   "No");
											   
							switch (getInput(2)) {
								case 1:
									System.out.println("\nFiles \"remaining-combos\", \"completed-combos\", and " +
													   "\"impossible-combos\" have been reset.");
									break;
								case 2: continue loop;
							}
							
							combos = readFromComboFile("all-combos");
							
							if (combos == null) {
								continue loop;
							}
							
							writeToComboFile("remaining-combos", combos);
							writeToComboFile("completed-combos", new ArrayList<String>());
							writeToComboFile("impossible-combos", new ArrayList<String>());
							break; }
					}
					break; }
				case 6:
					System.exit(0);
					break;
			}
		}
	}
	
	public static ArrayList<String> createAllCombosList() {
		ArrayList<String> towers = new ArrayList<>();
		ArrayList<String> heroes = new ArrayList<>();
		ArrayList<String> combos = new ArrayList<>();
		
		try (DataInputStream dataIn = new DataInputStream(new FileInputStream("towers.txt"))) {
			byte[] inbuf = new byte[20500];
			dataIn.read(inbuf);
			String str = new String(inbuf);
			String[] strAr = str.split("\n");
			boolean atHeroes = false;
			
			for (String s : strAr) {
				s = s.trim();
				
				if (s.contains("-")) {
					atHeroes = true;
					continue;
				}
				
				if (!atHeroes) {
					towers.add(s);
				} else {
					heroes.add(s);
				}
			}
		} catch (IOException ioe) {
			System.out.println("Read error. File \"towers\" is probably missing from the folder.");
			return null;
		}
		
		for (int i = 0; i < towers.size() - 2; i++) {
			for (int j = i + 1; j < towers.size() - 1; j++) {
				for (int k = j + 1; k < towers.size(); k++) {
					String combo = "|" + towers.get(i) + towers.get(j) + towers.get(k);
					combos.add(combo);
				}
			}
		}
		
		for (int i = 0; i < heroes.size(); i++) {
			for (int j = 0; j < towers.size() - 1; j++) {
				for (int k = j + 1; k < towers.size(); k++) {
					String combo = "|" + heroes.get(i) + towers.get(j) + towers.get(k);
					combos.add(combo);
				}
			}
		}
		
		return combos;
	}
	
	public static ArrayList<String> createUseableTowersList(String fileName) {
		try (DataInputStream dataIn = new DataInputStream(new FileInputStream(fileName + ".txt"))) {
			byte[] inbuf = new byte[20500];
			dataIn.read(inbuf);
			String str = new String(inbuf);
			String[] strAr = str.split("\n");
			
			ArrayList<String> useableTowers = new ArrayList<>();
			
			for (String s : strAr) {
				useableTowers.add(s.trim());
			}
			
			return useableTowers;
		} catch (IOException ioe) {
			System.out.println("Read error. File \"" + fileName + "\" is probably missing from the folder.");
			return null;
		}
	}

	public static String displayCombos(String fileName) {
		try (DataInputStream dataIn = new DataInputStream(new FileInputStream(fileName + ".txt"))) {
			byte[] inbuf = new byte[205000];
			dataIn.read(inbuf);
			String str = new String(inbuf);
			String[] strAr = str.split("\n");
			
			for (String s : strAr) {
				System.out.println(s.trim());
			}
			
			return "";
		} catch (IOException ioe) {
			System.out.println("Read error. File \"" + fileName + "\" is probably missing from the folder.");
			return null;
		}
	}

	public static ArrayList<String> readFromComboFile(String fileName) {
		try (DataInputStream dataIn = new DataInputStream(new FileInputStream(fileName + ".txt"))) {
			byte[] inbuf = new byte[205000];
			dataIn.read(inbuf);
			String str = new String(inbuf);
			String[] strAr = str.split("\n");
			
			ArrayList<String> combos = new ArrayList<>();
			
			for (String s : strAr) {
				if (!s.trim().isEmpty()) {
					combos.add(s.trim());
				}
			}
			
			return combos;
		} catch (IOException ioe) {
			System.out.println("Read error. File \"" + fileName + "\" is probably missing from the folder.");
			return null;
		}
	}
	
	public static void writeToComboFile(String fileName, ArrayList<String> combos) {
		try (PrintWriter writer = new PrintWriter(fileName + ".txt", "UTF-8")) {
			for (String combo : combos) {
				writer.println(combo);
			}
		} catch (IOException ioe) {
			System.out.println("Write error. Something's gone horribly wrong.");
			return;
		}
	}
	
	public static void displayOptionsMenu(int depth, String heading, String... options) {
		int maxLength = heading.length() - 3;
		int index = 1;
		String tabs = "";
		String border = "";
		
		for (int i = 0; i < depth; i++) {
			tabs += "\t";
		}
		
		for (String option : options) {
			if (option.length() > maxLength) {
				maxLength = option.length();
			}
		}
		
		for (int i = 0; i < maxLength + 7; i++) {
			border += "*";
		}
		
		System.out.println("\n" + tabs + border);
		System.out.println(tabs + getPaddedString(maxLength, heading));
		System.out.println(tabs + border);
		
		for (String option : options) {
			System.out.println(tabs + getPaddedString(maxLength, index++ + ") " + option));
		}
		
		System.out.println(tabs + border);
	}
	
	public static int getInput(int depth) {
		String tabs = "";
		
		for (int i = 0; i < depth; i++) {
			tabs += "\t";
		}
		
		System.out.print(tabs + "Choose: ");
		Scanner scanner = new Scanner(System.in);
		
		while (!scanner.hasNextInt()) {
			System.out.print(tabs + "Choose: ");
			scanner.next();
		}
		
		int select = scanner.nextInt();
		return select;
	}

	public static String getPaddedString(int length, String str) {
		String paddedString = "* " + str;
		
		for (int i = 0; i < length - str.length() + 4; i++) {
			paddedString += " ";
		}
		
		paddedString += "*";
		return paddedString;
	}
}