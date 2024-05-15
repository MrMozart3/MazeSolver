import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Maze {
    //statics
    public static int CHAR_CR = 13;
    public static int CHAR_LF = 10;
    private int sizeX, sizeY;

    private int startY, startX, endY, endX;

    boolean loaded, solved;
    private List<List<MazeWindow>> windows = new ArrayList<>();
    //constructors
    Maze(){
        sizeX = -1;
        sizeY = -1;
        startY = -1;
        startX = -1;
        endY = -1;
        endX = -1;
        loaded = false;
    }

    //encapsulation
    public int getSizeY(){ return this.sizeY; }
    public int getSizeX(){ return this.sizeX; }


    public int getStartY(){ return this.startY; }
    public int getStartX(){ return this.startX; }
    public void setStart(int startX, int startY){
        if(startX < 0 || startX >= this.sizeX || startY < 0 || startY >= this.sizeY){
            MainFrame.mesLabel.CustomError("START OUT OF BOUNDS");
            return;
        }
        if(startX == this.endX && startY == this.endY){
            MainFrame.mesLabel.CustomError("CANNOT SET START POSITION TO END POSITION");
            return;
        }
        this.startX = startX;
        this.startY = startY;
    }
    public int getEndY(){ return this.endY; }
    public int getEndX(){ return this.endX; }
    public void setEnd(int endX, int endY){
        if(endX < 0 || endX >= this.sizeX || endY < 0 || endY >= this.sizeY){
            MainFrame.mesLabel.CustomError("END OUT OF BOUNDS");
            return;
        }
        if(endX == this.startX && endY == this.startY){
            MainFrame.mesLabel.CustomError("CANNOT SET END POSITION TO START POSITION");
            return;
        }
        this.endX = endX;
        this.endY = endY;
    }

    //methods
    void SaveMazeFromTextFile(String fileName)  {
        this.loaded = false;
        FileInputStream fis = null;
        List<String> rawMaze = new ArrayList<>();
        try{
            fis = new FileInputStream(fileName);
        } catch (Exception e){
            System.out.println("Problem z otwarciem pliku" + fileName);
            return;
        }
        //saving to rawMaze
        int c, tempY = 0;
        boolean isUsedLine = false;
        rawMaze.add("");
        while(true){
            try {
                if ((c = fis.read()) == -1) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(c == Maze.CHAR_CR || c == Maze.CHAR_LF){
                if(isUsedLine){
                    rawMaze.add("");
                    tempY++;
                    isUsedLine = false;
                }
            }
            else {
                isUsedLine = true;
                rawMaze.set(tempY, rawMaze.get(tempY) + (char)c);
            }
        }
        //removing last
        if(rawMaze.getLast().isEmpty()) rawMaze.removeLast();
        //checking if not odd
        if(rawMaze.getFirst().length() % 2 == 0 || rawMaze.size() % 2 == 0){
            System.out.println("Blad w pliku z danymi |1|");
            return;
        }
        //verifying lines lengths
        for(int y = 1; y < rawMaze.size(); y++)
        {
            if(rawMaze.get(y).length() != rawMaze.getFirst().length()){
                System.out.println("Blad w pliku z danymi |2|");
                return;
            }
        }
        //checking border (top, bottom)
        for(int x = 0; x < rawMaze.getFirst().length(); x++)
        {
            //top
            char tempChar1 = rawMaze.getFirst().charAt(x);
            if(tempChar1 != 'X' && tempChar1 != 'P' && tempChar1 != 'K'){
                System.out.println("Blad w pliku z danymi |3|");
                return;
            }
            //bottom
            char tempChar2 = rawMaze.getLast().charAt(x);
            if(tempChar2 != 'X' && tempChar2 != 'P' && tempChar2 != 'K'){
                System.out.println("Blad w pliku z danymi |4|");
                return;
            }
        }
        //checking border (left, right)
        for(int y = 0; y < rawMaze.size(); y++)
        {
            char tempChar1 = rawMaze.get(y).charAt(0);
            if(tempChar1 != 'X' && tempChar1 != 'P' && tempChar1 != 'K'){
                System.out.println("Blad w pliku z danymi |5|");
                return;
            }
            char tempChar2 = rawMaze.get(y).charAt(rawMaze.get(y).length() - 1);
            if(tempChar2 != 'X' && tempChar2 != 'P' && tempChar2 != 'K'){
                System.out.println("Blad w pliku z danymi |6|");
                return;
            }
        }
        //checking corners
        for(int y = 1; y < rawMaze.size(); y+=2)
        {
            for(int x = 1; x < rawMaze.getFirst().length(); x+= 2)
            {
                int[] modY = {-1, -1, 1, 1};
                int[] modX = {-1, 1, -1, 1};
                for(int i = 0; i < 4; i++) {
                    char tempChar = rawMaze.get(y + modY[i]).charAt(x + modX[i]);
                    if(tempChar != 'X'){
                        System.out.println("Blad w pliku z danymi |7|");
                    }
                }
            }
        }
        //sizeX and sizeY
        this.sizeX = rawMaze.getFirst().length() / 2;
        this.sizeY = rawMaze.size() / 2;
        if(this.sizeX < 2 || this.sizeY < 2){
            System.out.println("Zbyt maly Labirynt");
            return;
        }
        //making Maze
        for(int y = 1; y < rawMaze.size(); y+=2)
        {
            ArrayList<MazeWindow> tempList = new ArrayList<>();
            for(int x = 1; x < rawMaze.getFirst().length(); x+=2)
            {
                int[] modY = {-1, 1, 0, 0};
                int[] modX = {0, 0, -1, 1};

                MazeWindow tempWindow = new MazeWindow();
                for(int i = 0; i < 4; i++){
                    char tempChar = rawMaze.get(y + modY[i]).charAt(x + modX[i]);
                    tempWindow.setWall(i,tempChar != ' ');
                    //start | end
                    if(tempChar == 'P'){
                        this.startY = y / 2;
                        this.startX = x / 2;
                    }else if(tempChar == 'K'){
                        this.endY = y / 2;
                        this.endX = x / 2;
                    }
                }
                tempWindow.setDistance(Integer.MAX_VALUE);
                tempList.add(tempWindow);
            }
            windows.add(tempList);
        }
        //loaded finished successfully
        this.loaded = true;
    }
    void FillMazeWithDistances(){
        if(!loaded){
            System.out.println("Labirynt niezaladowany");
            return;
        }
        int y = this.endY, x = this.endX;
        int distance = 1;

        int[] modY = {-1, 1, 0, 0};
        int[] modX = {0, 0, -1, 1};

        boolean repeatLoop = true;

        while(true)
        {
            repeatLoop = false;
            //updating distance
            if(windows.get(y).get(x).getDistance() > distance) windows.get(y).get(x).setDistance(distance);
            //checking for higher values
            for(int i = 0; i <4; i++){
                if(y + modY[i] >= 0 && y + modY[i] < this.sizeY && x + modX[i] >= 0 && x + modX[i] < this.sizeX) {
                    if (!windows.get(y).get(x).getWall(i) && windows.get(y + modY[i]).get(x + modX[i]).getDistance() > distance + 1) {
                        y += modY[i];
                        x += modX[i];
                        distance++;
                        repeatLoop = true;
                        break;
                    }
                }
            }
            if(repeatLoop) continue;
            //checking for lower values
            for(int i = 0; i <4; i++) {
                if(y + modY[i] >= 0 && y + modY[i] < this.sizeY && x + modX[i] >= 0 && x + modX[i] < this.sizeX){
                    if (!windows.get(y).get(x).getWall(i) && windows.get(y + modY[i]).get(x + modX[i]).getDistance() == distance - 1) {
                        y += modY[i];
                        x += modX[i];
                        distance--;
                        repeatLoop = true;
                        break;
                    }
                }
            }
            if(repeatLoop) continue;
            //if value 1(break loop)
            break;
        }
    }
    MazeWindow getWindow(int x, int y)
    {
        if(x >= 0 && x < sizeX && y >= 0 && y < sizeY)
        {
            return windows.get(y).get(x);
        }
        return null;
    }

}
