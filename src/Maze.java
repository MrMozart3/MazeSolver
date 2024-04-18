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
    private List<List<MazeWindow>> windows = new ArrayList<>();
    void SaveMazeFromFile(String fileName) throws IOException {
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
        while((c = fis.read()) != -1){
            if(c == CHAR_CR || c == CHAR_LF){
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
        if(rawMaze.getLast().equals("")) rawMaze.removeLast();
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
        if(sizeX < 2 || sizeY < 2){
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
                        startY = y / 2;
                        startX = x / 2;
                    }else if(tempChar == 'K'){
                        endY = y / 2;
                        endX = x / 2;
                    }
                }
                tempWindow.setDistance(-1);
                tempList.add(tempWindow);
            }
            windows.add(tempList);
        }
    }

}
