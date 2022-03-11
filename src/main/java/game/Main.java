package game;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        NumberGame ng = new NumberGame(4,4);
        ng.addTile(1,0,2);
        ng.addTile(2,0, 2);
        ng.addTile(0,1,4);
        ng.addTile(1,1,2);
        ng.addTile(2,1,2);
        ng.addTile(2,3,4);
        Scanner sc = new Scanner(System.in);
        System.out.println("Starting...");
        while (ng.canMove()) {
            System.out.println("\nScore: "+ng.getPoints());
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    System.out.print(ng.get(j,i)+" ");
                }
                System.out.print(" | "+i+"\n");
            }
            System.out.println("_________|");
            System.out.println("0 1 2 3");
            Direction[] dir = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
            List<Move> moveList = ng.move(dir[sc.nextInt()]);
            for (Move m : moveList) {
                System.out.println(m.toString());
            }
            //if (!ng.isFull()) ng.addRandomTile();
            //sc.nextLine();
            System.out.println();
        }
        System.out.println("Cant move");
        sc.close();
    }
}
