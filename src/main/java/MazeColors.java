import java.awt.*;

public enum MazeColors {
    START(Color.GREEN),
    END(Color.BLUE),
    ANSWER(Color.RED),
    EMPTY(Color.WHITE),
    FILLED(Color.BLACK);
    public final Color color;
    MazeColors(Color color){
        this.color = color;
    }
}
