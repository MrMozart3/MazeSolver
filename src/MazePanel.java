import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private Maze maze;
    private MainFrame mainFrame;
    MazePanel(MainFrame mainFrame){
        this.maze = new Maze();
        this.mainFrame = mainFrame;
        this.setLayout(new GridLayout(1, 1));
    }
    public void LoadMazeFromTextFile(String filename){
        maze.SaveMazeFromTextFile(filename);
        if(maze.loaded) {
            RenderMaze();
            MainFrame.mesLabel.CustomSuccess("LOADED MAZE SUCCESSFULLY");
            mainFrame.setNavbarStartEndButtonsEnabled(true);
        }
        else {
            MainFrame.mesLabel.CustomError("ERROR WHILE LOADING MAZE");
            mainFrame.setNavbarStartEndButtonsEnabled(false);
        }

    }
    public void RenderMaze(){
        this.removeAll();
        if(!maze.loaded){
            MainFrame.mesLabel.CustomError("CANNOT RENDER MAZE WHEN ITS NOT LOADED");
            return;
        }
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
        revalidate();
        repaint();
    }
    public void WindowCLicked(int y, int x)
    {
        if(mainFrame.getCurrentlyPressedNavbar() == 1){
            maze.setStart(x, y);
            mainFrame.unclickNavbarStartEndButtons();
            RenderMaze();
        }
        else if(mainFrame.getCurrentlyPressedNavbar() == 2){
            maze.setEnd(x, y);
            mainFrame.unclickNavbarStartEndButtons();
            RenderMaze();
        }
        System.out.println("y:" + y + " x:" + x +" cpn:" + mainFrame.getCurrentlyPressedNavbar());
    }
}
