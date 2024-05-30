

import javax.imageio.ImageIO;
import javax.management.InstanceAlreadyExistsException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class Maze {
    //statics
    public static int CHAR_CR = 13;
    public static int CHAR_LF = 10;
    private int sizeX, sizeY;

    private int startY, startX, endY, endX;

    private int wallSize, pathSize;

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
        solved = false;
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

    public int getWallSize(){
        return this.wallSize;
    }
    public int getPathSize(){
        return this.pathSize;
    }

    //methods
    int GetValueFromDataInputStream(DataInputStream stream, int bytes)
    {
        try {
            byte[] buffer = new byte[32];
            for (int i = bytes - 1; i >=0; i--) {
                buffer[i] = stream.readByte();
            }

            int value = 0;
            for (int i = 0; i < bytes; i++) {
                value |= (buffer[i] & 0xFF) << (8 * (bytes - 1 - i));
            }
            return value;
        }
        catch (IOException e) {
            return -1;
        }
    }
    boolean ConvertBinaryFileToTextFile(String fileName, String tempFileName)
    {
        FileInputStream fileInputStream = null;
        FileWriter fileOutputStream = null;
        DataInputStream dataInputStream = null;
        boolean isGood = true;
        int columns, lines;
        int tempStartX, tempStartY, tempEndX, tempEndY;
        char Separator, Wall, Path;
        int Counter;
        //delete file

        try{
            fileInputStream = new FileInputStream(fileName);
            fileOutputStream = new FileWriter(tempFileName);
            dataInputStream = new DataInputStream(fileInputStream);

            //header
            int temp = GetValueFromDataInputStream(dataInputStream, 4);
            if(temp != 0x52524243){
                isGood = false;
            }
            if(GetValueFromDataInputStream(dataInputStream, 1) != 0x1B){
                isGood = false;
            }
            columns = GetValueFromDataInputStream(dataInputStream, 2);
            lines  = GetValueFromDataInputStream(dataInputStream, 2);
            tempStartX = GetValueFromDataInputStream(dataInputStream, 2);
            tempStartY = GetValueFromDataInputStream(dataInputStream, 2);
            tempEndX = GetValueFromDataInputStream(dataInputStream, 2);
            tempEndY = GetValueFromDataInputStream(dataInputStream, 2);
            GetValueFromDataInputStream(dataInputStream, 12);
            Counter = GetValueFromDataInputStream(dataInputStream, 4);
            GetValueFromDataInputStream(dataInputStream, 4);
            Separator = (char)GetValueFromDataInputStream(dataInputStream, 1);
            Wall = (char)GetValueFromDataInputStream(dataInputStream, 1);
            Path = (char)GetValueFromDataInputStream(dataInputStream, 1);
            //counter
            this.sizeX = (columns - 1) / 2;
            this.sizeY = (lines - 1) / 2;
            this.startX = tempStartX == columns ? (tempStartX - 2) / 2 : (tempStartX - 1) / 2;
            this.startY = tempStartY == lines ? (tempStartY - 2) / 2 : (tempStartY - 1) / 2;
            this.endX = tempEndX == columns ? (tempEndX - 2) / 2 : (tempEndX - 1) / 2;
            this.endY = tempEndY == lines ? (tempEndY - 2) / 2 : (tempEndY - 1) / 2;

            String line = "";
            int lineNumber = 0;
            for(int i = 0; i < Counter; i++) {
                char s = (char) GetValueFromDataInputStream(dataInputStream, 1);
                char v = (char) GetValueFromDataInputStream(dataInputStream, 1);
                int l = GetValueFromDataInputStream(dataInputStream, 1) + 1;
                if (s != Separator) {
                    isGood = false;
                }

                if (v == Wall) {
                    for (int j = 0; j < l; j++) line += 'X';
                } else if (v == Path) {
                    for (int j = 0; j < l; j++) {
                        if (line.length() == 0 || line.length() == lines - 1 || lineNumber == 0 || lineNumber == columns - 1) {
                            line += 'X';
                        } else line += ' ';
                    }
                } else {
                    isGood = false;
                }
                if (line.length() == lines) {
                    line += '\n';
                    fileOutputStream.write(line);
                    line = "";
                    lineNumber++;
                } else if (line.length() > lines) {
                    isGood = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isGood;
    }
    void SaveMazeFromTextFile(String fileName, boolean fromBinary)  {
        windows.clear();
        sizeX = -1;
        sizeY = -1;
        if(!fromBinary) {
            startY = -1;
            startX = -1;
            endY = -1;
            endX = -1;
        }
        solved = false;
        this.loaded = false;

        boolean isGood = true;

        List<String> rawMaze = new ArrayList<>();
        FileChannel channel = null;
        MappedByteBuffer buffer = null;
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(fileName);
            channel = fileStream.getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            Charset charset = StandardCharsets.UTF_8;
            CharsetDecoder decoder = charset.newDecoder();

            // Create a CharBuffer to decode bytes to characters
            CharBuffer charBuffer = decoder.decode(buffer);

            // Read rawMaze from the CharBuffer
            StringBuilder lineBuilder = new StringBuilder();
            for (int i = 0; i < charBuffer.length(); i++) {
                char c = charBuffer.get(i);
                if (c == '\n' || c == '\r') {
                    // Check if the next character is '\n' to handle Windows line endings (\r\n)
                    if (c == '\r' && i + 1 < charBuffer.length() && charBuffer.get(i + 1) == '\n') {
                        // Skip the '\n' character in case of Windows line endings
                        i++;
                    }
                    // End of line, add line to the list if it's not empty
                    if (lineBuilder.length() > 0) {
                        rawMaze.add(lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                    }
                } else {
                    // Append character to the current line
                    lineBuilder.append(c);
                }
            }

            // Add the last line if not empty
            if (lineBuilder.length() > 0) {
                rawMaze.add(lineBuilder.toString());
            }
        } catch (Exception e) {
            isGood = false;
            e.printStackTrace();
        } finally {
            if (buffer != null) {
                try {
                    buffer.force();
                    buffer.clear();
                    buffer = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!isGood) return;
        //removing last
        if(rawMaze.getLast().isEmpty()) rawMaze.removeLast();
        //checking if not odd
        if(rawMaze.getFirst().length() % 2 == 0 || rawMaze.size() % 2 == 0){
            System.out.println("Blad w pliku z danymi |1|");
            return;
        }
        //verifying rawMaze lengths
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
    boolean FillMazeWithDistances(){
        if(!loaded){
            MainFrame.mesLabel.CustomError("Labirynt niezaladowany");
            return false;
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
        return getWindow(getStartX(), getStartY()).getDistance() != Integer.MAX_VALUE;
    }
    boolean Solve(){
        if(!loaded){
            MainFrame.mesLabel.CustomError("Labirynt niezaladowany");
            return false;
        }
        if(!FillMazeWithDistances()){
            MainFrame.mesLabel.CustomInformation("Labirynt nie do rozwiazania");
            return false;
        }
        int y = this.startY, x = this.startX;
        int[] modY = {-1, 1, 0, 0};
        int[] modX = {0, 0, -1, 1};


        while(true){
            boolean foundNext = false;
            MazeWindow curWindow = getWindow(x, y);
            for(int i = 0; i < 4; i++) {
                if (!curWindow.getWall(i)) {
                    if (y + modY[i] >= 0 && y + modY[i] < this.sizeY && x + modX[i] >= 0 && x + modX[i] < this.sizeX) {
                        if (curWindow.getDistance() - 1 == getWindow(x + modX[i], y + modY[i]).getDistance()) {
                            curWindow.setAnswer(true);
                            y += modY[i];
                            x += modX[i];
                            foundNext = true;
                            break;
                        }
                    }
                }
            }
            if(!foundNext){
                if(curWindow.getDistance() == 1){
                    curWindow.setAnswer(true);
                    solved = true;
                    return true;
                }
                else{
                    return false;
                }
            }
        }
    }
    BufferedImage GenerateImage(int wallSize, int pathSize){
        this.wallSize = wallSize;
        this.pathSize = pathSize;
        if(!loaded){
            MainFrame.mesLabel.CustomError("Labirynt niezaladowany");
            return null;
        }
        BufferedImage image = new BufferedImage(sizeY * (wallSize + pathSize ) + wallSize, sizeX * (wallSize + pathSize ) + wallSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        //background to black
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        //drawing
        int[] modY = {-1, 1, 0, 0};
        int[] modX = {0, 0, -1, 1};

        for(int y = 0; y < sizeY; y++)
        {
            for(int x = 0; x < sizeX; x++)
            {
                if(y == startY && x == startX){
                    graphics.setColor(MazeColors.START.color);
                }
                else if(y == endY && x == endX){
                    graphics.setColor(MazeColors.END.color);
                }
                else if(getWindow(x, y).getAnswer()){
                    graphics.setColor(MazeColors.ANSWER.color);
                }
                else{
                    graphics.setColor(MazeColors.EMPTY.color);
                }
                graphics.fillRect(x * (wallSize + pathSize) + wallSize, y * (wallSize + pathSize) + wallSize, pathSize, pathSize);


                for(int i = 0; i < 4; i++)
                {
                    if(!getWindow(x, y).getWall(i)){
                        if(getWindow(x, y).getAnswer() && getWindow(x + modX[i], y + modY[i]).getAnswer()){
                            graphics.setColor(MazeColors.ANSWER.color);
                        }
                        else{
                            graphics.setColor(MazeColors.EMPTY.color);
                        }


                        if(i == 0){
                            graphics.fillRect(x * (wallSize + pathSize) + wallSize, y * (wallSize + pathSize),pathSize, wallSize);
                        }
                        else if(i == 1){
                            graphics.fillRect(x * (wallSize + pathSize) + wallSize, y * (wallSize + pathSize) + pathSize + wallSize, pathSize, wallSize);

                        }
                        else if(i == 2){
                            graphics.fillRect(x * (wallSize + pathSize), y * (wallSize + pathSize) + wallSize, wallSize, pathSize);
                        }
                        else if(i == 3){
                            graphics.fillRect(x * (wallSize + pathSize) + pathSize + wallSize, y * (wallSize + pathSize) + wallSize, wallSize, pathSize);
                        }

                    }
                }
            }
        }
        graphics.dispose();

        return image;
    }
    MazeWindow getWindow(int x, int y)
    {
        if(x >= 0 && x < sizeX && y >= 0 && y < sizeY)
        {
            return windows.get(y).get(x);
        }
        return null;
    }
    public void GeneratePNG(String fileName, int wallSize, int pathSize){
        BufferedImage image = GenerateImage(2, 5);
        if(image != null){
            try {
                ImageIO.write(image, "png", new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void SaveAnswerToTextFile(String fileName){
        if(!loaded){
            MainFrame.mesLabel.CustomError("Labirynt niezaladowany");
            return;
        }
        if(!solved){
            MainFrame.mesLabel.CustomError("Labirynt nierozwiazany");
            return;
        }
        //open file
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            int y = startY, x = startX;
            int modY[] = {-1, 1, 0, 0};
            int modX[] = {0, 0, -1, 1};
            char dirs[] = {'N', 'S', 'W', 'E'};

            while(y != endY || x != endX){
                for(int i = 0; i < 4; i++){
                    if(!windows.get(y).get(x).getWall(i)) {
                        if (windows.get(y).get(x).getDistance() == windows.get(y + modY[i]).get(x + modX[i]).getDistance() + 1) {
                            y += modY[i];
                            x += modX[i];
                            fileWriter.write(dirs[i]);
                            if(y != endY || x != endX) fileWriter.write('\n');
                            break;
                        }
                    }
                }
            }
            MainFrame.mesLabel.CustomSuccess("Rozwiazanie zapisano poprawnie");
        } catch (IOException e) {
            e.printStackTrace();
            MainFrame.mesLabel.CustomError("Blad przy zapisywaniu rozwiazania");
            return;
        } finally {
            if(fileWriter != null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void SaveAnswerToBinaryFile(String fileName){
        if(!loaded){
            MainFrame.mesLabel.CustomError("Labirynt niezaladowany");
            return;
        }
        if(!solved){
            MainFrame.mesLabel.CustomError("Labirynt nierozwiazany");
            return;
        }

        boolean isGoodToWrite = false;
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        int Counter;
        try{
            fileInputStream = new FileInputStream(fileName);
            dataInputStream = new DataInputStream(fileInputStream);

            //header
            int temp = GetValueFromDataInputStream(dataInputStream, 4);
            if(temp != 0x52524243){
                throw new IOException();
            }
            if(GetValueFromDataInputStream(dataInputStream, 1) != 0x1B){
                throw new IOException();
            }
            GetValueFromDataInputStream(dataInputStream, 24);
            Counter = GetValueFromDataInputStream(dataInputStream, 4);
            GetValueFromDataInputStream(dataInputStream, 7);
            for(int i = 0; i < Counter; i++)
            {
                char t = (char)GetValueFromDataInputStream(dataInputStream, 3);
            }
            if(GetValueFromDataInputStream(dataInputStream, 4) != 0x52524243){
                throw new IOException();
            }

            if(GetValueFromDataInputStream(dataInputStream, 1) != -1){
                throw new InstanceAlreadyExistsException();
            }
            isGoodToWrite = true;
        } catch (IOException e) {
            MainFrame.mesLabel.CustomError("Blad przy zapisywaniu rozwiazania");
        } catch (InstanceAlreadyExistsException e){
            MainFrame.mesLabel.CustomError("Blad. Rozwiazanie juz istnieje w pliku");
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!isGoodToWrite) return;

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName, true)) {
            List<Byte> answer = new ArrayList<>();
            int y = startY, x = startX;
            int modY[] = {-1, 1, 0, 0};
            int modX[] = {0, 0, -1, 1};
            char dirs[] = {'N', 'S', 'W', 'E'};
            int moves = -1;

            while(y != endY || x != endX){
                for(int i = 0; i < 4; i++){
                    if(!windows.get(y).get(x).getWall(i)) {
                        if (windows.get(y).get(x).getDistance() == windows.get(y + modY[i]).get(x + modX[i]).getDistance() + 1) {
                            moves++;
                            y += modY[i];
                            x += modX[i];
                            answer.add((byte) dirs[i]);
                            break;
                        }
                    }
                }
            }

            byte[] buffer = ByteBuffer.allocate(4).putInt(moves).array();
            fileOutputStream.write(buffer);
            for(byte b : answer){
                fileOutputStream.write(b);
            }
            MainFrame.mesLabel.CustomSuccess("Rozwiazanie zapisano poprawnie");
        } catch (FileNotFoundException e) {
            MainFrame.mesLabel.CustomError("Blad przy zapisywaniu rozwiazania");
        } catch (IOException e) {
            MainFrame.mesLabel.CustomError("Blad przy zapisywaniu rozwiazania");
        }
    }
}
