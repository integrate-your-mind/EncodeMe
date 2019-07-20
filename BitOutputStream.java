package EncodingVisualizationStart;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//Liang Java Programming exercise 17.17
//Needed to properly read a file in and out since we are using binary
public class BitOutputStream {
    private ArrayList <Integer> mBits = new ArrayList <>();
    private DataOutputStream mDataOutputStream;

    public BitOutputStream(File file) throws IOException {
        mDataOutputStream = new DataOutputStream(new FileOutputStream(file, true));
    }

    public void writeBits(char bit) throws IOException {
        if (bit == '0') {
            mBits.add(0);
        } else {
            mBits.add(1);
        }

        if (mBits.size() == 8) {
            mDataOutputStream.writeByte(getBytes());
            mBits.clear();
        }
    }

    public void writeBits(String text) throws IOException {
        for (int i = 0; i < text.length(); i++) {
            writeBits(text.charAt(i));
        }
    }

    public void close() throws IOException {
        while (mBits.size() != 0) {
            writeBits('0');
        }
        mDataOutputStream.close();
    }

    private byte getBytes() {
        int sum = 0;
        for (int i = 7, num = 1; i >= 0; i--, num *= 2) {
            sum += mBits.get(i) * num;
        }
        return (byte) sum;
    }
}

