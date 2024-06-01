import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class MazeLabel extends JLabel{
    private Maze maze;
    private MainFrame mainFrame;
    private int cellSize = 10;
    ZoomClass zoom;
    MazeLabel(MainFrame mainFrame){
        this.maze = new Maze();
        this.mainFrame = mainFrame;
        this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int y = e.getY() / (maze.getWallSize() + maze.getPathSize());
                int x = e.getX() / (maze.getWallSize() + maze.getPathSize());
                if(e.getY() % (maze.getWallSize() + maze.getPathSize()) < maze.getWallSize() && e.getX() % (maze.getWallSize() + maze.getPathSize()) < maze.getWallSize()){
                    return;
                }
                if(y >= maze.getSizeY() || x >= maze.getSizeX()){
                    return;
                }
                if(y < 0 || x < 0){
                    return;
                }

                WindowCLicked(y, x);
                System.out.println(x + " " + y + " | " + e.getX() % (maze.getWallSize() + maze.getPathSize()) + " " + e.getY() % (maze.getWallSize() + maze.getPathSize()));
            }
        });
        this.setBackground(Color.BLUE);
    }
    public void LoadMazeFromBinaryFile(String fileName, int binaryCounter){
        if(!maze.ConvertBinaryFileToTextFile(fileName, "tempmaze" + binaryCounter + ".txt")){
            MainFrame.mesLabel.CustomError("ERROR WHILE LOADING MAZE");
            return;
        }
        LoadMazeFromTextFile("tempmaze" + binaryCounter + ".txt", true);
    }
    public void LoadMazeFromTextFile(String filename, boolean binary){
        double start = (double)System.nanoTime();
        maze.SaveMazeFromTextFile(filename, binary);
        double end = (double)System.nanoTime();
        System.out.println("maze to object:" + ((end - start) / 1000000000));
        if(maze.loaded) {
            int field = maze.getSizeX() * maze.getSizeY();
            int maxZoom;
            int startZoom;
            int zoomScale;
            if(field < 1000){
                maxZoom = 4;
                startZoom = 4;
                zoomScale = 5;
            }
            else if(field < 10000){
                maxZoom = 4;
                startZoom = 3;
                zoomScale = 5;
            }
            else if(field <= 50000){
                maxZoom = 3;
                startZoom = 2;
                zoomScale = 4;
            }
            else{
                maxZoom = 2;
                startZoom = 1;
                zoomScale = 3;
            }
            this.zoom = new ZoomClass(zoomScale, startZoom, maxZoom);
            if(zoom.isZoomableIn()){
                mainFrame.zoomInEnabled(true);
            }
            else{
                mainFrame.zoomInEnabled(false);
            }
            if(zoom.isZoomableOut()){
                mainFrame.zoomOutEnabled(true);
            }
            else{
                mainFrame.zoomOutEnabled(false);
            }

            RenderMaze(zoom.getWallSize(), zoom.getPathSize());
            MainFrame.mesLabel.CustomSuccess("LOADED MAZE SUCCESSFULLY");
            mainFrame.setNavbarStartEndButtonsEnabled(true);
            mainFrame.setSolveButtonEnabled(true);
            mainFrame.EnableSaveToPNG(true);
            mainFrame.EnableSaveToText(true);
            if(binary){
                mainFrame.EnableSaveToBinary(true);
            }
        }
        else {
            MainFrame.mesLabel.CustomError("ERROR WHILE LOADING MAZE");
            mainFrame.setNavbarStartEndButtonsEnabled(false);
            mainFrame.setSolveButtonEnabled(false);
            mainFrame.DisableAllButtonsExceptFileInput();
        }
    }
    public void RenderMaze(int wallSize, int pathSize)
    {
        double start2 = (double)System.nanoTime();
        BufferedImage image = maze.GenerateImage(wallSize, pathSize);
        this.setIcon(new ImageIcon(image));
        double end2 = (double)System.nanoTime();
        System.out.println("maze rendered:" + ((end2 - start2) / 1000000000));
        image = null;
    }
    public void WindowCLicked(int y, int x)
    {
        if(mainFrame.getCurrentlyPressedNavbar() == 1){
            ClearAnswers();
            maze.setStart(x, y);
            mainFrame.unclickNavbarStartEndButtons();
            RenderMaze(maze.getWallSize(), maze.getPathSize());
            revalidate();
            repaint();
        }
        else if(mainFrame.getCurrentlyPressedNavbar() == 2){
            ClearAnswers();
            maze.setEnd(x, y);
            mainFrame.unclickNavbarStartEndButtons();
            RenderMaze(maze.getWallSize(), maze.getPathSize());
            revalidate();
            repaint();
        }


        System.out.println("y:" + y + " x:" + x +" dis:" + maze.getWindow(x, y).getDistance() + " ans:" + maze.getWindow(x, y).getAnswer());
    }
    public void Solve()
    {
        double start = (double)System.nanoTime();

        if(maze.getStartX() == -1 || maze.getStartY() == -1 || maze.getEndX() == -1 && maze.getEndY() == -1){
            MainFrame.mesLabel.CustomError("Brak poczatku lub / i konca labiryntu");
            return;
        }
        if(maze.Solve()){
            RenderMaze(maze.getWallSize(), maze.getPathSize());
            MainFrame.mesLabel.CustomSuccess("Labirynt rozwiazano poprawnie");
        }


        System.out.println("maze to object:" + ((System.nanoTime() - start) / 1000000000));
    }
    private void ClearAnswers()
    {
        for(int y = 0; y < maze.getSizeY(); y++)
        {
            for(int x = 0; x < maze.getSizeX(); x++)
            {
                maze.getWindow(x, y).setAnswer(false);
                maze.getWindow(x, y).setDistance(Integer.MAX_VALUE);
            }
        }
    }
    public void ZoomIn()
    {
        if(!maze.loaded){
            mainFrame.zoomInEnabled(false);
            mainFrame.zoomOutEnabled(false);
        }

        if(zoom.isZoomableIn()){
            zoom.zoomIn();
            RenderMaze(zoom.getWallSize(), zoom.getPathSize());
            if(zoom.isZoomableIn()){
                mainFrame.zoomInEnabled(true);
            }
            else{
                mainFrame.zoomInEnabled(false);
            }
            mainFrame.zoomOutEnabled(true);
        }
    }
    public void ZoomOut()
    {
        if(!maze.loaded){
            mainFrame.zoomInEnabled(false);
            mainFrame.zoomOutEnabled(false);
        }

        if(zoom.isZoomableOut()){
            zoom.zoomOut();
            RenderMaze(zoom.getWallSize(), zoom.getPathSize());
            if(zoom.isZoomableOut()){
                mainFrame.zoomOutEnabled(true);
            }
            else{
                mainFrame.zoomOutEnabled(false);
            }
            mainFrame.zoomInEnabled(true);
        }
    }
    public void GeneratePNG(String filename)
    {
        maze.GeneratePNG(filename, zoom.getWallSize(), zoom.getPathSize());
    }
    public void SaveMazeToTextFile(String filename)
    {
        maze.SaveAnswerToTextFile(filename);
    }
    public void SaveMazeToBinaryFile(String filename)
    {
        maze.SaveAnswerToBinaryFile(filename);
    }
}
