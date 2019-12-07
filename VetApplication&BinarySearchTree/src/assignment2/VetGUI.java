package assignment2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import static java.awt.Toolkit.getDefaultToolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.w3c.dom.Document;

public class VetGUI extends JFrame {

    private final Color[] blueTheme = {new Color(115, 232, 255), new Color(41, 182, 246), new Color(0, 134, 195)};
    private final Dimension screenSize = getDefaultToolkit().getScreenSize();
    private final int WINDOW_WIDTH = ((screenSize.width / 2) - screenSize.width / 40), WINDOW_HEIGHT = ((screenSize.height / 2) + screenSize.width / 6);//window scalles up respective to screen size.

    public VetGUI() {
        this.setName("Vet of cute Animals");
        this.setTitle("Vet of cute Animals");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setLocation(screenSize.width / 2 - WINDOW_WIDTH / 2, screenSize.height / 2 - WINDOW_HEIGHT / 2);
        BackPanel backPanel = new BackPanel();
        this.add(backPanel);
        this.setVisible(true);
    }

    private class BackPanel extends JPanel {

        private TopBar topBar;
        private MainPanel mainPanel;
        private SidePanel sidePanel;
        private PriorityPanel priorityPanel;
        private AnimalProcessor animalProcessor;
        private Document XMLdocument;
        private JLabel lblAnimalsLeft, lblTimeSeen;
        private JTextArea txtaSymptons, txtaTreatments;
        private ImageIcon imgProfilePic;
        private String strAnimalsWaiting, strDateSeen, stSymptoms, strTreatments, strPriority, strPatientName, strSpecies;

        private final int TOP_BAR_RATIO = 14, SIDE_PANEL_RATIO = 3, PRIORIY_PANEL_RATIO = 8;
        private final String imgDefualtDogPath = "src/assignment2/Images/dog.png", imgDefaultHorsePath = "src/assignment2/Images/horse.png", imgDefaultCatPath = "src/assignment2/Images/cat.png",
                imgDefaultSanicPath = "src/assignment2/Images/sanic.png", imgDefaultNoImgAvail = "src/assignment2/Images/noImageAvail.png";

        public BackPanel() {
            animalProcessor = new AnimalProcessor();
            while (!AnimalProcessor.XMLValidator(AnimalProcessor.DEFAULT_XML_PATH)) {
                System.out.println("NO VALID");
                JOptionPane.showMessageDialog(null, "Please load an XML file to continue", "No valid XML file found!", JOptionPane.WARNING_MESSAGE);
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file", "xml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    System.out.println("You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
                    if (AnimalProcessor.XMLValidator(chooser.getSelectedFile().getAbsolutePath())) {
                        animalProcessor.getDocument(chooser.getSelectedFile().getAbsolutePath());
                    }
                } else {
                    System.exit(0);
                }
            }
            animalProcessor.loadAnimalsFromXML(animalProcessor.getDocument(AnimalProcessor.DEFAULT_XML_PATH));
            if (animalProcessor.getNextAnimal() != null) {
                imgProfilePic = animalProcessor.getNextAnimal().getImage();
                strAnimalsWaiting = Integer.toString(animalProcessor.animalsLeftToProcess());
                strDateSeen = animalProcessor.getNextAnimal().getDateLastSeen().toString();
                strPatientName = animalProcessor.getNextAnimal().getName();
                strSpecies = animalProcessor.getNextAnimal().getSpecies();
                stSymptoms = animalProcessor.getNextAnimal().getSymptoms();
                strTreatments = animalProcessor.getNextAnimal().getTreatment();
                strPriority = Integer.toString(animalProcessor.getNextAnimal().getPriority());
            } else {
                imgProfilePic = null;
                strAnimalsWaiting = "0";
                strDateSeen = "No patients left!";
                strPatientName = null;
                strSpecies = null;
                stSymptoms = null;
                strTreatments = null;
                strPriority = "1";
                JOptionPane.showMessageDialog(null, "No patients found!", "Empty XML", JOptionPane.WARNING_MESSAGE);
            }
            this.setBackground(Color.BLACK);
            this.setLayout(new BorderLayout());

            topBar = new TopBar();
            mainPanel = new MainPanel();
            sidePanel = new SidePanel();
            priorityPanel = new PriorityPanel();

            this.add(topBar, BorderLayout.NORTH);
            this.add(mainPanel);
            this.add(sidePanel, BorderLayout.EAST);
            this.add(priorityPanel, BorderLayout.SOUTH);

        }

