public class MazeWindow {
    private boolean[] walls;
    private int distance;
    MazeWindow()
    {
        walls = new boolean[4];
        distance = -1;
    }
    MazeWindow(boolean topWall, boolean bottomWall, boolean leftWall, boolean rightWall)
    {
        this();
        setWall(0, topWall);
        setWall(1, bottomWall);
        setWall(2, leftWall);
        setWall(3, rightWall);
    }
    MazeWindow(boolean topWall, boolean bottomWall, boolean leftWall, boolean rightWall, int distance){
        this(topWall, bottomWall, leftWall, rightWall);
        setDistance(distance);
    }
    public void setWall(int wallIndex, boolean wall)
    {
        if(wallIndex < 0 || wallIndex > 3){
            System.out.println("Wrong Wall Index");
            return;
        }
        this.walls[wallIndex] = wall;
        return;
    }
    public boolean getWall(int wallIndex)
    {
        if(wallIndex < 0 || wallIndex > 3) {
            System.out.println("Wrong Wall Index");
            return true;
        }
        else{
            return walls[wallIndex];
        }
    }
    public void setDistance(int distance)
    {
        this.distance = distance;
        return;
    }
    public int getDistance(){
        return this.distance;
    }
    @Override
    public String toString()
    {
        String answer = "";
        answer += "T:" + (getWall(0) ? 1 : 0) + " ";
        answer += "B:" + (getWall(1) ? 1 : 0) + " ";
        answer += "L:" + (getWall(2) ? 1 : 0) + " ";
        answer += "R:" + (getWall(3) ? 1 : 0) + " ";
        answer += "d:" + getDistance() + " ";
        return answer;
    }
}
