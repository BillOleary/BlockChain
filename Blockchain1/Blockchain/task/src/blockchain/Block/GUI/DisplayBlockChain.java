/*
 * Created by JFormDesigner on Fri Jul 23 15:52:46 AWST 2021
 */

package blockchain.Block.GUI;

import blockchain.Block.BlockProduct.Block;
import blockchain.Block.BlockProduct.BlockManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author William Oleary
 */
public class DisplayBlockChain extends JFrame implements BlockObserver {

    protected ListIterator<Block> blockListIterator;
    private List<Block> listOfChains = new ArrayList<>();
    Map<Integer, Block> updatedBlocks = new HashMap<>();
    //Keep a track of which block you are currently displaying on screen.
    int currentBlockOnDisplay;
    //To register an Observer with the Subject we pass along a BlockManager
    BlockManager blockManager;
    //Keep a link of the text block to be updated via the hashMap
    Map<String, JFormattedTextField> hashMapOfTextFields = new HashMap<>();

    String blockRecordToUpdate;
    Block initialBlock = null;

    ExecutorService executorService = null;

    //Constructor
    public DisplayBlockChain(BlockManager blockManager) {
        initComponents();
        this.blockManager = blockManager;
        initializeValues();
    }

    private void initializeValues() {
        //Set up the GUI as a listener of the Subject - BlockManager (producer of the data)
        blockManager.registerObservers(this);

        //Get a list of all text Field Objects
        for (Component list : blockNoJPanel.getComponents()) {
            if (list instanceof JTextField && list.getName() != null) {
                //((JTextField)list).getInsets(new Insets(1, 4, 1, 1));
                hashMapOfTextFields.put(list.getName(), (JFormattedTextField) list);
            }
        }

        hashPrevTextField.setFormatterFactory(new DefaultFormatterFactory());
        try {
            hashPrevTextField.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(3);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                executorService.submit(() -> {
                    blockManager.persistBlocks();
                    System.exit(0);
                });
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {


            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });

//        leftButton.setIcon(new ImageIcon(this.getClass().getResource("D:\\My Stuff\\Software Projects\\Java\\Blockchain1\\Blockchain\\task\\src\\blockchain\\Block\\GUI\\Icons\\Left Arrow Small.png")));
//        rightButton.setIcon(new ImageIcon(this.getClass().getResource("D:\\My Stuff\\Software Projects\\Java\\Blockchain1\\Blockchain\\task\\src\\blockchain\\Block\\GUI\\Icons\\Right Arrow Small.png")));

    }

    private void updateListWithData() {
        if (blockRecordToUpdate != null) {
            //Get the block to display on screen.  This may be an updated block and will have been stored in the
            //Map of updated Blocks.  The point was to NOT touch the original list which will be an easy way to reset
            //the list back to the original values if we need to.

           initialBlock = updatedBlocks.get(currentBlockOnDisplay) == null ?
                    listOfChains.get(currentBlockOnDisplay) :
                    updatedBlocks.get(currentBlockOnDisplay);
            String updatedString = hashMapOfTextFields.get(blockRecordToUpdate).getText();
            Block previousBlockToAdd = currentBlockOnDisplay - 1 < 0 ?
                    null :
                    listOfChains.get(currentBlockOnDisplay - 1);
            //System.out.println("Updated String \u2192 " + updatedString);

            switch (blockRecordToUpdate) {
                case "Block ID":
                    updatedBlocks.put(currentBlockOnDisplay, new Block(
                            Integer.parseInt(updatedString),
                            initialBlock.getDateTime(),
                            initialBlock.getPreviousBlockHash(),
                            initialBlock.getCurrentBlockHash(),
                            initialBlock.getMagicNumber(),
                            initialBlock.getNumberOfZeros(),
                            initialBlock.getTimeToCalculateBlock(),
                            previousBlockToAdd
                    ));
                    break;
                case "Time Stamp":
                    long newDate = Long.parseLong(new Date(updatedString).toString());
                    updatedBlocks.put(currentBlockOnDisplay, new Block(
                            initialBlock.getCurrentBlockNumber(),
                            newDate,
                            initialBlock.getPreviousBlockHash(),
                            initialBlock.getCurrentBlockHash(),
                            initialBlock.getMagicNumber(),
                            initialBlock.getNumberOfZeros(),
                            initialBlock.getTimeToCalculateBlock(),
                            previousBlockToAdd
                    ));
                    break;
                case "Hash Prev":
                    updatedBlocks.put(currentBlockOnDisplay, new Block(
                            initialBlock.getCurrentBlockNumber(),
                            initialBlock.getDateTime(),
                            updatedString,
                            initialBlock.getCurrentBlockHash(),
                            initialBlock.getMagicNumber(),
                            initialBlock.getNumberOfZeros(),
                            initialBlock.getTimeToCalculateBlock(),
                            previousBlockToAdd
                    ));
                    break;
                case "Hash Curr":
                    updatedBlocks.put(currentBlockOnDisplay, new Block(
                            initialBlock.getCurrentBlockNumber(),
                            initialBlock.getDateTime(),
                            initialBlock.getPreviousBlockHash(),
                            updatedString,
                            initialBlock.getMagicNumber(),
                            initialBlock.getNumberOfZeros(),
                            initialBlock.getTimeToCalculateBlock(),
                            previousBlockToAdd
                    ));
                    break;
                case "Nonce":
                    updatedBlocks.put(currentBlockOnDisplay, new Block(
                            initialBlock.getCurrentBlockNumber(),
                            initialBlock.getDateTime(),
                            initialBlock.getPreviousBlockHash(),
                            initialBlock.getCurrentBlockHash(),
                            Integer.parseInt(updatedString),
                            initialBlock.getNumberOfZeros(),
                            initialBlock.getTimeToCalculateBlock(),
                            previousBlockToAdd
                    ));
                    break;
                case "Time To Gen":
                default:
            }
            displayText(updatedBlocks.get(currentBlockOnDisplay));

            //If the current block Matches the first unmodified block in the List then we can safetly remove the updated
            //block value from the Map DS.
            if (listOfChains.get(currentBlockOnDisplay).equals(updatedBlocks.get(currentBlockOnDisplay))) {
                updatedBlocks.remove(currentBlockOnDisplay);
            }
        }
    }

