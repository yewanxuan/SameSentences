import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    static final Long usedMemroy = Long.parseLong(Config.get("usedMemroy"));

    static final int queueLength = (int)(usedMemroy / Record.usedMemory() / 2);

    static final String txtFilePath = Config.get("txtFilePath");

    public static void main(String args[]) throws InterruptedException {

        for (int k = 0; k < 1; k++) {
            File afile = new File(txtFilePath + "a.txt");
            File bfile = new File(txtFilePath + "b.txt");
            long minFileLength = afile.length() < bfile.length() ? afile.length(): bfile.length();
            int bucketNum = (int) (minFileLength / usedMemroy / 2 + 1);

            LinkedBlockingQueue<Record> firstQueue = new LinkedBlockingQueue(queueLength);
            AtomicBoolean readDone = new AtomicBoolean(false);

            TextApart.cleanTextPath();
            TextApart txtApart = new TextApart(firstQueue, bucketNum, readDone);
            TextReader aTxtReader = new TextReader("a.txt", firstQueue);
            TextReader bTxtReader = new TextReader("b.txt", firstQueue);

            aTxtReader.start();
            bTxtReader.start();
            txtApart.start();

            aTxtReader.join();
            bTxtReader.join();
            readDone.set(true);
            txtApart.join();

            for (int i = 0; i < bucketNum; i++) {
                File aFilepart = new File(txtFilePath + "a.txt_" + i);
                File bFilepart = new File(txtFilePath + "b.txt_" + i);
                if (aFilepart.length() < usedMemroy || bFilepart.length() < usedMemroy) {
                    Compare compare = new Compare(aFilepart, bFilepart);
                    compare.getResult();
                    aFilepart.delete();
                    bFilepart.delete();
                }
            }
        }

    }
}
