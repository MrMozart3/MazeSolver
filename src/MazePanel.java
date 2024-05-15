import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private Maze maze;
    MazePanel(){
        this.maze = new Maze();
        this.setLayout(new GridLayout(1, 1));
    }
    public void LoadMazeFromTextFile(String filename){
        maze.SaveMazeFromTextFile(filename);
        if(maze.loaded) {
            this.removeAll();
            this.setLayout(new GridLayout(maze.getSizeY(), maze.getSizeX()));
            for(int y = 0; y < maze.getSizeY(); y++)
            {
                for(int x = 0; x < maze.getSizeX(); x++)
                {
                    MazeWindowPanel t = new MazeWindowPanel(this, 10, 10, y, x, 1);
                    for(int i = 0; i < 4; i++) {
                        if (maze.getWindow(x, y).getWall(i)) {
                            t.ChangeBorder(i, BorderColor.WALL);
                        }else t.ChangeBorder(i, BorderColor.NONE);
                    }
                    if(y == maze.getStartY() && x == maze.getStartX()){
                        t.ChangeBackground(WindowColor.START);
                    }
                    if(y == maze.getEndY() && x == maze.getEndX()){
                        t.ChangeBackground(WindowColor.END);
                    }
                    this.add(t);
                }
            }
            MainFrame.mesLabel.CustomSuccess("LOADED MAZE SUCCESSFULLY");
        }
        else MainFrame.mesLabel.CustomError("ERROR WHILE LOADING MAZE");

    }
}
