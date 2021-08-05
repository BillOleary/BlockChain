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
import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.List;
import java.util.*;

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
                hashMapOfTextFields.put(list.getName(), (JFormattedTextField) list);
            }
        }

        hashPrevTextField.setFormatterFactory(new DefaultFormatterFactory());
        try {
            hashPrevTextField.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                            listOfChains.get(currentBlockOnDisplay - 1)
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
                            listOfChains.get(currentBlockOnDisplay - 1)
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
                            listOfChains.get(currentBlockOnDisplay - 1)
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
                            listOfChains.get(currentBlockOnDisplay - 1)
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
                            listOfChains.get(currentBlockOnDisplay - 1)
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
        displayText(listOfChains.get(0));

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
        buttonBar = new JPanel();
        leftButton = new JButton();
        rightButton = new JButton();
        testBlockButton = new JButton();
        resetChainButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

                //---- blockIDTextField ----
                blockIDTextField.setBorder(new LineBorder(Color.black, 1, true));
                blockIDTextField.setEditable(false);
                blockIDTextField.setColumns(20);
                blockIDTextField.setHorizontalAlignment(SwingConstants.LEFT);
                blockIDTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                blockIDTextField.setName("Block ID");
                blockIDTextField.setOpaque(false);
                blockIDTextField.addPropertyChangeListener("value", this::textFieldPropertyChange);

                //---- timeStampLabel ----
                timeStampLabel.setText("TimeStamp");
                timeStampLabel.setBorder(new LineBorder(Color.black, 1, true));

                //---- timeStampTextField ----
                timeStampTextField.setBorder(new LineBorder(Color.black, 1, true));
                timeStampTextField.setEditable(false);
                timeStampTextField.setColumns(20);
                timeStampTextField.setHorizontalAlignment(SwingConstants.LEFT);
                timeStampTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                timeStampTextField.setName("Time Stamp");
                timeStampTextField.setOpaque(false);
                timeStampTextField.addPropertyChangeListener("value", this::textFieldPropertyChange);

                //---- nonceLabel ----
                nonceLabel.setText("Nonce");
                nonceLabel.setBorder(new LineBorder(Color.black, 1, true));

                //---- nonceTextField ----
                nonceTextField.setBorder(new LineBorder(Color.black, 1, true));
                nonceTextField.setEditable(false);
                nonceTextField.setColumns(20);
                nonceTextField.setHorizontalAlignment(SwingConstants.LEFT);
                nonceTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                nonceTextField.setName("Nonce");
                nonceTextField.setOpaque(false);
                nonceTextField.addPropertyChangeListener("value", this::textFieldPropertyChange);

                //---- hashPrevJLabel ----
                hashPrevJLabel.setText("Hash Prev");
                hashPrevJLabel.setBorder(new LineBorder(Color.black, 1, true));

                //---- hashPrevTextField ----
                hashPrevTextField.setBorder(new LineBorder(Color.black, 1, true));
                hashPrevTextField.setColumns(20);
                hashPrevTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                hashPrevTextField.setName("Hash Prev");
                hashPrevTextField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                hashPrevTextField.setOpaque(false);
                hashPrevTextField.addPropertyChangeListener("value", this::textFieldPropertyChange);

                //---- hashJLabel ----
                hashJLabel.setText("Hash Curr");
                hashJLabel.setBorder(new LineBorder(Color.black, 1, true));

                //---- hashCurrentTextField ----
                hashCurrentTextField.setBorder(new LineBorder(Color.black, 1, true));
                hashCurrentTextField.setColumns(20);
                hashCurrentTextField.setHorizontalAlignment(SwingConstants.LEFT);
                hashCurrentTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                hashCurrentTextField.setName("Hash Curr");
                hashCurrentTextField.setOpaque(false);
                hashCurrentTextField.addPropertyChangeListener("value", this::textFieldPropertyChange);

                //---- timeJLabel ----
                timeJLabel.setText("Time To Gen");
                timeJLabel.setBorder(new LineBorder(Color.black, 1, true));

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

                GroupLayout blockNoJPanelLayout = new GroupLayout(blockNoJPanel);
                blockNoJPanel.setLayout(blockNoJPanelLayout);
                blockNoJPanelLayout.setHorizontalGroup(
                    blockNoJPanelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, blockNoJPanelLayout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                    .addGap(156, 156, 156)
                                    .addComponent(blockDemoLabel, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(label1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(blockNoTextField, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                                .addGroup(GroupLayout.Alignment.LEADING, blockNoJPanelLayout.createSequentialGroup()
                                    .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(timeStampLabel)
                                        .addComponent(blockIDLabel, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(blockNoJPanelLayout.createParallelGroup()
                                        .addComponent(timeStampTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                                        .addComponent(blockIDTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)))
                                .addGroup(GroupLayout.Alignment.LEADING, blockNoJPanelLayout.createSequentialGroup()
                                    .addComponent(nonceLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nonceTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                                .addGroup(blockNoJPanelLayout.createSequentialGroup()
                                    .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(hashPrevJLabel, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(hashJLabel, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(timeJLabel))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(blockNoJPanelLayout.createParallelGroup()
                                        .addComponent(hashCurrentTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                                        .addComponent(timeToGenerateTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                                        .addComponent(hashPrevTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))))
                            .addContainerGap())
                );
                blockNoJPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {blockIDLabel, hashJLabel, hashPrevJLabel, nonceLabel, timeJLabel, timeStampLabel});
                blockNoJPanelLayout.setVerticalGroup(
                    blockNoJPanelLayout.createParallelGroup()
                        .addGroup(blockNoJPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(blockDemoLabel)
                                .addComponent(label1)
                                .addComponent(blockNoTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
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
                                .addComponent(hashJLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(hashCurrentTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(blockNoJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(timeJLabel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(timeToGenerateTextField, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                            .addGap(60, 60, 60))
                );
                blockNoJPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {blockIDLabel, blockIDTextField, hashCurrentTextField, hashJLabel, hashPrevJLabel, hashPrevTextField, nonceLabel, nonceTextField, timeJLabel, timeToGenerateTextField});
            }
            dialogPane.add(blockNoJPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(null);

                //---- leftButton ----
                leftButton.setIcon(null);
                leftButton.setText("Left");
                leftButton.addActionListener(this::leftButtonActionPerformed);

                //---- rightButton ----
                rightButton.setIcon(null);
                rightButton.setText("Right");
                rightButton.addActionListener(this::rightButtonActionPerformed);

                //---- testBlockButton ----
                testBlockButton.setText("Test ");
                testBlockButton.addActionListener(this::testBlockButtonActionPerformed);

                //---- resetChainButton ----
                resetChainButton.setText("Reset");
                resetChainButton.addActionListener(this::resetBlockActionPerformed);

                GroupLayout buttonBarLayout = new GroupLayout(buttonBar);
                buttonBar.setLayout(buttonBarLayout);
                buttonBarLayout.setHorizontalGroup(
                    buttonBarLayout.createParallelGroup()
                        .addGroup(buttonBarLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(resetChainButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                            .addComponent(leftButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rightButton)
                            .addGap(82, 82, 82)
                            .addComponent(testBlockButton))
                );
                buttonBarLayout.setVerticalGroup(
                    buttonBarLayout.createParallelGroup()
                        .addGroup(buttonBarLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(testBlockButton)
                            .addComponent(resetChainButton)
                            .addComponent(leftButton)
                            .addComponent(rightButton))
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
    private JPanel buttonBar;
    private JButton leftButton;
    private JButton rightButton;
    private JButton testBlockButton;
    private JButton resetChainButton;
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
