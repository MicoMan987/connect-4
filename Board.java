import java.util.*;

class Pair{
   int x, y;

   Pair(int x,int y){
      this.x=x;
      this.y=y;
   }
}

public class Board {
   final char EMPTY = '.';
   final static char YELLOW = 'X';
   final static char RED = 'O';
   private boolean thereIsAWinner = false;
   private char winner;
   private char[][] board;

   Board() {
      board = new char[6][7];
      for(char[] row : board) Arrays.fill(row, '.');
   }

   Board(Board b) {
      board = new char[6][7];
      for(int i = 0; i < board.length; i++) // copiar o tabuleiro
         this.board[i] = Arrays.copyOf(b.getBoard()[i], board[0].length);
   }
   
   char[][] getBoard() {return board;}

   // coloca uma peça numa coluna; devolve o true se teve êxito, false caso contrario
   boolean put(int colNumber, char token) {
      for (int i = board.length-1; i >= 0; i--)
         if (board[i][colNumber] == EMPTY) {
            board[i][colNumber] = token;
            return true;
         }
      return false;
   }

   // retorna true se o tabuleiro estiver cheio ou false caso contrario
   boolean isFull(){
      for (int i = 0; i<board[0].length; i++)
         if (board[0][i] == EMPTY) return false;
      return true;
   }

   boolean thereIsAWinner() {return thereIsAWinner;}

   char getWinner() {return winner;}

   boolean isColumnFull(int colNumber) {return board[0][colNumber] != EMPTY;}

   Map<Board, Integer> successors(char token) {
      Map<Board, Integer> boards = new HashMap<>(7); // 7 = capacidade inicial
      for (int colNumber = 0; colNumber<board[0].length; colNumber++) {
         Board newBoard = new Board(this);
         if (newBoard.put(colNumber, token)) 
            boards.put(newBoard, colNumber); // key, value
      }
      return boards;
   }
   // escolhe a melhor jogada usando um dos 3 algoritmos e faz essa jogada 
   int computerTurn(int algorithm, char token) {
      int colNumber;
      if (algorithm == 1) {
         //guarda semore 7^6 -1 nós
         colNumber = Strategies.minimax(5, this);
         put(colNumber, token);
      }
      else if (algorithm == 2) {
         //guarda no máximo 7^6 nós ao mesmo tempo
         colNumber = Strategies.alpha_beta(5, this);
         put(colNumber, token);       
      }
      else {
         //guarda 
         colNumber = Strategies.mcts(this);
         put(colNumber, token);
      }
      return colNumber;
   }

   void check() {
      int result = evaluate();
      if (result != 512 && result != -512) return;
      thereIsAWinner = true;
      if (result == 512) winner = 'X';
      else winner = 'O';
   }

   int evaluate() { //     vertical     horizontal     diagonalFrente    diagonalTras
      Pair[] increments ={new Pair(0,1), new Pair(1,0), new Pair(1,1), new Pair(-1,1)};
      int eval = 0;
      for (int y = 0; y<board.length; y++) // linhas
         for (int x = 0; x<board[0].length; x++) { // colunas
            boolean vertical = y+3 < board.length;
            boolean horizontal = x+3 < board[0].length;
            boolean diagonalFrente = vertical && horizontal;
            boolean diagonalTras = vertical && x-3 >= 0;
            boolean[] dirs = {vertical, horizontal, diagonalFrente, diagonalTras};

            for(int dir = 0; dir<4; dir++)
               if(dirs[dir]) { // se for possivel...então avalia
                  eval = evaluate(y, x, eval, increments[dir]);
                  if(eval == 512 || eval == -512) return eval;
               }
         }
      return eval;
   }

   int evaluate(int y, int x, int eval, Pair increment) {
      int counto = 0, countx = 0;
      for (int yy = y, xx = x, k = 0; k<4; k++, yy+=increment.y, xx+=increment.x) {
         if(board[yy][xx] == YELLOW)
            countx++;
         else if(board[yy][xx] == RED)
            counto++;
      }
      if(countx == 0) {
         if(counto == 1)    eval-=1;
         else if(counto==2) eval-=10;
         else if(counto==3) eval-=50;
         else if(counto==4) return -512;
      }
      else if(counto == 0) {
        if(countx == 1)    eval+=1;
        else if(countx==2) eval+=10;
        else if(countx==3) eval+=50;
        else if(countx==4) return 512;
      }
      return eval;
   }

   // Representacao em String do tabuleiro
   public void printBoard() {
      for (int i=0; i < board.length; i++) {
         String str = String.valueOf(board[i]);
         System.out.println(str.replace("", " ").trim());
      }
   }

   // comparar dois boards, verificando apenas se tem o mesmo tabuleiro
   public boolean equals(Board b) {
      if (b==null) return false;
      for(int i = 0; i < this.board.length; i++)
         for (int j = 0; j<board[0].length; j++)
            if (!(this.board[i][j]==b.getBoard()[i][j])) return false;
      return true;
   } 
}
