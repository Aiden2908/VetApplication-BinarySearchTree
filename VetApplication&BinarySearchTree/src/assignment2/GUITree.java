package assignment2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import static java.awt.Toolkit.getDefaultToolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GUITree extends JFrame {

    private final int WINDOW_HEIGHT = 900, WINDOW_WIDTH = 800;

    public GUITree() {
        this.setTitle("GUI Tree");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = getDefaultToolkit().getScreenSize();
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setLocation((screenSize.width / 2) - WINDOW_WIDTH / 2, (screenSize.height / 2) - WINDOW_HEIGHT / 2);

        BackPanel backPanel = new BackPanel();
        this.add(backPanel);

        this.setVisible(true);

    }

    private class BackPanel extends JPanel {

        private MainPanel mainPanel;
        private ButtonPanel buttonPanel;
        private final int CHILD_PANEL_RATIO = 22;
        private BinarySearchTree binarySearchTree;

        public BackPanel() {
            this.setBackground(Color.BLACK);
            this.setLayout(new BorderLayout());
            binarySearchTree = new BinarySearchTree();
            initializeTree();
            mainPanel = new MainPanel();
            buttonPanel = new ButtonPanel();
            this.add(mainPanel, BorderLayout.CENTER);
            this.add(buttonPanel, BorderLayout.SOUTH);
        }

        private void initializeTree() {
            binarySearchTree.add("cow");
            binarySearchTree.add("fly");
            binarySearchTree.add("dog");
            binarySearchTree.add("bat");
            binarySearchTree.add("fox");
            binarySearchTree.add("eel");
            binarySearchTree.add("ant");
            binarySearchTree.add("ans");
        }

        private class MainPanel extends JPanel {

            public MainPanel() {
                this.setBackground(Color.WHITE);
                this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - (WINDOW_HEIGHT / CHILD_PANEL_RATIO)));
                System.out.println(WINDOW_HEIGHT - (WINDOW_HEIGHT / CHILD_PANEL_RATIO));

            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                binarySearchTree.drawTree(g, WINDOW_WIDTH);
            }

        }

        private class ButtonPanel extends JPanel implements ActionListener {

            private final JButton btnAdd, btnRemove, btnLevelOrderTraverse, btnInOrderTraverse, btnLeftRotate, btnRightRotate;
            private Timer timer;

            public ButtonPanel() {
                this.setBackground(Color.LIGHT_GRAY);
                this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT / CHILD_PANEL_RATIO));
                //this.setLayout(new GridLayout());

                btnAdd = new JButton("Add Node");
                btnAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String temp = JOptionPane.showInputDialog(null, "Name your new node:", "Add Node", JOptionPane.QUESTION_MESSAGE);
                        if (temp != null) {
                            binarySearchTree.add(temp);
                            mainPanel.repaint();
                        }
                    }

                });
                this.add(btnAdd);

                btnRemove = new JButton("Remove Node");
                btnRemove.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String temp = JOptionPane.showInputDialog(null, "Enter the name of node to be removed:", "Remove Node", JOptionPane.QUESTION_MESSAGE);
                        if (temp != null) {
                            if (!binarySearchTree.remove(temp)) {
                                JOptionPane.showMessageDialog(null, "That node does not exist:", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            mainPanel.repaint();
                        }
                    }

                });
                this.add(btnRemove);

                btnLevelOrderTraverse = new JButton("Level Order Traverse");
                btnLevelOrderTraverse.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                btnInOrderTraverse.setEnabled(false);
                                btnLevelOrderTraverse.setEnabled(false);
                                timer = new Timer(binarySearchTree.getVisitTime() * binarySearchTree.getNumElements(), buttonPanel);
                                timer.start();
                                binarySearchTree.LevelOrderTraverse(mainPanel);
                            }
                        }).start();
                    }

                });
                this.add(btnLevelOrderTraverse);

                btnInOrderTraverse = new JButton("In Order Traverse");
                btnInOrderTraverse.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                timer = new Timer(binarySearchTree.getVisitTime() * binarySearchTree.getNumElements(), buttonPanel);
                                timer.start();
                                btnInOrderTraverse.setEnabled(false);
                                btnLevelOrderTraverse.setEnabled(false);
                                binarySearchTree.inOrderTraverse(binarySearchTree.rootNode, mainPanel);
                            }
                        }).start();
                    }

                });
                this.add(btnInOrderTraverse);

                btnLeftRotate = new JButton("Left Rotate");
                btnLeftRotate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        String temp = JOptionPane.showInputDialog(null, "Enter the name of node to be searched:", "Search Node", JOptionPane.QUESTION_MESSAGE);
//                        binarySearchTree.leftRotate(temp);
//
//                      //  binarySearchTree.setNode("bat", "KOK");
//                        mainPanel.repaint();
                    }

                });
                this.add(btnLeftRotate);

                btnRightRotate = new JButton("Right Rotate");
                this.add(btnRightRotate);

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                //   binarySearchTree.resetElementVisited();

                binarySearchTree.resetElementVisited();
                mainPanel.repaint();
                timer.stop();
                btnInOrderTraverse.setEnabled(true);
                btnLevelOrderTraverse.setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        GUITree guiTree = new GUITree();
    }
}
