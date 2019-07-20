package EncodingVisualizationStart;

import javafx.animation.StrokeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Encoding_Controller implements Initializable {

    final FileChooser mFileChoose = new FileChooser();

    private HuffmanTree mHuffmanTree;


    private File mCurrentInputFile;

    @FXML
    private BorderPane mBorderPane;

    @FXML
    private AnchorPane mAnchorPane;

    @FXML
    private TextField mTextToBeEncodedTextField;

    @FXML
    private TextField mBytesBeforeEncodingTextField;

    @FXML
    private TextField mBytesAfterEncodingTextField;

    @FXML
    private TextField mEncodedStringTextField;

    @FXML
    private TextField mOriginalStringTextField;

    @FXML
    private TextField mWeightTextField;

    @FXML
    private TextField mElementTextField;

    @FXML
    private TextField mCodeTextField;

    @FXML
    private Button mDecodeButton;

    @FXML
    private Button mChooseFileButton;

    @FXML
    private Button mSaveFileButton;

    @FXML
    private TextArea mDecodedFileTextArea;


    @FXML
    private Button mFastForwardButton;

    @FXML
    private Button mRewindButton;

    @FXML
    private Button mPauseButton;

    @FXML
    private Button mChooseDirectoryButton;

    @FXML
    private ComboBox mBeforeEncodeOutputByteTypeComboBox;

    @FXML
    private ComboBox mAfterEncodeOutputByteTypeComboBox;

    //private Desktop mDesktop = Desktop.getDesktop();

    //private HuffmanTree mHuffmanTree;

    //private HuffmanTreeView mHuffmanTreeView;

    //private Stack <Circle> mCircleStack;

    //private HuffmanTreeAdapter mHuffmanTreeAdapter;

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mHuffmanTree = new HuffmanTree();
        //mCircleStack = new Stack <>();
        configureFileChooser(mFileChoose);
        mChooseFileButton.setOnAction(e -> {
            File file = mFileChoose.showOpenDialog(mChooseFileButton.getScene().getWindow());
            StringBuilder stringBuilder = new StringBuilder();
            if (file != null) {
                stringBuilder.append(compressFile(file));
                updateUI(stringBuilder.toString());
            }
            mCurrentInputFile = file;
        });
        mSaveFileButton.setOnAction(e -> {
            configureFileChooser(mFileChoose);
            File saveFile = mFileChoose.showSaveDialog(mChooseFileButton.getScene().getWindow());
            if (saveFile != null) {
//                HuffmanTree root = mHuffmanTree;
                try {
                    saveFile(saveFile);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
        mDecodeButton.setOnAction(e -> {
            try {
                setDecodeButtonOnAction();
            } catch (IOException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        //Not functional yet
        /*mChooseDirectoryButton.setOnAction(e->{
            StringBuilder stringBuilder = new StringBuilder();
            List<String> fileList = new ArrayList <>();
            List<File> list =
                    mFileChoose.showOpenMultipleDialog(mChooseDirectoryButton.getScene().getWindow());
            if(list!=null){
                for(File file:list){
                    String temp = stringBuilder.append(compressFile(file)).toString();
                    fileList.pushNewInstance(temp);
                }
                try {
                    updateUIForMultipleFiles(fileList);
                } catch(Exception f){
                    f.printStackTrace();
                }
            }
        });*/
    }

    private void setDecodeButtonOnAction() throws IOException, ClassNotFoundException {
        File file = mFileChoose.showOpenDialog(mChooseFileButton.getScene().getWindow());
        FileReader fileReader = new FileReader(file.getPath());
        String decodedFile = decodeFile(file);

        mDecodedFileTextArea.setText(decodedFile);
    }

    private String decodeFile(File source) throws IOException, ClassNotFoundException {
        if (!source.exists()) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(source);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        String[] codes = (String[]) (objectInputStream.readObject());
        int dataSize = objectInputStream.readInt();

        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while ((i = fileInputStream.read()) != -1) {
            stringBuilder.append(getBits(i));
        }
        fileInputStream.close();
        stringBuilder.delete(dataSize, stringBuilder.length());

        StringBuilder decodedString = new StringBuilder();
        while (stringBuilder.length() != 0) {
            boolean dataIsOk = false;
            for (int v = 0; v < codes.length; v++) {
                //here we are checking the array of codes for matches in the encoded string
                //If we find a match then we have found an occurrence of that character and add it to the decoded string
                if ((codes[v] != null) && (stringBuilder.indexOf(codes[v]) == 0)) {
                    decodedString.append((char) v);
                    stringBuilder.delete(0, codes[v].length());
                    dataIsOk = true;
                    break;
                }
            }
            if (!dataIsOk) {
                mDecodedFileTextArea.setText("Bad Source File, try another file or save a new text file");
            }
        }
        File decodedFile = mFileChoose.showSaveDialog(mDecodeButton.getScene().getWindow());
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(decodedFile));
        dataOutputStream.write(decodedString.toString().getBytes());
        dataOutputStream.close();

        /*try {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encodedData = stringBuilder.toString();

        if (mHuffmanTree.getRoot() == null) {
            return null;
        }
        Node current = mHuffmanTree.getRoot();
        StringBuilder data = new StringBuilder();
        for (char c : encodedData.toCharArray()) {
            if (c == '0') {
                current = mHuffmanTree.getRoot().left;
            } else if (c == '1') {
                current = mHuffmanTree.getRoot().right;
            } else if (current.left.element == '0' && current.right.element == '0') {
                data.append(mHuffmanTree.getRoot().element);
                current = mHuffmanTree.getRoot();
            }
        }
        return data.toString();*/
        return decodedString.toString();
    }

    private String getBits(int v) {
        v = v % 256;
        String binaryInt = "";
        int i = 0;
        //Using a bitwise operator to set the correct bit
        int temp = v >> i;
        for (int j = 0; j < 8; j++) {
            binaryInt = (temp & 1) + binaryInt;
            i++;
            temp = v >> i;
        }
        return binaryInt;
    }

    private void saveFile(File file) throws IOException {
        //String currentText = mOriginalStringTextField.getText();
        String path = file.getPath();
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(mCurrentInputFile)));

        //This function allows the developer to see how many bytes the file can hold so we can set a limit to avoid overload or leaks
        int availableBytes = dataInputStream.available();

        byte[] bytes = new byte[availableBytes];
        dataInputStream.read(bytes);
        dataInputStream.close();

        String data = new String(bytes);

        int[] counts = mHuffmanTree.getCharacterFrequency(data);
        Tree tree = mHuffmanTree.getHuffmanTree(counts);
        String[] codes = mHuffmanTree.getCodes(tree.root);
        StringBuilder encodedData = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            encodedData.append(codes[data.charAt(i)]);
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));

        //Making sure we keep the array of codes stored in order to retrieve the huffman tree to decode later
        objectOutputStream.writeObject(codes);
        objectOutputStream.writeInt(encodedData.length());
        objectOutputStream.close();

        //Must be written to file as bits since we are using binary
        File outputFile = new File(path);
        BitOutputStream bitOutputStream = new BitOutputStream(outputFile);
        bitOutputStream.writeBits(encodedData.toString());
        bitOutputStream.close();

        /*byte[] bytesAfterEncoding = new byte[0];
        try {
            bytesAfterEncoding = encodedData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        setTextField(outputFile.length() + " bytes", mBytesAfterEncodingTextField);

        /*try {
            writer = new PrintWriter(file);
            writer.println(sb.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    /*private void updateUIForMultipleFiles(List<String> files) {
        mAnchorPane.getChildren().clear();
        // DO NOT USE mHuffmanTreeView.getChildren().clear();
        List<HuffmanTree> huffmanTrees = new ArrayList <>();
        List<HuffmanTreeView> huffmanTreeViews = new ArrayList <>();

        for(String file:files) {
            HuffmanTree huffmanTree = new HuffmanTree();
            huffmanTree.setTextToBeEncoded(file);
            HuffmanTreeView huffmanTreeView = new HuffmanTreeView(huffmanTree);
            //mHuffmanTreeView.getChildren().clear();
            //mHuffmanTreeView = new HuffmanTreeView(huffmanTree);
//            mAnchorPane.getChildren().pushNewInstance(huffmanTreeView);
            huffmanTrees.pushNewInstance(huffmanTree);
            huffmanTreeViews.pushNewInstance(huffmanTreeView);


//          huffmanTreeView.drawTree();

            *//*setBytesBeforeEncodingTextField(files);
            setBytesAfterEncodingTextField(huffmanTree);
            setTextField(files, mOriginalStringTextField);
            setEncodedStringTextField(huffmanTree);*//*
        }
    }*/
    private void updateUI(String text) {
        mAnchorPane.getChildren().clear();
        // DO NOT USE mHuffmanTreeView.getChildren().clear();

        HuffmanTree huffmanTree = new HuffmanTree();
        huffmanTree.setTextToBeEncoded(text);
        HuffmanTreeView huffmanTreeView = new HuffmanTreeView(huffmanTree);
        //mHuffmanTreeView.getChildren().clear();
        //mHuffmanTreeView = new HuffmanTreeView(huffmanTree);
        mAnchorPane.getChildren().add(huffmanTreeView);


        huffmanTreeView.drawTree();

        mHuffmanTree = huffmanTree;

        setBytesBeforeEncodingTextField(text);
        setBytesAfterEncodingTextField(huffmanTree, text);
        setTextField(text, mOriginalStringTextField);
        setEncodedStringTextField(huffmanTree);
    }

    @FXML
    private void setEncodedTextButtonListener() {
        String text = mTextToBeEncodedTextField.getText();

        updateUI(text);
    }

    private void setBytesBeforeEncodingTextField(String text) {
        byte[] bytesBeforeEncoding = new byte[0];
        try {
            bytesBeforeEncoding = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setTextField(bytesBeforeEncoding.length + " bytes", mBytesBeforeEncodingTextField);
    }

    private void setBytesAfterEncodingTextField(HuffmanTree huffmanTree, String text) {
        int[] counts = huffmanTree.getCharacterFrequency(text);
        Tree tree = huffmanTree.getHuffmanTree(counts);
        String[] codes = huffmanTree.getCodes(tree.root);
        StringBuilder encodedData = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            encodedData.append(codes[text.charAt(i)]);
        }
        byte[] bytesAfterEncoding = new byte[0];
        try {
            bytesAfterEncoding = encodedData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setTextField(bytesAfterEncoding.length + " bytes", mBytesAfterEncodingTextField);

        setTextField(bytesAfterEncoding.length + " bytes", mBytesAfterEncodingTextField);
    }
    /*private void setEncodedStringTextField() {
        Node root = mHuffmanTree.getTree().root;

        String[] codes = mHuffmanTree.getCodes(mHuffmanTree.getTree().root);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codes.length; i++) {

            if (codes[i] != null) {
                sb.append(codes[i]);
            }
        }
        setTextField(sb.toString(), mEncodedStringTextField);
    }*/

    /* private void setBytesAfterEncodingTextField() {
         Node root = mHuffmanTree.getTree().root;

         String[] codes = mHuffmanTree.getCodes(mHuffmanTree.getTree().root);

         byte[] totalBytesAfterEncoding = new byte[0];

         for (int i = 0; i < codes.length; i++) {
             if (codes[i] != null) {
                 try {
                     totalBytesAfterEncoding = codes[i].getBytes("UTF-8");
                 } catch (UnsupportedEncodingException e) {
                     e.printStackTrace();
                 }
             } else {

             }
         }

         setTextField(totalBytesAfterEncoding.length + " bytes", mBytesAfterEncodingTextField);
     }*/
    private void setEncodedStringTextField(HuffmanTree huffmanTree) {
        Node root = huffmanTree.getTree().root;

        String[] codes = huffmanTree.getCodes(root);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codes.length; i++) {

            if (codes[i] != null) {
                sb.append(codes[i]);
            }
        }
        setTextField(sb.toString(), mEncodedStringTextField);
    }

//    @FXML
//    public void setPlayButtonListener() {
//        mAnchorPane.setContent(null);
//
//        Pane pane = new Pane();
//        Stack <Circle> circleStack = mCircleStack;
//        List <Animation> transitions = new ArrayList <>();
//
//        while (!circleStack.isEmpty()) {
//            Circle circle = circleStack.pop();
//            Circle destination = circleStack.peek();
//            pane.getChildren().pushNewInstance(circle);
//
//            TranslateTransition translateTransitionX = new TranslateTransition(Duration.seconds(1),
//                    circle);
//            translateTransitionX.setFromX(circle.getCenterX());
//            translateTransitionX.setToX(destination.getCenterX());
//            TranslateTransition translateTransitionY = new TranslateTransition(Duration.seconds(1),
//                    circle);
//            translateTransitionY.setFromY(circle.getCenterY());
//            translateTransitionY.setToY(destination.getCenterY());
//
//            transitions.pushNewInstance(translateTransitionX);
//            transitions.pushNewInstance(translateTransitionY);
//
//            if (circleStack.isEmpty()) {
//                break;
//            }
//
//        }
//        IntegerProperty nextTransitionIndex = new SimpleIntegerProperty();
//
//        int index = nextTransitionIndex.get();
//        Animation anim = transitions.get(index);
//        anim.setOnFinished(evt -> nextTransitionIndex.set(index + 1));
//        anim.setRate(1);
//        anim.play();
//
//    }

    private void setTextField(String text, TextField textField) {
        textField.setText(text);
    }

    private String compressFile(File file) {
        try {
            //Insert method to compress file here
            StringBuilder data = new StringBuilder();
            FileReader fileReader = new FileReader(file.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            char[] buffer = new char[1024];
            int read = 0;

            while ((read = bufferedReader.read(buffer)) != -1) {
                String inputData = String.valueOf(buffer, 0, read);
                data.append(inputData);
            }
            bufferedReader.close();

            return data.toString();

        } catch (IOException ioe) {
            Logger.getLogger(
                    Encoding_Controller.class.getName()).log(
                    Level.SEVERE, null, ioe);
        }
        return null;
    }

    class HuffmanTreeView extends Pane {


        private HuffmanTree mHuffmanTree;

        private double vGap = 50;
        private double radius = 15;

        private double width = 1200;
        private double height = 650;

        public HuffmanTreeView(HuffmanTree huffmanTree) {
            mHuffmanTree = huffmanTree;

        /*    if (mHuffmanTree.getTree().root == null) {
                setStatus("Tree is Empty");
            }*/
        }

       /* public void setStatus(String msg) {
            getChildren().pushNewInstance(new Text(20, 20, msg));
        }*/

        public void drawTree() {
            try {
                this.getChildren().clear();
                if (mHuffmanTree.getTree().root != null) {
                    drawTree(mHuffmanTree.getTree().root, width / 1.5, vGap,
                            height / 1.7);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void drawTree(Node root, double x, double y, double hGap) {
            if (root.left != null) {
                getChildren().add(new Line(x - (hGap), y + vGap, x, y));
                drawTree(root.left, x - (hGap), y + (vGap), hGap / 2);
            }

            if (root.right != null) {
                getChildren().add(new Line(x + (hGap), y + vGap, x, y));
                drawTree(root.right, x + (hGap), y + (vGap), hGap / 2);
            }

            Circle circle = new Circle(x, y, radius);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);

            if (root.element != 0)

            {
                circle.setOnMouseEntered(e -> {
                            mElementTextField.setText(root.element + "");
                            mWeightTextField.setText(root.weight + "");
                            mCodeTextField.setText(root.code + "");
                            StrokeTransition strokeTransition = new StrokeTransition(Duration.seconds(3), circle, Color.RED, Color.BLACK);
                            strokeTransition.setCycleCount(1);
                            strokeTransition.setAutoReverse(true);
                            strokeTransition.play();
                        }
                );
                //mCircleStack.push(circle);
                getChildren().addAll(circle,
                        new Text(x - 4, y + 4, root.element + ""));
            } else

            {
                circle.setOnMouseEntered(e -> {
                    mElementTextField.setText("Huffman Leaf Node");
                    mWeightTextField.setText(root.weight + "");
                    mCodeTextField.setText(root.code + "");
                    StrokeTransition strokeTransition = new StrokeTransition(Duration.seconds(3), circle, Color.RED, Color.BLACK);
                    strokeTransition.setCycleCount(1);
                    strokeTransition.setAutoReverse(true);
                    strokeTransition.play();
                });

                //mCircleStack.push(circle);
                getChildren().addAll(circle,
                        new Text(x - 6, y + 4, "LN"));
                //this.getChildren().remove(mAnimateCircle);
            }
        }


    }


}


//


