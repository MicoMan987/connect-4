import java.util.*;

class Mcts {
	final int TOTAL_ITERATIONS = 1000;
	Node root;

	int bestMove(Board board) {
		root = new Node(board, Board.RED, null);
		int iteration = 0;
		while (iteration < TOTAL_ITERATIONS) {
			Node leaf = transverseTheTree(root);
			if (!gameIsOver(leaf.getBoard())) {
				generateChildren(leaf);
			}
			Node node = leaf;
			if (leaf.hasChild()) {
				node = leaf.getAnyChild(); // um filho qualquer serve, da proxima vez esse nó, não será mais uma folha, então será usada a UCT
			}
			int simulationResult = simulate(node);
			backPropagate(simulationResult, node);
			iteration++;
		}
		// retornar o numero da coluna correspondente ao melhor nó
		return bestChildColumnNumber();
	}

	Node transverseTheTree(Node root) {
		Node node = root;
		while (node.hasChild())
			node = node.selectBestChild();
		return node;
	}

	boolean gameIsOver(Board board) {
		return board.isFull() || board.thereIsAWinner();
	}

	void generateChildren(Node leaf) {
		char player = nextPlayer(leaf.getCurrentPlayer());
		for (Board board : leaf.getBoard().successors(player).keySet()) {
			Node newChild = new Node(board, player, leaf);
			leaf.addToChildrenList(newChild); // adiciona o novo filho à lista
		}
	}

	// retorna o proximo a jogar, dependendo de quem é foi o ultimo; se currentPlayer é 'X' retorna 'O', vice-versa
	char nextPlayer(char currentPlayer) {
		if (currentPlayer == Board.RED) return Board.YELLOW;
		return Board.RED;
	}

	int simulate(Node node) {
		Board simulationBoard = new Board(node.getBoard());
		char currentPlayer = node.getCurrentPlayer();
		while(!gameIsOver(simulationBoard)) {
			currentPlayer = nextPlayer(currentPlayer);
			int randomMove = pickRandomMove(simulationBoard);
			simulationBoard.put(randomMove, currentPlayer);
			simulationBoard.check();
		}
		if (simulationBoard.thereIsAWinner()) {
			if (simulationBoard.getWinner() == Board.YELLOW) return 1; // 'X' venceu
			else return -1; // 'O' venceu
		}
		return 0; // empate
	}

	int pickRandomMove(Board simulationBoard) {
		int[] possibleMoves = getPossibleMoves(simulationBoard);
		int randomIndex = new Random().nextInt(possibleMoves.length); // indice aleatório entre 0 e possibleMoves.length-1 
		return possibleMoves[randomIndex];
	}

	int[] getPossibleMoves(Board simulationBoard) {
		List<Integer> possibleMoves = new ArrayList<>();
		for (int column = 0; column <= 6; column++) {
			if (!simulationBoard.isColumnFull(column))
				possibleMoves.add(column);
		}
		return possibleMoves.stream().mapToInt(Integer::intValue).toArray();
	}

	void backPropagate(int result, Node node) {
		Node tmpNode = node;
		while (tmpNode != null) {
			if ((result == 1 && tmpNode.getCurrentPlayer() == Board.YELLOW) || (result == -1 && tmpNode.getCurrentPlayer() == Board.RED))
				tmpNode.incrementStats(1);
			else tmpNode.incrementStats(0);
			tmpNode = tmpNode.getParent();
		}
	}

	int bestChildColumnNumber() {
		int bestColumn = -1;
		Map<Board, Integer> successors = root.getBoard().successors(nextPlayer(root.getCurrentPlayer()));
		Node mostVisitedNode = root.getMostVisitedNode();
		for (Board b : successors.keySet()) // comparar qual dos sucessores tem o mesmo board que o mostVisitedNode
			if (b.equals(mostVisitedNode.getBoard())) {
				bestColumn = successors.get(b).intValue(); // bestColumn está declarada na classe Strategies
				break;
			}
		return bestColumn;
	}
}

class Node {
	private Board board;
	private char currentPlayer; // RED -> 'O' ou YELLOW -> 'X'
	private Node parent;
	private List<Node> childrenList = new ArrayList<>(7);
	private int wins = 0, numberOfVisits = 0;
	
	Node(Board board, char currentPlayer, Node parent) {
		this.board = board;
		this.currentPlayer = currentPlayer;
		this.parent = parent;
	}

	Board getBoard() {return board;}

	char getCurrentPlayer() {return currentPlayer;}

	Node getParent() {return parent;}

	int getNumberOfVisits() {return numberOfVisits;}

	int getWins() {return wins;}

	List<Node> getChildrenList() {return childrenList;}
	
	boolean hasChild() {return childrenList.size()==0 ? false : true;}
	
	Node getAnyChild() {
		int index = new Random().nextInt(childrenList.size()); // indice aleatório entre 0 e childrenList.size()-1  
		return childrenList.get(index);
	}

	void addToChildrenList(Node child) {childrenList.add(child);}

	void incrementStats(int incrWin) { // incrWin e incrNumberOfVisits serão 0 ou 1;
		wins += incrWin; numberOfVisits += 1;
	}

	Node selectBestChild() {
		Node bestChild = null; double maxSoFar = -1;
		for (Node child : childrenList) {
			double uctValue = child.uctValue();
			if (uctValue > maxSoFar) {maxSoFar = uctValue; bestChild = child;}
		}
		return bestChild;
	}

	double uctValue() {
		if (numberOfVisits == 0) return Integer.MAX_VALUE;
		return ((double)wins / (double)numberOfVisits) + 1.41 * Math.sqrt(Math.log(parent.getNumberOfVisits()) / (double)numberOfVisits);
	}

	Node getMostVisitedNode() {
		Node mostVisitedNode = null; int maxSoFar = -1;
		for (Node child : childrenList) {
			int totalVisits = child.getNumberOfVisits();
			if (totalVisits > maxSoFar) {maxSoFar = totalVisits; mostVisitedNode = child;}
		}
		return mostVisitedNode;
	}
}
