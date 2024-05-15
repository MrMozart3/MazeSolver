import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class StartEndButton extends JButton {
    StartEndButton(String text){
        super(text);
        this.setUI(new BasicButtonUI());
        this.setFocusable(false);
        this.setBackground(Color.GRAY);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
    public void click(){
        this.setBackground(Color.CYAN);
    }
    public void unclick(){
        this.setBackground(Color.GRAY);
    }
}
