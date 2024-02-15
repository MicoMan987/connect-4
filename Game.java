import java.util.Scanner;

public class Game {
	static Scanner stdin = new Scanner(System.in);
	static Board board;
	static int strategy; // um número escolhido pelo usário; representa um dos 3 algoritmos
	static boolean userIstheFirst;
	public static void sayWhoIsTheWinner() {
		if (board.thereIsAWinner()) {
			if (board.getWinner() == Board.YELLOW)
				System.out.println("\n------ THE WINNER IS PLAYER '" + board.getWinner() + "', YOU LOSE! ------");
			else
				System.out.println("\n------ THE WINNER IS PLAYER '" + board.getWinner() + "', YOU WIN! ------");
		}
		else System.out.println("\n------ THE GAME ENDED IN A DRAW! ------");
	}

	public static void chooseStrategy() {
		System.out.println("Choose a strategy: 1 - MINIMAX, 2 - ALPHA-BETA, 3 - MCTS");
		System.out.print("Insert a number:");
		strategy = stdin.nextInt();
		if(strategy > 3 || strategy < 0)
			strategy = 0;
	}

	public static void decidingWhoIsFirst() {
		System.out.print("\nWant to be the first? (y/n): ");
		String yesOrNo = stdin.next();
		if (yesOrNo.equals("y") || yesOrNo.equals("Y")) userIstheFirst = true;
		else userIstheFirst = false;
	}

	public static boolean gameIsOver(Board board) {
		return board.isFull() || board.thereIsAWinner();
	}

	public static void computerTurn() {
		int column = board.computerTurn(strategy, Board.YELLOW);
		System.out.println("\nComputer move: " + column + "\n");
		board.check();
		System.out.println("0 1 2 3 4 5 6");
		board.printBoard();
	}

	public static void userTurn() {
		System.out.println("\nYour turn, choose a column number");
		while(!board.put(stdin.nextInt(), Board.RED))
			System.out.println("This column is full, choose another one.");
		board.check();
	}	

	public static void main(String[] args) {
		board = new Board();
		chooseStrategy();
		if(strategy != 0){
			decidingWhoIsFirst();
			
			System.out.println("\nEmpty board");
			System.out.println("0 1 2 3 4 5 6");
			board.printBoard();

			// aqui acontecerá toda a interação entre a máquina e a pessoa que está jogando
			while (!gameIsOver(board)) {
				if (!userIstheFirst) {
					computerTurn();
					if (board.thereIsAWinner()) break; // se houve um vencedor sai do ciclo
					userTurn();
				}
				else {
					userTurn();
					if (board.thereIsAWinner()) break; // se houve um vencedor sai do ciclo

					//medir o tempo de execução do turno do computador
					computerTurn();
				}
			}
			sayWhoIsTheWinner();
			board.printBoard();
		}
		else{
			System.out.println("Insert a valid number!");
		}
	}
}
