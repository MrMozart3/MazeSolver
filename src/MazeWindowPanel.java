import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class MazeWindowPanel extends JPanel {
    int y, x;
    int width, height;
    int borderWidth;
    BorderColor topBorder, bottomBorder, leftBorder, rightBorder;
    MazeWindowPanel(MazePanel maze, int width, int height, int y, int x, int borderWidth){
        //init y, x
        this.y = y;
        this.x = x;
        setBorderWidth(borderWidth);
        //
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new GridLayout(1, 1));
        //borders
        ChangeBorder(0, BorderColor.WALL);
        ChangeBorder(1, BorderColor.WALL);
        ChangeBorder(2, BorderColor.WALL);
        ChangeBorder(3, BorderColor.WALL);
        //button
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maze.WindowCLicked(y, x);
            }
        });
        this.add(button);

    }
    public void RefreshBorder(){
        Color wallColor = Color.BLACK, answerColor = Color.RED;
        CompoundBorder border = new CompoundBorder();
        if(topBorder == BorderColor.WALL){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(this.borderWidth, 0, 0, 0, wallColor));
        }
        if(bottomBorder == BorderColor.WALL){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0, 0, this.borderWidth, 0, wallColor));
        }
        if(leftBorder == BorderColor.WALL){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0, this.borderWidth, 0, 0, wallColor));
        }
        if(rightBorder == BorderColor.WALL){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0, 0, 0, this.borderWidth, wallColor));
        }

        if(topBorder == BorderColor.ANSWER){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(this.borderWidth, 0, 0, 0, answerColor));
        }
        if(bottomBorder == BorderColor.ANSWER){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0, 0, this.borderWidth, 0, answerColor));
        }
        if(leftBorder == BorderColor.ANSWER){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0, this.borderWidth, 0, 0, answerColor));
        }
        if(rightBorder == BorderColor.ANSWER){
            border = BorderFactory.createCompoundBorder(border, BorderFactory.createMatteBorder(0, 0, 0, this.borderWidth, answerColor));
        }

        border = BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(this.borderWidth, this.borderWidth, this.borderWidth, this.borderWidth));
        this.setBorder(border);
    }
    public void setBorderWidth(int borderWidth){
        this.borderWidth = borderWidth;
    }
    public void ChangeBorder(int borderIndex, BorderColor color){
        if(borderIndex == 0){
            this.topBorder = color;
        }
        else if(borderIndex == 1){
            this.bottomBorder = color;
        }
        else if(borderIndex == 2){
            this.leftBorder = color;
        }
        else if(borderIndex == 3){
            this.rightBorder = color;
        }
        RefreshBorder();
    }
    public void ChangeBackground(WindowColor color){
        Color startColor = Color.BLUE,
                endColor = Color.GREEN,
                pathColor = Color.RED,
                emptyColor = Color.WHITE,
                filledColor = Color.BLACK;
        switch (color){
            case START -> this.setBackground(startColor);
            case END -> this.setBackground(endColor);
            case PATH -> this.setBackground(pathColor);
            case EMPTY -> this.setBackground(emptyColor);
            case FILLED -> this.setBackground(filledColor);
        }
    }
}
