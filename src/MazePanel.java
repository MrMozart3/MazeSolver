import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MazePanel extends JPanel{
    private Maze maze;
    private MainFrame mainFrame;
    private int cellSize = 10;
    ArrayList<ArrayList<MazeWindowPanel>> windows = new ArrayList<>();
    ArrayList<JPanel> rows = new ArrayList<>();
    ArrayList<Boolean> progress = new ArrayList<>();
    MazePanel(MainFrame mainFrame){
        this.maze = new Maze();
        this.mainFrame = mainFrame;
        this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }
    public void LoadMazeFromTextFile(String filename){
        double start = (double)System.nanoTime();
        maze.SaveMazeFromTextFile(filename);
        if(maze.loaded) {
            System.out.println("maze to object:" + ((System.nanoTime() - start) / 1000000000));
            start = (double)System.nanoTime();
            RenderMaze();
            System.out.println("rendering maze:" + ((System.nanoTime() - start) / 1000000000));
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
        this.setLayout(new GridLayout(maze.getSizeY(), 1));
        rows.clear();
        progress.clear();
        if(!maze.loaded){
            MainFrame.mesLabel.CustomError("CANNOT RENDER MAZE WHEN ITS NOT LOADED");
            return;
        }
        for(int y = 0; y < maze.getSizeY(); y++)
        {
            rows.add(new JPanel(new GridLayout(1, maze.getSizeX())));
            windows.add(new ArrayList<MazeWindowPanel>());
            progress.add(false);
            FillRowWorker w = new FillRowWorker(y);
            w.execute();
        }
        for(int y = 0; y < maze.getSizeY(); y++)
        {
            if(!progress.get(y)){
                y = 0;
                continue;
            }
        }
        for(int y = 0; y < maze.getSizeY(); y++)
        {
            this.add(rows.get(y));
        }
        revalidate();
        repaint();
    }
    public void WindowCLicked(int y, int x)
    {
        if(mainFrame.getCurrentlyPressedNavbar() == 1){
            ClearAnswers();
            maze.setStart(x, y);
            mainFrame.unclickNavbarStartEndButtons();
            if(maze.getStartX() != -1 && maze.getStartY() != -1)
                windows.get(maze.getStartY()).get(maze.getStartX()).ChangeBackground(WindowColor.START);
            if(maze.getEndX() != -1 && maze.getEndY() != -1)
                windows.get(maze.getEndY()).get(maze.getEndX()).ChangeBackground(WindowColor.END);
            revalidate();
            repaint();
        }
        else if(mainFrame.getCurrentlyPressedNavbar() == 2){
            ClearAnswers();
            maze.setEnd(x, y);
            mainFrame.unclickNavbarStartEndButtons();
            if(maze.getStartX() != -1 && maze.getStartY() != -1)
                windows.get(maze.getStartY()).get(maze.getStartX()).ChangeBackground(WindowColor.START);
            if(maze.getEndX() != -1 && maze.getEndY() != -1)
                windows.get(maze.getEndY()).get(maze.getEndX()).ChangeBackground(WindowColor.END);
            revalidate();
            repaint();
        }


        System.out.println("y:" + y + " x:" + x +" dis:" + maze.getWindow(x, y).getDistance() + " ans:" + maze.getWindow(x, y).getAnswer());
    }
    private class FillRowWorker extends SwingWorker<String, Object>
    {
        int y;
        FillRowWorker(int y){
            this.y = y;
        }
        @Override
        public String doInBackground(){
            JPanel row = rows.get(y);
            for(int x = 0; x < maze.getSizeX(); x++)
            {
                MazeWindowPanel t = new MazeWindowPanel(MazePanel.this, MazePanel.this.cellSize, MazePanel.this.cellSize, this.y, x, 1);
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
                windows.get(y).add(x, t);
                row.add(t);
            }
            progress.set(y, true);
            return "test";
        }
    }
    public void Solve()
    {
        double start = (double)System.nanoTime();

        if(maze.getStartX() == -1 || maze.getStartY() == -1 || maze.getEndX() == -1 && maze.getEndY() == -1){
            MainFrame.mesLabel.CustomError("Brak poczatku lub / i konca labiryntu");
            return;
        }
        if(maze.Solve()){
            RenderAnswers();
            MainFrame.mesLabel.CustomSuccess("Labirynt rozwiazano poprawnie");
        }


        System.out.println("maze to object:" + ((System.nanoTime() - start) / 1000000000));
    }
    private void RenderAnswers()
    {
        int[] modY = {-1, 1, 0, 0};
        int[] modX = {0, 0, -1, 1};

        for(int y = 0; y < maze.getSizeY(); y++)
        {
            for(int x = 0; x < maze.getSizeX(); x++)
            {
                for(int i = 0; i < 4; i++)
                {
                    if(maze.getWindow(x, y).getAnswer() && !maze.getWindow(x, y).getWall(i) && maze.getWindow(x + modX[i], y + modY[i]).getAnswer()){
                        windows.get(y).get(x).ChangeBorder(i, BorderColor.ANSWER);
                    }
                    if(maze.getWindow(x, y).getAnswer()){
                        if(x != maze.getStartX() || y != maze.getStartY()){
                            if(x != maze.getEndX() || y != maze.getEndY()){
                                windows.get(y).get(x).ChangeBackground(WindowColor.PATH);
                            }
                        }
                    }
                }
            }
        }
        revalidate();
        repaint();
    }
    private void ClearAnswers()
    {
        for(int y = 0; y < maze.getSizeY(); y++)
        {
            for(int x = 0; x < maze.getSizeX(); x++)
            {
                maze.getWindow(x, y).setAnswer(false);
                maze.getWindow(x, y).setDistance(Integer.MAX_VALUE);
                windows.get(y).get(x).ChangeBackground(WindowColor.EMPTY);
                for(int i = 0; i < 4; i++)
                {
                    if(windows.get(y).get(x).isAnswerBorder(i)){
                        windows.get(y).get(x).ChangeBorder(i, BorderColor.NONE);
                    }
                }
            }
        }
    }
}