        private class TopBar extends JPanel {

            public TopBar() {
                this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_WIDTH / TOP_BAR_RATIO));
                this.setBackground(blueTheme[1]);
                this.setLayout(new BorderLayout());

                lblAnimalsLeft = new JLabel();
                lblAnimalsLeft.setText("Animals still waiting to be seen: " + strAnimalsWaiting);
                lblAnimalsLeft.setForeground(Color.white);
                lblAnimalsLeft.setFont(new Font(lblAnimalsLeft.getName(), Font.PLAIN, 16));
                lblAnimalsLeft.setHorizontalAlignment(JLabel.CENTER);
                lblAnimalsLeft.setVerticalAlignment(JLabel.TOP);

                lblTimeSeen = new JLabel();
                lblTimeSeen.setText("DATE/TIME SEEN: " + strDateSeen + " - " + strPatientName
                        + "," + "[" + strSpecies + "]");
                lblTimeSeen.setFont(new Font(lblTimeSeen.getName(), Font.BOLD, 20));
                lblTimeSeen.setForeground(Color.white);
                lblTimeSeen.setHorizontalAlignment(JLabel.CENTER);
                lblTimeSeen.setVerticalAlignment(JLabel.BOTTOM);

                this.add(lblAnimalsLeft, BorderLayout.NORTH);
                this.add(lblTimeSeen, BorderLayout.SOUTH);
            }
        }

        private class MainPanel extends JPanel {

            private final ImageIcon imgNoImageAvail = new ImageIcon(animalProcessor.getImage(imgDefaultNoImgAvail));

            public MainPanel() {
                this.setPreferredSize(new Dimension(WINDOW_WIDTH - (WINDOW_WIDTH / SIDE_PANEL_RATIO), WINDOW_HEIGHT - ((WINDOW_WIDTH / TOP_BAR_RATIO) + (WINDOW_WIDTH / PRIORIY_PANEL_RATIO))));
                this.setBackground(Color.WHITE);

            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imgProfilePic != null && imgProfilePic.getIconWidth() > 0) {
                    Image i = imgProfilePic.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST);
                    imgProfilePic.setImage(i);
                    if (imgProfilePic != null) {
                        imgProfilePic.paintIcon(this, g, 0, 0);
                    }
                } else if (animalProcessor.animalsLeftToProcess() > 0) {
                    Image k = imgNoImageAvail.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST);
                    imgNoImageAvail.setImage(k);
                    imgNoImageAvail.paintIcon(this, g, 0, 0);
                }

            }

        }

        private class SidePanel extends JPanel {

            public SidePanel() {
                this.setPreferredSize(new Dimension(WINDOW_WIDTH / SIDE_PANEL_RATIO, WINDOW_HEIGHT - ((WINDOW_WIDTH / TOP_BAR_RATIO) + (WINDOW_WIDTH / PRIORIY_PANEL_RATIO))));
                this.setBackground(Color.CYAN);
                this.setLayout(new GridLayout(2, 1));

                txtaSymptons = new JTextArea(stSymptoms, 10, 20);
                txtaSymptons.setBorder(BorderFactory.createTitledBorder(null, "Symptoms", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font(txtaSymptons.getName(), Font.PLAIN, 30), blueTheme[2]));
                txtaSymptons.setBackground(Color.white);
                txtaSymptons.setForeground(blueTheme[1]);
                txtaSymptons.setFont(new Font(txtaSymptons.getName(), Font.PLAIN, 19));
                txtaSymptons.setPreferredSize(new Dimension(WINDOW_WIDTH / SIDE_PANEL_RATIO, this.getWidth() / 2));

                txtaTreatments = new JTextArea(strTreatments, 10, 20);
                txtaTreatments.setBorder(BorderFactory.createTitledBorder(null, "Treatments", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font(txtaSymptons.getName(), Font.PLAIN, 30), blueTheme[2]));
                txtaTreatments.setBackground(Color.WHITE);
                txtaTreatments.setForeground(blueTheme[1]);
                txtaTreatments.setFont(new Font(txtaTreatments.getName(), Font.PLAIN, 19));
                txtaTreatments.setPreferredSize(new Dimension(WINDOW_WIDTH / SIDE_PANEL_RATIO, this.getWidth() / 2));

                JScrollPane spSymptons = new JScrollPane(txtaSymptons);
                spSymptons.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                JScrollPane spTeatments = new JScrollPane(txtaTreatments);
                spTeatments.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                this.add(spSymptons);
                this.add(spTeatments);

            }

        }

        private class PriorityPanel extends JPanel {

            private ButtonPanel buttonPanel;
            private JSlider prioritySlider;

            public PriorityPanel() {
                buttonPanel = new ButtonPanel();
                this.setLayout(new BorderLayout());
                this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_WIDTH / PRIORIY_PANEL_RATIO));
                this.setBackground(Color.MAGENTA);
                this.add(buttonPanel, BorderLayout.SOUTH);

                prioritySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, Integer.parseInt(strPriority));
                prioritySlider.setBackground(blueTheme[1]);
                prioritySlider.setForeground(Color.white);
                prioritySlider.setBorder(BorderFactory.createTitledBorder(null, "Priority Level", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font(txtaSymptons.getName(), Font.PLAIN, 20), Color.white));
                prioritySlider.setMajorTickSpacing(1);
                prioritySlider.setPaintLabels(true);
                prioritySlider.setPaintTicks(true);

                this.add(prioritySlider);
            }

            private class ButtonPanel extends JPanel {

                private JTextField nameField = new JTextField(25);
                private JTextField speciesField = new JTextField(25);
                private JTextArea symptomsField = new JTextArea();
                private final JLabel lbName = new JLabel("Name: "), lbSpecies = new JLabel("Species: "), lbSymptoms = new JLabel("Symptoms: "), lbPriority = new JLabel("Priority: ");
                private SpinnerModel model = new SpinnerNumberModel(7, 1, 10, 1);
                private JSpinner priority = new JSpinner(model);
                private JLabel picLB = new JLabel("Profile picture: ");
                private Font lbFont = new Font(picLB.getName(), Font.PLAIN, 16);
                private JButton browseBtn = new JButton("Broswe");
                private String imagePath = null;
                private JFileChooser imageChooser = new JFileChooser();
                private AnimalPatient tempAnimalToAdd;

                JPanel myPanel = new JPanel();
                private final JButton btnNewPatient, btnSeeLater, btnRelease, btnLoadXML, btnSaveXML, btnUpdatePic;

                public ButtonPanel() {
                    this.setLayout(new GridLayout());
                    this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_WIDTH / 20));
                    this.setBackground(Color.LIGHT_GRAY);
                    initalizeAddPatient();

                    btnNewPatient = new JButton("New Patient");
                    btnNewPatient.setBackground(blueTheme[2]);
                    btnNewPatient.setForeground(Color.white);
                    btnNewPatient.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                                            "New Patient Datails:", JOptionPane.OK_CANCEL_OPTION);
                                    if (result == JOptionPane.OK_OPTION) {
                                        if (!nameField.getText().isEmpty() && !speciesField.getText().isEmpty()) {
                                            tempAnimalToAdd = new AnimalPatient(speciesField.getText(), nameField.getText());
                                            tempAnimalToAdd.setPriority((int) priority.getValue());
                                            tempAnimalToAdd.setSymptoms(symptomsField.getText());
                                            tempAnimalToAdd.setTreatment("unknow");
                                            if (imagePath != null) {
                                                System.out.println("so path is " + imagePath);
                                                ImageIcon k = new ImageIcon(animalProcessor.getImage(imagePath));
                                                tempAnimalToAdd.setImage(new ImageIcon(animalProcessor.getImage(imagePath)));
                                            }
                                            animalProcessor.addAnimal(tempAnimalToAdd);
                                            animalProcessor.addPatient(nameField.getText(), speciesField.getText(), "2", symptomsField.getText(), imagePath);
                                            updateScreen();
                                            JOptionPane.showMessageDialog(null, "Name: " + nameField.getText() + "\nSpecies: " + speciesField.getText() + "\nPriotiy: " + priority.getValue(), "New Patient added!", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Patient was not added! Missing name or species field.", "Missing fields", JOptionPane.WARNING_MESSAGE);
                                        }
                                    }
                                }
                            }).start();
                        }

                    });
                    this.add(btnNewPatient);

                    btnSeeLater = new JButton("See Later");
                    btnSeeLater.setBackground(blueTheme[2]);
                    btnSeeLater.setForeground(Color.white);
                    btnSeeLater.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (animalProcessor.animalsLeftToProcess() > 0) {
                                        System.out.println("SEE LATER");
                                        animalProcessor.saveXML(txtaSymptons.getText(), txtaTreatments.getText(), null, new Date().toString(), Integer.toString(prioritySlider.getValue()));
                                        animalProcessor.seeLater();
                                        updateScreen();
                                    }
                                }
                            }).start();
                        }

                    });
                    this.add(btnSeeLater);

                    btnRelease = new JButton("Release");
                    btnRelease.setBackground(blueTheme[2]);
                    btnRelease.setForeground(Color.white);
                    btnRelease.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (animalProcessor.animalsLeftToProcess() > 0) {
                                        animalProcessor.deletePatient();
                                        animalProcessor.releaseAnimal();
                                        updateScreen();
                                    }
                                }
                            }).start();
                        }

                    });
                    this.add(btnRelease);

                    btnLoadXML = new JButton("Load XML");
                    btnLoadXML.setBackground(blueTheme[2]);
                    btnLoadXML.setForeground(Color.white);
                    btnLoadXML.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new Thread(new Runnable() {
                                public void run() {
                                    JFileChooser chooser = new JFileChooser();
                                    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file", "xml");
                                    chooser.setFileFilter(filter);
                                    int returnVal = chooser.showOpenDialog(null);
                                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                                        System.out.println("You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
                                        if (AnimalProcessor.XMLValidator(chooser.getSelectedFile().getAbsolutePath())) {
                                            animalProcessor.loadAnimalsFromXML(animalProcessor.getDocument(chooser.getSelectedFile().getAbsolutePath()));
                                            updateScreen();
                                        }
                                    }
                                }
                            }).start();

                        }

                    });
                    this.add(btnLoadXML);

                    btnSaveXML = new JButton("Save XML");
                    btnSaveXML.setBackground(blueTheme[2]);
                    btnSaveXML.setForeground(Color.white);
                    btnSaveXML.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (animalProcessor.animalsLeftToProcess() > 0) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        animalProcessor.saveXML(txtaSymptons.getText(), txtaTreatments.getText(), null, new Date().toString(), Integer.toString(prioritySlider.getValue()));
                                        updateScreen();
                                        JOptionPane.showMessageDialog(null, "Program is saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }).start();
                            }
                        }
                    });
                    this.add(btnSaveXML);

                    btnUpdatePic = new JButton("Update Pic");
                    btnUpdatePic.setBackground(blueTheme[2]);
                    btnUpdatePic.setForeground(Color.white);
                    btnUpdatePic.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (animalProcessor.animalsLeftToProcess() > 0) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        String imagePath = null;
                                        int returnVal = imageChooser.showOpenDialog(null);
                                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                                            imagePath = imageChooser.getSelectedFile().getAbsolutePath();
                                            System.out.println("You chose to open this file: " + imagePath);
                                            animalProcessor.saveXML(null, null, imagePath, null, null);
                                            System.out.println(">>>>>>>>>>"+animalProcessor.getNextAnimal().getImage().getDescription());
                                            updateScreen();
                                            JOptionPane.showMessageDialog(null, "Picture is updated.", "Save", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }
                                }).start();
                            }
                        }

                    });
                    this.add(btnUpdatePic);

                }

                private void initalizeAddPatient() {
                    myPanel.setLayout(new GridLayout(5, 2));

                    lbName.setFont(lbFont);
                    myPanel.add(lbName);
                    nameField.setFont(lbFont);
                    nameField.requestFocus();
                    myPanel.add(nameField);

                    lbSpecies.setFont(lbFont);
                    myPanel.add(lbSpecies);
                    speciesField.setFont(lbFont);
                    myPanel.add(speciesField);

                    symptomsField.setPreferredSize(new Dimension(100, 60));
                    symptomsField.setBorder(nameField.getBorder());
                    lbSymptoms.setFont(lbFont);
                    myPanel.add(lbSymptoms);
                    symptomsField.setFont(lbFont);
                    myPanel.add(symptomsField);

                    priority.setBorder(BorderFactory.createTitledBorder("10=Low & 1=High"));
                    lbPriority.setFont(lbFont);
                    myPanel.add(lbPriority);
                    priority.setFont(lbFont);
                    myPanel.add(priority);

                    picLB.setFont(lbFont);
                    myPanel.add(picLB);
                    browseBtn.setFont(lbFont);
                    browseBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int returnVal = imageChooser.showOpenDialog(null);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                imagePath = imageChooser.getSelectedFile().getAbsolutePath();
                                System.out.println("You chose to open this file: " + imagePath);
                                browseBtn.setBackground(Color.GREEN);
                                browseBtn.setText("Image Loded!");

                            }
                        }

                    });
                    myPanel.add(browseBtn);

                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Png or Jpg file", "png", "jpg");
                    imageChooser.setFileFilter(filter);
                }

                private void updateScreen() {
                    if (animalProcessor.animalsLeftToProcess() >= 1) {
                        if (!btnRelease.isEnabled()) {
                            btnRelease.setBackground(blueTheme[2]);
                            btnRelease.setForeground(Color.white);
                            txtaSymptons.setEnabled(true);
                            txtaTreatments.setEnabled(true);
                            prioritySlider.setEnabled(true);
                            btnRelease.setEnabled(true);
                            btnUpdatePic.setEnabled(true);
                            btnSeeLater.setEnabled(true);
                            btnSeeLater.setBackground(blueTheme[2]);
                            btnSeeLater.setForeground(Color.white);
                            btnUpdatePic.setBackground(blueTheme[2]);
                            btnUpdatePic.setForeground(Color.white);

                        }
                        lblAnimalsLeft.setText("Animals still waiting to be seen: " + animalProcessor.animalsLeftToProcess());
                        lblTimeSeen.setText("DATE/TIME SEEN: " + animalProcessor.getNextAnimal().getDateLastSeen() + " - " + animalProcessor.getNextAnimal().getName()
                                + "," + "[" + animalProcessor.getNextAnimal().getSpecies() + "]");
                        txtaSymptons.setText(animalProcessor.getNextAnimal().getSymptoms());
                        txtaTreatments.setText(animalProcessor.getNextAnimal().getTreatment());
                        prioritySlider.setValue(animalProcessor.getNextAnimal().getPriority());
                        imgProfilePic = animalProcessor.getNextAnimal().getImage();
                        mainPanel.repaint();
                    } else {
                        noAnimalsLeft();
                        btnRelease.setBackground(Color.LIGHT_GRAY);
                        btnRelease.setForeground(Color.LIGHT_GRAY);

                        btnSeeLater.setBackground(Color.LIGHT_GRAY);
                        btnSeeLater.setForeground(Color.LIGHT_GRAY);

                        btnUpdatePic.setBackground(Color.LIGHT_GRAY);
                        btnUpdatePic.setForeground(Color.LIGHT_GRAY);

                        btnUpdatePic.setEnabled(false);
                        btnSeeLater.setEnabled(false);

                        btnRelease.setEnabled(false);
                    }

                }

                private void noAnimalsLeft() {
                    imgProfilePic = null;

                    mainPanel.repaint();
                    lblAnimalsLeft.setText("No animals left to be treated.");
                    lblTimeSeen.setText("No more animals left!");
                    txtaSymptons.setText("");
                    txtaTreatments.setText("");
                    txtaSymptons.setEnabled(false);
                    txtaTreatments.setEnabled(false);
                    prioritySlider.setEnabled(false);
                }
            }
        }

    }

    public static void main(String[] args) {

        VetGUI vetGUI = new VetGUI();
    }
}
