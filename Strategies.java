import java.util.*;

public class Strategies {
	protected static int bestColumn;
	private static int rootDepth;
	private static boolean ALPHA_BETA;

	static int minimax(int depth, Board board) {
		rootDepth = depth-1;
		ALPHA_BETA = false;
		maxValue(rootDepth, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return bestColumn;
	}

	static int maxValue(int depth, Board board, int alpha, int beta) {
		if (depth == 0) return board.evaluate();
		int value = Integer.MIN_VALUE; // constante da classe Integer
		Map<Board, Integer> successors = board.successors(Board.YELLOW);
		for (Board successor : successors.keySet()) {
			int value2 = minValue(depth-1, successor, alpha, beta);	
			if (value2 > value) {
				value = value2;
				alpha = Math.max(alpha, value);
				// se (value2 > value) bestColumn é atualizado mas somente depois de ser feito backtrack e estiver na root, por isso a condição: if depth == rootDepth
				if (depth == rootDepth) bestColumn = successors.get(successor).intValue(); // get() retorna um Integer é preciso converter para int
			}
			if(ALPHA_BETA && value >= beta) return value;
		}
		return value;
	}

	static int minValue(int depth, Board board, int alpha, int beta) {
		if (depth == 0) return board.evaluate();
		int value = Integer.MAX_VALUE; // constante da classe Integer
		Map<Board, Integer> successors = board.successors(Board.RED);
		for (Board successor : successors.keySet()) {
			int value2 = maxValue(depth-1, successor, alpha, beta);
			if (value2 < value) {
				value = value2;
				beta = Math.min(beta, value);
			}
			if(ALPHA_BETA && value <= alpha) return value;
		}
		return value;
	}

	static int alpha_beta(int depth, Board board) {
		rootDepth = depth-1;
		ALPHA_BETA = true;
		maxValue(rootDepth, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return bestColumn;
	}

	static int mcts(Board board) {
		return new Mcts().bestMove(board);
	}
}
