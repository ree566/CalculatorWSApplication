/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Wei.Cheng
 */
public class Hanoi {

    static class Move {

        char from, to;

        Move(char from, char to) {
            this.from = from;
            this.to = to;
        }
    }

    List<Move> solve(int n) {
        moves = new ArrayList();
        move(n, 'A', 'B', 'C');
        return moves;
    }

    private List<Move> moves;

    private void move(int n, char a, char b, char c) {
        if (n == 1) {
            moves.add(new Move(a, c));
        } else {
            move(n - 1, a, c, b);
            move(1, a, b, c);
            move(n - 1, b, a, c);
        }
    }

    public static void main(String args[]) {
        out.print("Please insert the plate count:");
        Hanoi hanoi = new Hanoi();
        int n = new Scanner(System.in).nextInt();
        for (Move move : hanoi.solve(n)) {
            out.printf("Plate from %c moveTo %c%n", move.from, move.to);
        }
    }
}
