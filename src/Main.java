import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Maze maze = new Maze();
        maze.SaveMazeFromFile("mazes/maze_10.txt");
    }
}
