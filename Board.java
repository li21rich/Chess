import java.util.ArrayList;
import java.util.Scanner; 

public class Board extends ChessFrontEnd{
	private boolean whiteTurn;
	private String[][] boardList = 
			{{"r", "n", "b", "q", "k", "b", "n", "r"},
			{"p", "p", "p", "p", "p", "p", "p", "p"},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{"P", "P", "P", "P", "P", "P", "P", "P"},
			{"R", "N", "B", "Q", "K", "B", "N", "R"}};
	private ArrayList<String[][]> history = new ArrayList<>();
	/*private String[][] boardList = 
			{{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", "Q", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", " ", " ", " "},
			{" ", " ", " ", " ", " ", "K", " ", "k"}};*/
	private int kingX;
	private int kingY;
	private Scanner scn;
	private int[] checkEnPassant = new int[2];
	private static boolean pawnDoublePush = false;
	private boolean[] pieceMoved; //white king, black king, white left rook, white right rook, black left rook, black right rook
	public Board() {
		whiteTurn = true;
		kingX = 4;
		kingY = 7;
		pieceMoved = new boolean[6];
		scn = new Scanner(System.in);
		render();
	}
	private void render() {
		String[][] boardCopy = new String[boardList.length][boardList[0].length];
		for (int i = 0; i < boardList.length; i++) {
			for (int j = 0; j < boardList[0].length; j++) {
			    boardCopy[i][j] = boardList[i][j];
			}
		}
		history.add(boardCopy);
		int row = 8;
		int set = 0;
		//String col = "a";
		if(!whiteTurn) {
			row = 1;
			set = 1;
			//col = "h";
		}
		System.out.println();
		String[] piecesLost = getTakenPieces();
		for(int i = 0; i < piecesLost[set].length(); i++) {
			printc(String.valueOf(Character.toUpperCase(piecesLost[set].charAt(i))));
		}
		System.out.print("\n" + row + " [ ");
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(j == 7) {
					if(whiteTurn) {
						row--;
					}else {
						row++;
					}
					printc(boardList[i][j]);
					System.out.print(" ]");
					if(i!=7) {
						System.out.print("\n" + row  + " [ ");
					}
				}else {
					printc(boardList[i][j]);
					System.out.print(" | ");
				}
			}
		}
		if(whiteTurn) {
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
		if(inCheck(boardList)) {
			if(!anyMoves()) {
				System.out.print("Checkmate! ");
				if(whiteTurn) {
					System.out.println("Black wins.");
				}else {
					System.out.println("White wins.");
				}
				gameOver = true;
			}else {
				System.out.println("Check!");
			}
		}else if(!anyMoves()) {
			System.out.println("Stalemate.");
			gameOver = true;
		}
	}
	public boolean makeMove(String input) {
		//input format: a-h,1-8>a-h,1-8
		try {
			if(input.length() < 1) {
				return false;
			}
			String[] in = input.split(",");
			int x = in[0].charAt(0) - 'a';
			int y = Integer.valueOf(in[1])-1;
			int x2 = in[2].charAt(0) - 'a';
			int y2 = Integer.valueOf(in[3])-1;
			//System.out.println(x + "," + y);
			if(!whiteTurn) {
				x = 7-x;
				y = 7-y;
				x2 = 7-x2;
				y2 = 7-y2;
			}
			//bug fix upon check
			if(movePiece(x,y,x2,y2)) {
				flipBoard();
				whiteTurn = !whiteTurn;
				render();
				return true;
			}
		}catch(Exception e) {
			//System.out.println(e);
		}
		return false;
	}
	private boolean movePiece(int x, int y, int x2, int y2) {
		y = 7 -y;
		y2= 7 -y2;
		//System.out.println(y2-y);
		//System.out.println(x + ", " + y + ", " + x2 + ", " + y2);
		//pawn double push
		if(boardList[y][x].equals("P") && x2 == x && y2-y == -2 && boardList[y2][x2].equals(" ") && y == 6 && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn advances 2 squares
			boardList[y][x] = " ";
			boardList[y2][x2] = "P";
			checkEnPassant[0] = 7-y2;
			checkEnPassant[1] = 7-x2;
			pawnDoublePush = true;
			return true;
		}
		//en passant
		if(pawnDoublePush && checkEnPassant[0] == y2 && checkEnPassant[1] == x2 && boardList[y][x].equals("P") && Math.abs(x2 - x) == 1 && y2-y == -1 && boardList[y2][x2].equals(" ") && boardList[y][x2].equals("p") && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn takes
			boardList[y][x] = " ";
			boardList[y][x2] = " ";
			boardList[y2][x2] = "P";
			if(y2==0) {
				boardList[y2][x2] = promotePawn();
			}
			return true;
		}
		if(pawnDoublePush) {
			pawnDoublePush = false;
			checkEnPassant[0] = 7;
			checkEnPassant[1] = 7;
		}
		if(boardList[y][x].equals(" ") || x2 > 7 || y2 > 7 || x2 < 0 || y2 < 0 || (x2 == x && y2 == y) || (!boardList[y2][x2].equals(" ") && boardList[y2][x2].equals(boardList[y2][x2].toUpperCase()))) { //invalid
			//System.out.println("A");
			return false;
		}
		//pawn takes
		if(boardList[y][x].equals("P") && Math.abs(x2 - x) == 1 && y2-y == -1 && !boardList[y2][x2].equals(" ") && boardList[y2][x2].equals(boardList[y2][x2].toLowerCase()) && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn takes
			boardList[y][x] = " ";
			boardList[y2][x2] = "P";
			if(y2==0) {
				boardList[y2][x2] = promotePawn();
			}
			return true;
		}
		//pawn pushes
		if(boardList[y][x].equals("P") && x2 == x && y2-y == -1 && boardList[y2][x2].equals(" ") && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn advances
			boardList[y][x] = " ";
			boardList[y2][x2] = "P";
			if(y2==0) {
				boardList[y2][x2] = promotePawn();
			}
			return true;
		}
		if((boardList[y][x].equals("Q") || boardList[y][x].equals("B")) && Math.abs(x2 - x) == Math.abs(y2 - y) && checkMove(x,y,x2,y2, boardList[y][x])) { //bishop or queen moves
			boardList[y2][x2] = boardList[y][x];
			boardList[y][x] = " ";
			return true;
		}
		if((boardList[y][x].equals("Q") || boardList[y][x].equals("R")) && (x2==x || y2 ==y) && checkMove(x,y,x2,y2, boardList[y][x])) { //rook or queen moves
			if(boardList[y][x].equals("R")) { //this will let me know if castle is allowed
				if(whiteTurn) {
					if(x == 0 && !pieceMoved[2]) {
						pieceMoved[2] = true;
					}else if(x == 7 && !pieceMoved[3]){
						pieceMoved[3] = true;
					}
				}else {
					if(x == 0 && !pieceMoved[4]) {
						pieceMoved[4] = true;
					}else if(x == 7 && !pieceMoved[5]){
						pieceMoved[5] = true;
					}
				}
			}
			boardList[y2][x2] = boardList[y][x];
			boardList[y][x] = " ";
			return true;
		}
		if(boardList[y][x].equals("N") && ((Math.abs(x2-x) ==1 && Math.abs(y2-y) ==2) || (Math.abs(x2-x) ==2 && Math.abs(y2-y)==1)) && checkMove(x,y,x2,y2, boardList[y][x])) { //knight moves
			boardList[y2][x2] = boardList[y][x];
			boardList[y][x] = " ";
			return true;
		}
		if(boardList[y][x].equals("K") && ((Math.abs(x2-x)<=1 && Math.abs(y2-y)<=1))&& checkMove(x,y,x2,y2, boardList[y][x])) {
			if(whiteTurn && !pieceMoved[0]) {
				pieceMoved[0] = true;
			}else if(!whiteTurn && !pieceMoved[1]) {
				pieceMoved[1] = true;
			}
			boardList[y2][x2] = boardList[y][x];
			boardList[y][x] = " ";
			return true;
		}
		/*
		for(boolean a : pieceMoved) {
			System.out.print(a);
		}*/
		if(!inCheck(boardList) && boardList[y][x].equals("K") && ((whiteTurn && !pieceMoved[0]) || (!whiteTurn && !pieceMoved[1])) && (x2 == 1 || x2 == 2 || x2 == 5 || x2 == 6) && y2 == 7) {
			//System.out.println("." + castleBlocked("left")); //test case v
			if(!castleBlocked("left") && x2 == 2 && whiteTurn && !pieceMoved[2] && boardList[7][1].equals(" ") && boardList[7][2].equals(" ") && boardList[7][3].equals(" ")) {
				boardList[7][0] = " ";
				boardList[7][1] = " ";
				boardList[7][2] = "K";
				boardList[7][3] = "R";
				boardList[7][4] = " ";
				return true;
			}else if (!castleBlocked("left") && x2 == 1 && !whiteTurn && !pieceMoved[4]  && boardList[7][1].equals(" ") && boardList[7][2].equals(" ") && boardList[7][3].equals(" ")){
				boardList[7][0] = " ";
				boardList[7][1] = "K";
				boardList[7][2] = "R";
				boardList[7][3] = " ";
				return true;
			}else if(!castleBlocked("right") && x2 == 6 && whiteTurn && !pieceMoved[3] && boardList[7][5].equals(" ") && boardList[7][6].equals(" ")) {
				boardList[7][7] = " ";
				boardList[7][6] = "K";
				boardList[7][5] = "R";
				boardList[7][4] = " ";
				return true;
			}else if (!castleBlocked("right") && x2 == 5 && !whiteTurn && !pieceMoved[5] && boardList[7][5].equals(" ") && boardList[7][6].equals(" ")) {
				boardList[7][7] = " ";
				boardList[7][6] = " ";
				boardList[7][5] = "K";
				boardList[7][4] = "R";
				boardList[7][3] = " ";
				return true;
			}
		}
		return false;
	}
	private String promotePawn() {
		String in = " ";
		System.out.print("Enter a letter to promote pawn to (Q, N, R, or B): ");
		try {
		in = scn.nextLine().toUpperCase().replaceAll("\\s+", "");
		}catch(Exception e) {
		}
		while(!"QNRB".contains(in)) {
			System.out.print("Invalid input. Enter a letter to promote your pawn to (Q, N, R, or B): ");
			try {
			in = scn.nextLine().toUpperCase().replaceAll("\\s+", "");
			}catch(Exception e) {
			}
		}
		return in;
	}
	private boolean checkMove(int x,int y,int x2,int y2, String piece) {
		String[][] futureBoard = new String[8][8];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				futureBoard[i][j] = boardList[i][j];
			}
		}
		futureBoard[y2][x2] = piece;
		futureBoard[y][x] = " ";/*
		if(piece.equals("N")) {
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 8; j++) {
					System.out.print(futureBoard[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println("_________");
		}*/
		outer:
		for(int i =0; i < 8; i++) { //locate king
			for( int j = 0; j < 8; j ++) {
				if(boardList[j][i].equals("K")) {
					kingX = i;
					kingY = j;
					break outer;
				}
			}
		}
		//System.out.println(inCheck(futureBoard) + " " + isClearPath(x,y,x2,y2,futureBoard));
		if(!inCheck(futureBoard) && (piece.equals("N") || piece.equals("K") || isClearPath(y,x,y2,x2,futureBoard))) { 
			return true;
		}
		//System.out.print(!inCheck(futureBoard));
		return false;
	}
	private boolean isClearPath(int y, int x, int y2, int x2, String[][] board) {
		//signum: 1 / 0 / -1
	    int factorX = Integer.signum(x2-x); 
	    int factorY = Integer.signum(y2-y); 
		//System.out.println("[startCoords: " + y + "," + x + " endCoords: " + y2 + "," + x2 +"]"/* + " factors: " + factorX + " " + factorY + "]"*/);
	    int checkX = x + factorX; 
	    int checkY = y + factorY; 
	    while(checkX != x2 || checkY != y2) {
	    	//System.out.println(checkY + ", " + checkX);
	    	//System.out.print(checkX + "," + checkY + " " + board[checkY][checkX]);
	        if (!board[checkY][checkX].equals(" ")) { //x and y swapped
	        	//System.out.println(board[checkX][checkY] + checkX + ", " + checkY);
	            return false;
	        }
	        checkX += factorX; 
	        checkY += factorY; 
	    }
	    return true;
	}
	private boolean inCheck(String[][] board) { //location
		outer:
		for(int i =0; i < 8; i++) {
			for( int j = 0; j < 8; j ++) {
				if(board[i][j].equals("K")) {
					kingX = j;
					kingY = i;
					break outer;
				}
			}
		}
		//rook or queen vertical
		for(int i = 0; i < 8; i++) {
			if(board[i][kingX].equals("K")) {
				//i ++;
				continue;
			}
			if((board[i][kingX].equals("q") || board[i][kingX].equals("r")) && isClearPath(i, kingX, kingY, kingX, board)) {
			    /*System.out.println("King:" + kingX + " " + kingY);
				System.out.println("Queen:" + kingX + " " + i);
				System.out.println(board[kingY][kingX]);*/
				return true;
			}
		}
		//rook or queen horizontal
		for(int i = 0; i < 8; i++) {
			if(board[kingY][i].equals("K")) {
				//i ++;
				continue;
			}
			if((board[kingY][i].equals("q") || board[kingY][i].equals("r")) && isClearPath(kingY, i, kingY, kingX,board)) {

				return true;
			}
		}
		//bishop or queen check
	    for(int i = 1; i < 8; i++) {
	        // Check top right of king diagonal
	        if (kingX + i < 8 && kingY - i >= 0) {
	            if (board[kingY - i][kingX + i].equals("q") || board[kingY - i][kingX + i].equals("b")) {
	                if (isClearPath(kingY - i, kingX + i, kingY, kingX,board)) {
	                    return true;
	                }
	            }
	        }
	        // Check top left of king diagonal
	        if (kingX - i >= 0 && kingY - i >= 0) {
	            if (board[kingY - i][kingX - i].equals("q") || board[kingY - i][kingX - i].equals("b")) {
	    	    	//System.out.print(kingY-i + " " + (kingX-i) + " " + kingY + " " + kingX + isClearPath(kingY - i, kingX - i, kingY, kingX,board));
	                if (isClearPath(kingY - i, kingX - i, kingY, kingX,board)) {
	                    return true;
	                }
	            }
	        }
	        // Check bottom left of king diagonal
	        if (kingX - i >= 0 && kingY + i < 8) {
	            if (board[kingY + i][kingX - i].equals("q") || board[kingY + i][kingX - i].equals("b")) {
	                if (isClearPath(kingY + i, kingX - i, kingY, kingX,board)) {
	                    return true;
	                }
	            }
	        }
	        // Check bottom right of king diagonal
	        if (kingX + i < 8 && kingY + i < 8) {
	            if (board[kingY + i][kingX + i].equals("q") || board[kingY + i][kingX + i].equals("b")) {
	                if (isClearPath(kingY + i, kingX + i, kingY, kingX,board)) {
	                    return true;
	                }
	            }
	        }
	    }
	    // knight check
	    int[] xMoves = {2, 2, 1, 1, -1, -1, -2, -2};
	    int[] yMoves = {1, -1, 2, -2, 2, -2, 1, -1};
	    for (int i = 0; i < 8; i++) {
	        int nextX = kingX + xMoves[i];
	        int nextY = kingY + yMoves[i];
	        if (nextX >= 0 && nextX < 8 && nextY >= 0 && nextY < 8 && board[nextY][nextX].equals("n")) {
	        	/*System.out.println("debug: Knight checks. why?");
	        	System.out.println(kingX +" ,"+ kingY);*/
	            return true;
	        }
	    }
	    // pawn check
	    if((kingY-1 >= 0 && kingX +1 < 8 && board[kingY-1][kingX+1].equals("p")) || (kingY-1 >= 0 && kingX -1 >= 0 && board[kingY-1][kingX-1].equals("p"))) {
	    	return true;
	    }
	    //"king check" diagonal
	    if((kingY-1>=0 && kingX+1 < 8 && board[kingY-1][kingX+1].equals("k")) || (kingY+1< 8 && kingX+1< 8 && board[kingY+1][kingX+1].equals("k")) || (kingY+1 < 8 && kingX-1>=0 && board[kingY+1][kingX-1].equals("k")) || (kingY-1>=0 && kingX-1 >=0 &&board[kingY-1][kingX-1].equals("k"))) {

	    	return true;
	    }
	    //"king check" straight
	    if((kingX+1 < 8 && board[kingY][kingX+1].equals("k")) || (kingX-1 >=0 && board[kingY][kingX-1].equals("k")) || (kingY+1 < 8 && board[kingY+1][kingX].equals("k")) || (kingY-1>=0 && board[kingY-1][kingX].equals("k"))) {

	    	return true;
	    }
		return false;
	}
	private void flipBoard() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 4; j++) {
				String temp = boardList[i][j];
				boardList[i][j] = boardList[7-i][7-j];
				boardList[7-i][7-j] = temp;
				if(boardList[i][j].equals(boardList[i][j].toLowerCase())) {
					boardList[i][j] = boardList[i][j].toUpperCase();
				}else {
					boardList[i][j] = boardList[i][j].toLowerCase();
				}
				if(boardList[7-i][7-j].equals(boardList[7-i][7-j].toLowerCase())) {
					boardList[7-i][7-j] = boardList[7-i][7-j].toUpperCase();
				}else {
					boardList[7-i][7-j] = boardList[7-i][7-j].toLowerCase();
				}
			}
		}
	}
	public boolean getTurn() {
		return whiteTurn;
	}
	private boolean castleBlocked(String way) { //left or right
		String[][] testBoard = new String[8][8];
		testBoard[7][4] = " ";
		for(int i =0; i < 8; i++) {
			for( int j = 0; j < 8; j ++) {
				testBoard[i][j] = boardList[i][j];
				if(boardList[i][j].equals("K")) {
					kingX = j;
					kingY = i;
				}
			}
		}
		if(way.equals("left")) {
			if(whiteTurn) {
				testBoard[7][4] = " ";
				testBoard[7][3] = "K";
				testBoard[7][2] = "K";
				testBoard[7][1] = " ";
				testBoard[7][0] = " ";
			}else {
				testBoard[7][3] = " ";
				testBoard[7][2] = "K";
				testBoard[7][1] = " ";
				testBoard[7][0] = " ";
			}
		}else {
			if(whiteTurn) {
				testBoard[7][4] = " ";
				testBoard[7][5] = "K";
				testBoard[7][6] = " ";
				testBoard[7][7] = " ";
			}else {
				testBoard[7][3] = " ";
				testBoard[7][4] = "K";
				testBoard[7][5] = "K";
				testBoard[7][6] = " ";
				testBoard[7][7] = " ";
			}
		}
		return inCheck(testBoard);
	}
	private boolean anyMoves() {
		for(int ya = 0; ya < 8; ya++) {
			for(int x = 0; x < 8; x++) {
				if(boardList[7-ya][x].equals(boardList[7-ya][x].toUpperCase())) { 
					for(int y2a = 0; y2a < 8; y2a++) {
						for(int x2 = 0; x2 < 8; x2++) {
							int y = 7 -ya;
							int y2= 7 -y2a;
							if((x2 == x && y2 == y) || (!boardList[y2][x2].equals(" ") && boardList[y2][x2].equals(boardList[y2][x2].toUpperCase()))) {
								continue;
							}
							//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
							if(boardList[y][x].equals("P") && x2 == x && y2-y == -2 && boardList[y2][x2].equals(" ") && y == 6 && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn advances 2 squares
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
							//en passant
							if(pawnDoublePush && checkEnPassant[0] == y2 && checkEnPassant[1] == x2 && boardList[y][x].equals("P") && Math.abs(x2 - x) == 1 && y2-y == -1 && boardList[y2][x2].equals(" ") && boardList[y][x2].equals("p") && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn takes
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
							//pawn takes
							if(boardList[y][x].equals("P") && Math.abs(x2 - x) == 1 && y2-y == -1 && !boardList[y2][x2].equals(" ") && boardList[y2][x2].equals(boardList[y2][x2].toLowerCase()) && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn takes
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
							//pawn pushes
							if(boardList[y][x].equals("P") && x2 == x && y2-y == -1 && boardList[y2][x2].equals(" ") && checkMove(x,y,x2,y2, boardList[y][x])) { //pawn advances
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
							if((boardList[y][x].equals("Q") || boardList[y][x].equals("B")) && Math.abs(x2 - x) == Math.abs(y2 - y) && checkMove(x,y,x2,y2, boardList[y][x])) { //bishop or queen moves
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
							if((boardList[y][x].equals("Q") || boardList[y][x].equals("R")) && (x2==x || y2 ==y) && checkMove(x,y,x2,y2, boardList[y][x])) { //rook or queen moves
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
							if(boardList[y][x].equals("N") && ((Math.abs(x2-x) ==1 && Math.abs(y2-y) ==2) || (Math.abs(x2-x) ==2 && Math.abs(y2-y)==1)) && checkMove(x,y,x2,y2, boardList[y][x])) { //knight moves
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2] + " not putting in check? " + checkMove(x,y,x2,y2, boardList[y][x]));
								return true;
							}
							if(boardList[y][x].equals("K") && ((Math.abs(x2-x)<=1 && Math.abs(y2-y)<=1))&& checkMove(x,y,x2,y2, boardList[y][x])) {
								//System.out.println(x + "," + y + " " + x2 + "," + y2 + " - " + boardList[y][x] + " : " + boardList[y2][x2]);
								return true;
							}
						}
					}
				}
			}
		}
		//System.out.println("noMoves");
		return false;
	}
	
	protected String[] getTakenPieces() {
		String[] pieces = {"KQRRBBNNPPPPPPPP", "kqrrbbnnpppppppp"}; //white pieces lost, black pieces lost
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				String ref = boardList[i][j];
				if(!ref.equals(" ")) {
					int set = -1;
					if(ref.equals(ref.toUpperCase())) { //ref is uppercase
						set = 0;
						if(!whiteTurn) {
							set=1;
						}
						if(pieces[set].charAt(0) == Character.toLowerCase(pieces[set].charAt(0))) {
							ref = ref.toLowerCase();
						}
					}else { //ref is lowercase
						set = 1;
						if(!whiteTurn) {
							set=0;
						}
						if(pieces[set].charAt(0) == Character.toUpperCase(pieces[set].charAt(0))) {
							ref = ref.toUpperCase();
						}
					}
					//System.out.print("[" + ref + ", " + pieces[set].indexOf(ref) + "]");
					//try {
					pieces[set] = pieces[set].substring(0, pieces[set].indexOf(ref)) + pieces[set].substring(pieces[set].indexOf(ref)+1);
					//}catch(Exception e) {
					//	System.out.print(e);
					//}
				}
			}
		}
		return pieces;
	}
	public String[][] getPreviousHistory(boolean cur){
		if(cur) {
			return history.get(history.size()-1);
		}else if(history.size()-2 > -1) {
			return history.get(history.size()-2);
		}else {
			return null;
		}
	}
	public void printc(String print) {
		//if(((whiteTurn && print.equals(print.toUpperCase())) || (!whiteTurn && print.equals(print.toLowerCase()))) && !print.equals(" ")) {
			  //System.out.print("\033[31;1;4m" + print + "\033[0m");
		//}else {
			System.out.print(print);
		//}
	}
}
