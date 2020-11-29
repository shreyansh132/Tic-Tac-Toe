package com.rgame.tictactoe.Tools;

public class Move {
    private final char player = 'x',opponent = 'o';

    public Move() {}

    private boolean isMovesLeft(char board[][]) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '_') {
                    return true;
                }
            }
        }
        return false;
    }

    public int findBestMove(char board[][]) {
        int bestVal = -1000;
        int bestMove[] = new int[2];
        bestMove[0] = -1;
        bestMove[1] = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '_') {
                    board[i][j] = player;
                    int moveVal = minimax(board,0,false);
                    board[i][j] = '_';
                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }

        if (bestMove[0] == 0) {
            if (bestMove[1] == 0) {
                return 1;
            }else if (bestMove[1] == 1) {
                return 2;
            }else {
                return 3;
            }
        }else if (bestMove[0] == 1) {
            if (bestMove[1] == 0) {
                return 4;
            }else if (bestMove[1] == 1) {
                return 5;
            }else {
                return 6;
            }
        }else {
            if (bestMove[1] == 0) {
                return 7;
            }else if (bestMove[1] == 1) {
                return 8;
            }else {
                return 9;
            }
        }
    }

    private int minimax(char board[][], int depth, Boolean isMax) {
        int score = evaluate(board);
        if (score == 10)
            return score;
        if (score == -10)
            return score;
        if (isMovesLeft(board) == false)
            return 0;
        if (isMax) {
            int best = -1000;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j]=='_') {
                        board[i][j] = player;
                        best = Math.max(best, minimax(board, depth + 1, !isMax));
                        board[i][j] = '_';
                    }
                }
            }
            return best;
        } else {
            int best = 1000;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '_') {
                        board[i][j] = opponent;
                        best = Math.min(best, minimax(board,depth + 1, !isMax));
                        board[i][j] = '_';
                    }
                }
            }
            return best;
        }
    }

    private int evaluate(char b[][]) {
        for (int row = 0; row < 3; row++) {
            if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                if (b[row][0] == player)
                    return +10;
                else if (b[row][0] == opponent)
                    return -10;
            }
        }

        for (int col = 0; col < 3; col++) {
            if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                if (b[0][col] == player)
                    return +10;

                else if (b[0][col] == opponent)
                    return -10;
            }
        }

        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == player)
                return +10;
            else if (b[0][0] == opponent)
                return -10;
        }

        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == player)
                return +10;
            else if (b[0][2] == opponent)
                return -10;
        }

        return 0;
    }


}
