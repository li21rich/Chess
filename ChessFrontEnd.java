/*
 * Richard Li
 * 2023
 */
import java.util.*;

public class ChessFrontEnd {
	private static Scanner scn = new Scanner(System.in);
	protected static boolean gameOver = false;
	
	private static boolean debugFriedLiver = false;
	private static ArrayList<String> debugNextMove = new ArrayList<>(Arrays.asList("e2,e4", "e7,e5", "g1,f3", "b8,c6", "f1,c4", "g8,f6", "f3,g5", "d7,d5", "e4,d5", "f6,d5", "g5,f7", "e8,f7", "d1,f3","f7,g8","f3,d5", "d8,d5", "c4,d5", "c8,e6", "d5,e6"));

	public static void main(String[] args) {
		System.out.println("Welcome to Chess (Java edition). Explanation:\n1.Uppercase letters are your pieces, lowercase letters are the opponent's.\n2. To make a move, follow this notation:\n    Column letter of starting piece + row number of starting piece + comma + column letter of target location\n    + row number of target location.\n    For example, to move pawn E2 to E4, you could say: \"e2, e4\" or you might say \"E2,E4\".\n3. It is not case or whitespace sensitive.\n4. At any point, you can resign by entering \"R\" or offer a draw by entering \"D\".\n5. To castle, move your king two steps over to the appropriate square.\n6. To check the previous move, enter \"P\".\nGood luck!");
		String in = "";	
		Board board = new Board();
		boolean fail = false;
		String turn = "White ";
		while(!gameOver) {
			if(board.getTurn()) {
				turn = "White ";
			}else {
				turn = "Black ";
			}
			System.out.println(turn + "to move: ");
			fail = false;
			boolean rdp = false; 
			try {
				if(debugFriedLiver) {
					in = debugNextMove.get(0);
					debugNextMove.remove(0);
				}else{
					in = scn.nextLine().toLowerCase().replaceAll("\\s+", "");
				}
				/*if(in.equals("openingexample") && debugFriedLiver == false) {
					debugFriedLiver = true;
					in = debugNextMove.get(0);
					debugNextMove.remove(0);
				}*/
				if(in.equals("r")) {
					Resign(turn);
					rdp = true;
				}
				if(in.equals("d")) {
					Draw(turn);
					rdp = true;
				}
				tag:
				if(in.equals("p")) {
					rdp = true;
					String[][] hist = board.getPreviousHistory(false);
					if(hist == null) {
						System.out.print("Cannot go back further. ");
						break tag;
					}
					System.out.println("Previous position:");
					renderPreviousPosition(board, hist);
					System.out.print("Type anything when you are ready to continue: ");
					scn.nextLine();
					hist = board.getPreviousHistory(true);
					renderPreviousPosition(board, hist);
				}
				if(gameOver) {
					System.exit(0);
				}
				if(!rdp) {
					in = in.substring(0,1) + "," + in.substring(1,4) + "," + in.substring(4);
				}
			}catch(Exception e) {
				fail = true;
			}
			while(fail || !board.makeMove(in)) {
				if(rdp == false) {
					System.out.println("Invalid input. " + turn + "to move: ");
				}else {
					System.out.println(turn + "to move: ");
				}
				try {
					in = "";
					in = scn.nextLine().toLowerCase().replaceAll("\\s+", "");
					if(in.equals("r") || in.equals("d")) {
						if(in.equals("r")) {
							Resign(turn);
						}else{
							Draw(turn);
						}
						rdp=true;
					}else {
						rdp = false;
					}
					tag:
					if(in.equals("p")) {
						rdp = true;
						String[][] hist = board.getPreviousHistory(false);
						if(hist == null) {
							System.out.print("Cannot go further back. ");
							break tag;
						}
						System.out.println("Previous position:");
						renderPreviousPosition(board, hist);
						System.out.print("Type anything when you are ready to continue: ");
						scn.nextLine();
						hist = board.getPreviousHistory(true);
						renderPreviousPosition(board, hist);
					}
					if(gameOver) {
						System.exit(0);
					}
					if(!rdp) {
						in = in.substring(0,1) + "," + in.substring(1,4) + "," + in.substring(4);
					}
					fail = false;
				}catch(Exception e) {
					//System.out.println(e);
					fail = true;
					rdp = false;
				}
			}
		}
		scn.close();
	}
	public static boolean Draw(String turn) {
        while (true) {
			System.out.println("Are you sure you want to offer a draw? \"Y\" or \"N\"");
            try {
                String input = scn.nextLine().toLowerCase().replaceAll("\\s+", "");
                if (input.equals("y")) {
                	System.out.print(turn + "offered a draw. Do you accept, ");
                	if(turn.equals("White ")) {
                		System.out.print("black? ");
                	}else {
                		System.out.print("white? ");
                	}
                    break;
                }else if(input.equals("n")) {
                	System.out.println("Draw cancelled.");
                	return false;
                }
                System.out.print("Invalid input. ");
            }
            catch (Exception e) {
                System.out.println("Invalid input. Are you sure you want to resign? \"Y\" or \"N\"");
            }
        }
        while (true) {
			System.out.println("Enter \"Y\" or \"N\" to accept or decline the draw.");
            try {
                String input = scn.nextLine().toLowerCase().replaceAll("\\s+", "");
                if (input.equals("y")) {
                	System.out.println("Accepted, the game ends in a draw!");
                    gameOver = true;
                    return true;
                }else if(input.equals("n")) {
                	System.out.println("Draw declined. Play on!");
                	return false;
                }
                System.out.print("Invalid input. ");
            }
            catch (Exception e) {
                System.out.println("Invalid input. Enter \"Y\" or \"N\" to accept or decline the draw.");
            }
        }
	}
	public static boolean Resign(String turn) {

        while (true) {
			System.out.println("Are you sure you want to resign? \"Y\" or \"N\"");
            try {
                String input = scn.nextLine().toLowerCase().replaceAll("\\s+", "");
                if (input.equals("y")) {
                	System.out.print(turn + "resigned. ");
                	if(turn.equals("White ")) {
                		System.out.println("Black wins!");
                	}else {
                		System.out.println("White wins!");
                	}
                    gameOver = true;
                    return true;
                }else if(input.equals("n")) {
                	System.out.println("Resignation cancelled.");
                	return false;
                }
                System.out.print("Invalid input. ");
            }
            catch (Exception e) {
                System.out.println("Invalid input. Are you sure you want to resign? \"Y\" or \"N\"");
            }
        }

	}
	public static void renderPreviousPosition(Board board, String[][] position) {
		int row = 8;
		int set = 0;
		if(!board.getTurn()) {
			row = 1;
			set = 1;
		}
		System.out.println();
		String[] piecesLost = board.getTakenPieces();
		for(int i = 0; i < piecesLost[set].length(); i++) {
			System.out.print(Character.toUpperCase(piecesLost[set].charAt(i)));
		}
		System.out.print("\n" + row + " [ ");
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(j == 7) {
					if(board.getTurn()) {
						row--;
					}else {
						row++;
					}
					System.out.print(position[i][j] + " ]");
					if(i!=7) {
						System.out.print("\n" + row  + " [ ");
					}
				}else {
					System.out.print(position[i][j] + " | ");
				}
			}
		}
		if(board.getTurn()) {
			System.out.println("\n    a   b   c   d   e   f   g   h");
		}else {
			System.out.println("\n    h   g   f   e   d   c   b   a");
		}
		if(set == 0) {
			set = 1;
		}else {
			set = 0;
		}
		for(int i = 0; i < piecesLost[set].length(); i++) {
			System.out.print(Character.toLowerCase(piecesLost[set].charAt(i)));
		}
		System.out.println("\n");
		
	}
}