    private void displayText(Block blockToDisplay) {

        //Set up the current block w.r.t the total count
        blockNoTextField.setText(blockToDisplay.getCurrentBlockNumber() + "/" + listOfChains.size());

        //Now print out the rest of the fields
        blockIDTextField.setText(String.valueOf(blockToDisplay.getCurrentBlockNumber()));
        timeStampTextField.setText(new Date(blockToDisplay.getDateTime()).toString());
        nonceTextField.setText(String.valueOf(blockToDisplay.getMagicNumber()));
        hashPrevTextField.setText(blockToDisplay.getPreviousBlockHash());
        hashPrevTextField.setCaretPosition(0);
        hashCurrentTextField.setText(blockToDisplay.getCurrentBlockHash());
        hashCurrentTextField.setCaretPosition(0);
        timeToGenerateTextField.setText(String.valueOf(blockToDisplay.getTimeToCalculateBlock()));

        if (blockToDisplay.equals(listOfChains.get(currentBlockOnDisplay))) {
            Color limeGreen = new Color(204, 255, 204, 255);
            dialogPane.setBackground(limeGreen);
            blockNoJPanel.setBackground(limeGreen);
            buttonBar.setBackground(limeGreen);
        } else {
            Color redShade = new Color(255, 153, 153, 255);
            dialogPane.setBackground(redShade);
            blockNoJPanel.setBackground(redShade);
            buttonBar.setBackground(redShade);
        }
    }

    /******************************ACTION LISTENERS**************************************/

    private void leftButtonActionPerformed(ActionEvent e) {
        if (currentBlockOnDisplay == 0) {
            currentBlockOnDisplay = listOfChains.size();
        }
        currentBlockOnDisplay -= 1;

        Block block = updatedBlocks.get(currentBlockOnDisplay) == null ?
                listOfChains.get(currentBlockOnDisplay) :
                updatedBlocks.get(currentBlockOnDisplay);
        displayText(block);

    }

    private void rightButtonActionPerformed(ActionEvent e) {
        if (currentBlockOnDisplay == listOfChains.size() - 1) {
            currentBlockOnDisplay = 0;
        } else {
            currentBlockOnDisplay += 1;
        }
        Block block = updatedBlocks.get(currentBlockOnDisplay) == null ?
                listOfChains.get(currentBlockOnDisplay) :
                updatedBlocks.get(currentBlockOnDisplay);
        displayText(block);
    }


