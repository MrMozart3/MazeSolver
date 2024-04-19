import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Maze maze = new Maze();
        maze.SaveMazeFromFile("mazes/maze_1024.txt");
        maze.FillMazeWithDistances();
        MazeWindow win = maze.getWindow(0, 0);
        System.out.println(win);
    }
}
