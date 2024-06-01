import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class MainFrame extends JFrame {
    MazeLabel mazeLabel;
    public static MessageLabel mesLabel;
    private final NavigationBarPanel navBar;
    private JMenuItem zoomInItem;
    private JMenuItem zoomOutItem;
    JMenuItem saveTextFileItem;
    JMenuItem saveBinaryFileItem;
    JMenuItem saveToPngFileItem;
    String lastBinaryFile;
    int binaryCounter = 0;
    MainFrame(){
        for(int i = 0; i < 10; i++) {
            try {
                Files.deleteIfExists(java.nio.file.Path.of("tempmaze" + i + ".txt"));
            } catch (IOException e) {
                System.out.println("Blad przy usuwaniu pliku tymczasowego");
            }
        }

        this.setMinimumSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        //navbar
        navBar = new NavigationBarPanel(this, 150, 200);
        this.add(navBar, BorderLayout.WEST);

        //maze panel
        mazeLabel = new MazeLabel(this);
        JPanel mazePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mazePanel.add(mazeLabel);
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
                DisableAllButtonsExceptFileInput();
                mazeLabel.LoadMazeFromTextFile(file.toString(), false);
            }
        });
        loadBinaryFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = OpenUserFile("bin");
                if(file == null) {
                    mesLabel.CustomInformation("NONE FILES SELECTED");
                    return;
                }
                DisableAllButtonsExceptFileInput();
                lastBinaryFile = file.toString();
                mazeLabel.LoadMazeFromBinaryFile(file.toString(), binaryCounter);
                binaryCounter++;
            }
        });

        JMenu saveFileMenu = new JMenu("Save File");
        saveTextFileItem = new JMenuItem("To Text File");
        saveBinaryFileItem = new JMenuItem("To Binary File");
        saveToPngFileItem = new JMenuItem("To PNG");
        saveTextFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveToText();
            }
        });
        saveBinaryFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeLabel.SaveMazeToBinaryFile(lastBinaryFile);
            }
        });
        saveToPngFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveAsPng();
            }
        });


        JMenu zoomMenu = new JMenu("Zoom");
        zoomInItem = new JMenuItem("Zoom In");
        zoomOutItem = new JMenuItem("Zoom Out");
        zoomInItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeLabel.ZoomIn();
            }
        });
        zoomOutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeLabel.ZoomOut();
            }
        });


        menuBar.add(loadFileMenu);
        menuBar.add(saveFileMenu);
        menuBar.add(zoomMenu);

        loadFileMenu.add(loadTextFileItem);
        loadFileMenu.add(loadBinaryFileItem);

        saveFileMenu.add(saveTextFileItem);
        saveFileMenu.add(saveBinaryFileItem);
        saveFileMenu.add(saveToPngFileItem);

        zoomMenu.add(zoomInItem);
        zoomMenu.add(zoomOutItem);



        this.add(menuBar, BorderLayout.NORTH);

        DisableAllButtonsExceptFileInput();
        this.setVisible(true);
    }
    private File OpenUserFile(String extension){
        JFileChooser j = new JFileChooser();
        j.setFileFilter(new FileNameExtensionFilter("." + extension, extension));
        j.showOpenDialog(null);
        return j.getSelectedFile();
    }
    //save as png
    public void SaveAsPng(){
        JFileChooser j = new JFileChooser();
        j.setFileFilter(new FileNameExtensionFilter(".png", "png"));
        j.showSaveDialog(null);
        File file = j.getSelectedFile();
        if(file == null || file.toString().isEmpty()){
            mesLabel.CustomInformation("NONE FILES SELECTED");
            return;
        }
        if(!file.toString().endsWith(".png")){
            file = new File(file.toString() + ".png");
        }
        mazeLabel.GeneratePNG(file.toString());
    }
    //save to text
    public void SaveToText(){
        JFileChooser j = new JFileChooser();
        j.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
        j.showSaveDialog(null);
        File file = j.getSelectedFile();
        if(file == null || file.toString().isEmpty()){
            mesLabel.CustomInformation("NONE FILES SELECTED");
            return;
        }
        mazeLabel.SaveMazeToTextFile(file.toString());
    }
    public void setNavbarStartEndButtonsEnabled(boolean enabled){
        navBar.setEnabledStartEndButtons(enabled);
    }
    public void setSolveButtonEnabled(boolean enabled){
        navBar.setSolveButtonEnabled(enabled);
    }
    public int getCurrentlyPressedNavbar(){
        return navBar.getCurrentlyPressed();
    }
    public void unclickNavbarStartEndButtons(){
        navBar.unclickButtons();
    }
    public void SolveMaze(){
        mazeLabel.Solve();
    }
    public void zoomInEnabled(boolean enabled){
        this.zoomInItem.setEnabled(enabled);
    }
    public void zoomOutEnabled(boolean enabled){
        this.zoomOutItem.setEnabled(enabled);
    }
    public void EnableSaveToText(boolean enabled){
        this.saveTextFileItem.setEnabled(enabled);
    }
    public void EnableSaveToBinary(boolean enabled){
        this.saveBinaryFileItem.setEnabled(enabled);
    }
    public void EnableSaveToPNG(boolean enabled){
        this.saveToPngFileItem.setEnabled(enabled);
    }
    public void DisableAllButtonsExceptFileInput(){
        this.zoomInEnabled(false);
        this.zoomOutEnabled(false);
        this.setNavbarStartEndButtonsEnabled(false);
        this.setSolveButtonEnabled(false);
        this.saveToPngFileItem.setEnabled(false);
        this.saveBinaryFileItem.setEnabled(false);
        this.saveTextFileItem.setEnabled(false);
    }
}
