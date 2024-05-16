import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class MainFrame extends JFrame {
    MazePanel mazePanel;
    public static MessageLabel mesLabel;
    private final NavigationBarPanel navBar;
    MainFrame(){
        this.setMinimumSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        //navbar
        navBar = new NavigationBarPanel(this, 150, 200);
        this.add(navBar, BorderLayout.WEST);

        //maze panel
        mazePanel = new MazePanel(this);
        //scroll pane
        JScrollPane scrollMazePane = new JScrollPane(mazePanel);
        scrollMazePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollMazePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollMazePane, BorderLayout.CENTER);
        //error bar
        mesLabel = new MessageLabel(" ");
        this.add(mesLabel, BorderLayout.SOUTH);
        //menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu loadFileMenu = new JMenu("Load File");
        JMenuItem loadTextFileItem = new JMenuItem("From Text File");
        JMenuItem loadBinaryFileItem = new JMenuItem("From Binary File");

        loadTextFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = OpenUserFile("txt");
                if(file == null){
                    mesLabel.CustomInformation("NONE FILES SELECTED");
                    return;
                }
                mazePanel.LoadMazeFromTextFile(file.toString());
            }
        });

        JMenu saveFileMenu = new JMenu("Save File");
        JMenuItem saveTextFileItem = new JMenuItem("To Text File");
        JMenuItem saveBinaryFileItem = new JMenuItem("To Binary File");

        menuBar.add(loadFileMenu);
        menuBar.add(saveFileMenu);

        loadFileMenu.add(loadTextFileItem);
        loadFileMenu.add(loadBinaryFileItem);

        saveFileMenu.add(saveTextFileItem);
        saveFileMenu.add(saveBinaryFileItem);



        this.add(menuBar, BorderLayout.NORTH);

        this.setVisible(true);

        //mazePanel.LoadMazeFromTextFile("mazes/maze_50.txt");



    }
    private File OpenUserFile(String extension){
        JFileChooser j = new JFileChooser();
        j.setFileFilter(new FileNameExtensionFilter("." + extension, extension));
        j.showOpenDialog(null);
        return j.getSelectedFile();
    }
    public void setNavbarStartEndButtonsEnabled(boolean enabled){
        navBar.setEnabledStartEndButtons(enabled);
    }
    public int getCurrentlyPressedNavbar(){
        return navBar.getCurrentlyPressed();
    }
    public void unclickNavbarStartEndButtons(){
        navBar.unclickButtons();
    }
    public void SolveMaze(){
        mazePanel.Solve();
    }
}
