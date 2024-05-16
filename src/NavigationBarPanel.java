import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class NavigationBarPanel extends JPanel {
    private StartEndButton startButton, endButton;
    private int currentlyPressed = 0;
    NavigationBarPanel(int width, int height){
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonsPanel.setPreferredSize(new Dimension(1000, 150));
        //solve button
        JButton solveButton = new JButton("Solve");
        buttonsPanel.add(solveButton);
        //start button
        this.startButton = new StartEndButton("Start");
        buttonsPanel.add(this.startButton);
        //end button
        this.endButton = new StartEndButton("End");
        buttonsPanel.add(this.endButton);
        //listeners
        solveButton = new JButton("xd");
        this.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentlyPressed == 1){
                    currentlyPressed = 0;
                    startButton.unclick();
                }
                else{
                    currentlyPressed = 1;
                    startButton.click();
                }
                endButton.unclick();
            }
        });
        this.endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentlyPressed == 2){
                    currentlyPressed = 0;
                    endButton.unclick();
                }
                else{
                    currentlyPressed = 2;
                    endButton.click();
                }
                startButton.unclick();
            }
        });

        setEnabledStartEndButtons(false);
        this.add(buttonsPanel, BorderLayout.NORTH);

    }
    public void unclickButtons(){
        currentlyPressed = 0;
        startButton.unclick();
        endButton.unclick();
    }
    public int getCurrentlyPressed(){
        return currentlyPressed;
    }
    public void setEnabledStartEndButtons(boolean enabled){
        this.startButton.unclick();
        this.endButton.unclick();
        this.startButton.setEnabled(enabled);
        this.endButton.setEnabled(enabled);
        currentlyPressed = 0;
    }
}
