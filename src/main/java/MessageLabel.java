import javax.swing.*;
import java.awt.*;

public class MessageLabel extends JLabel {
    MessageLabel(String startText){
        this.setText(startText);
    }
    public void CustomLog(String message){
        this.setForeground(Color.BLACK);
        this.setText(" " + message);
    }
    public void CustomInformation(String message){
        this.setForeground(Color.BLUE);
        this.setText(" " + message);
    }
    public void CustomSuccess(String message){
        this.setForeground(Color.GREEN);
        this.setText(" " + message);
    }
    public void CustomError(String error){
        this.setForeground(Color.RED);
        this.setText(" " + error);
    }
    public void ClearMessage(){
        this.setText(" ");
    }
}