    private void testBlockButtonActionPerformed(ActionEvent e) {
        Block updatedBlock = updatedBlocks.get(currentBlockOnDisplay) == null ?
                listOfChains.get(currentBlockOnDisplay) :
                updatedBlocks.get(currentBlockOnDisplay);
        if (updatedBlock.blockIsValid()) {
            JOptionPane.showMessageDialog(dialogPane, "VALID BLOCK", "BLOCK VALIDITY CHECKER", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(dialogPane, "INVALID BLOCK", "BLOCK VALIDITY CHECKER", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void textFieldPropertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getSource() instanceof JFormattedTextField) {
            blockRecordToUpdate = ((JFormattedTextField)propertyChangeEvent.getSource()).getName();
            updateListWithData();
        }
    }
    private void resetBlockActionPerformed(ActionEvent e) {
        //Once the reset Button is hit - all Blocks go back to the initial Block values.
        //Easy way to do this is to remove all values from the updated hash map list
        //as this list is used by the code to keep track of which updated block is displayed
        //on screen.  Clear this and the code defaults to the main list.
        updatedBlocks.clear();
        //update the curren block
        displayText(listOfChains.get(currentBlockOnDisplay));
    }

    private void generateBlockButtonActionPerformed(ActionEvent e) {

        generateBlockButton.setText("Calculating...");
        generateBlockButton.setEnabled(false);
        executorService.submit(() -> {
            blockManager.createTheBlocks((Integer) blocksToGenerateSpinner.getValue());
            generateBlockButton.setText("Generate");
            generateBlockButton.setEnabled(true);
            });
    }




    /****************************OBSERVER update CODE************************************/
    @Override
    public void update(Object listOfBlockChain) {
        if (listOfBlockChain instanceof ListIterator) {
            blockListIterator = (ListIterator<Block>) listOfBlockChain;
        }

        //Clear the list first
        listOfChains.clear();
        //Now get the list of Blocks for the iterator
        while (blockListIterator.hasNext()) {
            listOfChains.add(blockListIterator.next());
        }
        //If the chain object has changed then update the display
        //blockNoTextField.setText(listOfChains.get(currentBlockOnDisplay) + "/" + listOfChains.size());
        displayText(listOfChains.get(currentBlockOnDisplay));

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        blockNoJPanel = new JPanel();
        blockDemoLabel = new JLabel();
        blockIDLabel = new JLabel();
        blockIDTextField = new JFormattedTextField();
        timeStampLabel = new JLabel();
        timeStampTextField = new JFormattedTextField();
        nonceLabel = new JLabel();
        nonceTextField = new JFormattedTextField();
        hashPrevJLabel = new JLabel();
        hashPrevTextField = new JFormattedTextField();
        hashJLabel = new JLabel();
        hashCurrentTextField = new JFormattedTextField();
        timeJLabel = new JLabel();
        timeToGenerateTextField = new JFormattedTextField();
        blockNoTextField = new JTextField();
        label1 = new JLabel();
        leftButton = new JButton();
        rightButton = new JButton();
        buttonBar = new JPanel();
        testBlockButton = new JButton();
        resetChainButton = new JButton();
        generateBlockButton = new JButton();
        blocksToGenerateSpinner = new JSpinner();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("BLOCKCHAIN DISPLAY");
        setVisible(true);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== blockNoJPanel ========
            {
                blockNoJPanel.setBorder(null);

                //---- blockDemoLabel ----
                blockDemoLabel.setText("BlockChain Demo");
                blockDemoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                blockDemoLabel.setBorder(new LineBorder(Color.black, 2, true));

                //---- blockIDLabel ----
                blockIDLabel.setText("Block ID");
                blockIDLabel.setBorder(new LineBorder(Color.black, 1, true));
                blockIDLabel.setToolTipText("Current ID For The Block");
                blockIDLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                //---- blockIDTextField ----
                blockIDTextField.setBorder(new LineBorder(Color.black, 1, true));
                blockIDTextField.setEditable(false);
                blockIDTextField.setColumns(20);
                blockIDTextField.setHorizontalAlignment(SwingConstants.LEFT);
                blockIDTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                blockIDTextField.setName("Block ID");
                blockIDTextField.setOpaque(false);
                blockIDTextField.addPropertyChangeListener("value", e -> textFieldPropertyChange(e));

                //---- timeStampLabel ----
                timeStampLabel.setText("TimeStamp");
                timeStampLabel.setBorder(new LineBorder(Color.black, 1, true));
                timeStampLabel.setToolTipText("Time the Block was Generated at.");
                timeStampLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                //---- timeStampTextField ----
                timeStampTextField.setBorder(new LineBorder(Color.black, 1, true));
                timeStampTextField.setEditable(false);
                timeStampTextField.setColumns(20);
                timeStampTextField.setHorizontalAlignment(SwingConstants.LEFT);
                timeStampTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                timeStampTextField.setName("Time Stamp");
                timeStampTextField.setOpaque(false);
                timeStampTextField.addPropertyChangeListener("value", e -> textFieldPropertyChange(e));

                //---- nonceLabel ----
                nonceLabel.setText("Nonce");
                nonceLabel.setBorder(new LineBorder(Color.black, 1, true));
                nonceLabel.setToolTipText("Proof of Work for current Block");
                nonceLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                //---- nonceTextField ----
                nonceTextField.setBorder(new LineBorder(Color.black, 1, true));
                nonceTextField.setEditable(false);
                nonceTextField.setColumns(20);
                nonceTextField.setHorizontalAlignment(SwingConstants.LEFT);
                nonceTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                nonceTextField.setName("Nonce");
                nonceTextField.setOpaque(false);
                nonceTextField.addPropertyChangeListener("value", e -> textFieldPropertyChange(e));

                //---- hashPrevJLabel ----
                hashPrevJLabel.setText("Hash Prev");
                hashPrevJLabel.setBorder(new LineBorder(Color.black, 1, true));
                hashPrevJLabel.setToolTipText("Hash for the previous Block");
                hashPrevJLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                //---- hashPrevTextField ----
                hashPrevTextField.setBorder(new LineBorder(Color.black, 1, true));
                hashPrevTextField.setColumns(20);
                hashPrevTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                hashPrevTextField.setName("Hash Prev");
                hashPrevTextField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                hashPrevTextField.setOpaque(false);
                hashPrevTextField.addPropertyChangeListener("value", e -> textFieldPropertyChange(e));

                //---- hashJLabel ----
                hashJLabel.setText("Hash Curr");
                hashJLabel.setBorder(new LineBorder(Color.black, 1, true));
                hashJLabel.setToolTipText("Hash of the Current Block");
                hashJLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                //---- hashCurrentTextField ----
                hashCurrentTextField.setBorder(new LineBorder(Color.black, 1, true));
                hashCurrentTextField.setColumns(20);
                hashCurrentTextField.setHorizontalAlignment(SwingConstants.LEFT);
                hashCurrentTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                hashCurrentTextField.setName("Hash Curr");
                hashCurrentTextField.setOpaque(false);
                hashCurrentTextField.addPropertyChangeListener("value", e -> textFieldPropertyChange(e));

                //---- timeJLabel ----
                timeJLabel.setText("Time To Gen");
                timeJLabel.setBorder(new LineBorder(Color.black, 1, true));
                timeJLabel.setToolTipText("Time taken to validated Block");
                timeJLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                //---- timeToGenerateTextField ----
                timeToGenerateTextField.setBorder(new LineBorder(Color.black, 1, true));
                timeToGenerateTextField.setEditable(false);
                timeToGenerateTextField.setColumns(20);
                timeToGenerateTextField.setHorizontalAlignment(SwingConstants.LEFT);
                timeToGenerateTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                timeToGenerateTextField.setName("Time To Gen");
                timeToGenerateTextField.setOpaque(false);

                //---- blockNoTextField ----
                blockNoTextField.setEditable(false);
                blockNoTextField.setBorder(new LineBorder(Color.black, 1, true));
                blockNoTextField.setBackground(Color.lightGray);

                //---- label1 ----
                label1.setText("Block #");

                //---- leftButton ----
                leftButton.setIcon(new ImageIcon("D:\\My Stuff\\Software Projects\\Java\\Blockchain1\\Blockchain\\task\\src\\blockchain\\Block\\Icons\\icons8-left-arrow-48.png"));
                leftButton.addActionListener(e -> leftButtonActionPerformed(e));

                //---- rightButton ----
                rightButton.setIcon(new ImageIcon("D:\\My Stuff\\Software Projects\\Java\\Blockchain1\\Blockchain\\task\\src\\blockchain\\Block\\Icons\\icons8-right-arrow-48.png"));
                rightButton.addActionListener(e -> rightButtonActionPerformed(e));

                GroupLayout blockNoJPanelLayout = new GroupLayout(blockNoJPanel);
                blockNoJPanel.setLayout(blockNoJPanelLayout);
                blockNoJPanelLayout.setHorizontalGroup(
                    blockNoJPanelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, blockNoJPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(blockNoJPanelLayout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, blockNoJPanelLayout.createSequentialGroup()
                                    .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(hashPrevJLabel, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                        .addComponent(hashJLabel, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                        .addComponent(timeJLabel, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(blockNoJPanelLayout.createParallelGroup()
                                        .addComponent(hashCurrentTextField, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(timeToGenerateTextField, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(hashPrevTextField, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                    .addComponent(nonceLabel, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nonceTextField, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE))
                                .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                    .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                            .addComponent(leftButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(rightButton, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                                        .addComponent(blockIDLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                        .addComponent(timeStampLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                                    .addGroup(blockNoJPanelLayout.createParallelGroup()
                                        .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(blockNoJPanelLayout.createParallelGroup()
                                                .addComponent(timeStampTextField, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(blockIDTextField, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                            .addGap(87, 87, 87)
                                            .addComponent(blockDemoLabel, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(label1)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(blockNoTextField)))))
                            .addContainerGap())
                );
                blockNoJPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {blockIDLabel, hashJLabel, hashPrevJLabel, nonceLabel, timeJLabel, timeStampLabel});
                blockNoJPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {blockIDTextField, hashCurrentTextField, hashPrevTextField, nonceTextField, timeStampTextField, timeToGenerateTextField});
                blockNoJPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {leftButton, rightButton});
                blockNoJPanelLayout.setVerticalGroup(
                    blockNoJPanelLayout.createParallelGroup()
                        .addGroup(blockNoJPanelLayout.createSequentialGroup()
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(blockDemoLabel)
                                        .addComponent(label1)
                                        .addComponent(blockNoTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addComponent(leftButton, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(rightButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(blockIDTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(blockIDLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(timeStampTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(timeStampLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(blockNoJPanelLayout.createParallelGroup()
                                .addComponent(nonceTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(nonceLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(blockNoJPanelLayout.createParallelGroup()
                                .addComponent(hashPrevTextField, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addComponent(hashPrevJLabel, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(hashCurrentTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(hashJLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(timeToGenerateTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(timeJLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addGap(60, 60, 60))
                );
                blockNoJPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {blockIDLabel, blockIDTextField, hashCurrentTextField, hashJLabel, hashPrevJLabel, hashPrevTextField, nonceLabel, nonceTextField, timeJLabel, timeStampLabel, timeStampTextField, timeToGenerateTextField});
                blockNoJPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {leftButton, rightButton});
            }
            dialogPane.add(blockNoJPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(null);

                //---- testBlockButton ----
                testBlockButton.setText("Test ");
                testBlockButton.addActionListener(e -> testBlockButtonActionPerformed(e));

                //---- resetChainButton ----
                resetChainButton.setText("Reset");
                resetChainButton.addActionListener(e -> resetBlockActionPerformed(e));

                //---- generateBlockButton ----
                generateBlockButton.setText("Generate");
                generateBlockButton.addActionListener(e -> generateBlockButtonActionPerformed(e));

                //---- blocksToGenerateSpinner ----
                blocksToGenerateSpinner.setBorder(new LineBorder(Color.black, 1, true));
                blocksToGenerateSpinner.setModel(new SpinnerNumberModel(5, 0, null, 1));

                GroupLayout buttonBarLayout = new GroupLayout(buttonBar);
                buttonBar.setLayout(buttonBarLayout);
                buttonBarLayout.setHorizontalGroup(
                    buttonBarLayout.createParallelGroup()
                        .addGroup(buttonBarLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(resetChainButton)
                            .addGap(98, 98, 98)
                            .addComponent(blocksToGenerateSpinner, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(generateBlockButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                            .addComponent(testBlockButton))
                );
                buttonBarLayout.setVerticalGroup(
                    buttonBarLayout.createParallelGroup()
                        .addGroup(buttonBarLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(testBlockButton)
                            .addComponent(resetChainButton)
                            .addComponent(generateBlockButton)
                            .addComponent(blocksToGenerateSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                );
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel blockNoJPanel;
    private JLabel blockDemoLabel;
    private JLabel blockIDLabel;
    private JFormattedTextField blockIDTextField;
    private JLabel timeStampLabel;
    private JFormattedTextField timeStampTextField;
    private JLabel nonceLabel;
    private JFormattedTextField nonceTextField;
    private JLabel hashPrevJLabel;
    private JFormattedTextField hashPrevTextField;
    private JLabel hashJLabel;
    private JFormattedTextField hashCurrentTextField;
    private JLabel timeJLabel;
    private JFormattedTextField timeToGenerateTextField;
    private JTextField blockNoTextField;
    private JLabel label1;
    private JButton leftButton;
    private JButton rightButton;
    private JPanel buttonBar;
    private JButton testBlockButton;
    private JButton resetChainButton;
    private JButton generateBlockButton;
    private JSpinner blocksToGenerateSpinner;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class testTheCurrentBlock extends AbstractAction {

        private testTheCurrentBlock() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
        }
    }
}
