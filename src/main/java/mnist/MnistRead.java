package mnist;

import frame.Word;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MnistRead {

    public static final String base = ".\\src\\main\\resources\\";
    public static final String TRAIN_IMAGES_FILE = base + "mnist\\train-images.idx3-ubyte";
    public static final String TRAIN_LABELS_FILE = base + "mnist\\train-labels.idx1-ubyte";
    public static final String TEST_IMAGES_FILE = base + "mnist\\t10k-images.idx3-ubyte";
    public static final String TEST_LABELS_FILE = base + "mnist\\t10k-labels.idx1-ubyte";

    /**
     * change bytes into a hex string.
     *
     * @param bytes bytes
     * @return the returned hex string
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * get images of 'train' or 'test'
     *
     * @param fileName the file of 'train' or 'test' about image
     * @return one row show a `picture`
     */
    public static double[][] getImages(String fileName) {
        double[][] x = null;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000803".equals(bytesToHex(bytes))) {                        // 读取魔数
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Integer.parseInt(bytesToHex(bytes), 16);           // 读取样本总数
                bin.read(bytes, 0, 4);
                int xPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每行所含像素点数
                bin.read(bytes, 0, 4);
                int yPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每列所含像素点数
                x = new double[number][xPixel * yPixel];
                for (int i = 0; i < number; i++) {
                    double[] element = new double[xPixel * yPixel];
                    for (int j = 0; j < xPixel * yPixel; j++) {
                        element[j] = bin.read();                                // 逐一读取像素值
                        // normalization
//                        element[j] = bin.read() / 255.0;
                    }
                    x[i] = element;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return x;
    }

    /**
     * get labels of `train` or `test`
     *
     * @param fileName the file of 'train' or 'test' about label
     * @return
     */
    public static double[] getLabels(String fileName) {
        double[] y = null;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000801".equals(bytesToHex(bytes))) {
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Integer.parseInt(bytesToHex(bytes), 16);
                y = new double[number];
                for (int i = 0; i < number; i++) {
                    y[i] = bin.read();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return y;
    }

    public static void main(String[] args) {
        double[][] images = getImages(TRAIN_IMAGES_FILE);
        double[] labels = getLabels(TRAIN_LABELS_FILE);

        System.out.println();
    }

    public static MnistTeacher buildTeacher(){
        Map<Word,String> map = new HashMap<>(4096);
        double[][] images = getImages(TRAIN_IMAGES_FILE);
        double[] labels = getLabels(TRAIN_LABELS_FILE);
        int cou = 0;
        for (int i = 0; i < 10000; i++){
            List<String> list = new ArrayList<>();
            for (int j = 1; j <= 1; j++){
                StringBuilder sb = new StringBuilder();
                for (int k = 1; k <= 28*28; k++){
                    int num = j*k-1;
                    if (images[i][num] < 125){
                        sb.append("0");
                    }else {
                        sb.append("1");
                    }
                }
                list.add(sb.toString());
            }
            Word word = new Word(list);
            map.put(word, String.valueOf(labels[i]));
            cou++;
        }
        System.out.println(cou+"个");
        MnistTeacher mnistTeacher = new MnistTeacher(map);
        return mnistTeacher;
    }


    public static Map<Word,String> buildTestSet(){
        Map<Word,String> map = new HashMap<>(1024);
        double[][] images = getImages(TEST_IMAGES_FILE);
        double[] labels = getLabels(TEST_LABELS_FILE);
        for (int i = 0; i < 10000; i++){
            List<String> list = new ArrayList<>();
            for (int j = 1; j <= 1; j++){
                StringBuilder sb = new StringBuilder();
                for (int k = 1; k <= 28*28; k++){
                    int num = j*k-1;
                    if (images[i][num] < 125){
                        sb.append("0");
                    }else {
                        sb.append("1");
                    }
                }
                list.add(sb.toString());
            }
            Word word = new Word(list);
            map.put(word, String.valueOf(labels[i]));
        }
        return map;
    }

}

