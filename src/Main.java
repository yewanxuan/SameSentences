import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    static final Long usedMemroy = Long.parseLong(Config.get("usedMemroy"));

    static final int queueLength = (int)(usedMemroy / Record.usedMemory() / 2);

    static final String txtFilePath = Config.get("txtFilePath");

    private static void onceResult(File afile, File bfile) throws InterruptedException {
        if(!afile.exists() || !bfile.exists()) {
            return;
        }

        long minFileLength = afile.length() < bfile.length() ? afile.length(): bfile.length();
        int diskBucketNum = (int) (minFileLength / usedMemroy + 1);
        System.out.println(afile.getName() + diskBucketNum);

        if (minFileLength < usedMemroy) {
            File aFilepart = new File(afile.getAbsolutePath() + "_0");
            File bFilepart = new File(bfile.getAbsolutePath() + "_0");
            Compare compare = new Compare(aFilepart, bFilepart);
            compare.getResult();
            return;
        }

        LinkedBlockingQueue<Record> firstQueue = new LinkedBlockingQueue(queueLength);
        AtomicBoolean readDone = new AtomicBoolean(false);
        TextApart txtApart = new TextApart(firstQueue, diskBucketNum, readDone);
        TextReader aTxtReader = new TextReader(afile, firstQueue);
        TextReader bTxtReader = new TextReader(bfile, firstQueue);
        aTxtReader.start();
        bTxtReader.start();
        txtApart.start();
        aTxtReader.join();
        bTxtReader.join();
        readDone.set(true);
        txtApart.join();

        for (int i = 0; i < diskBucketNum; i++) {
            File aFilepart = new File(afile.getAbsolutePath() + "_" + i);
            File bFilepart = new File(bfile.getAbsolutePath() + "_" + i);
            if (aFilepart.length() < usedMemroy || bFilepart.length() < usedMemroy) {
                Compare compare = new Compare(aFilepart, bFilepart);
                compare.getResult();
                //aFilepart.delete();
                //bFilepart.delete();
            } else {
                onceResult(aFilepart, bFilepart);
            }
        }
       return;
    }

    public static void main(String args[]) {
        TextApart.cleanTextPath();
        File afile = new File(txtFilePath + "a.txt");
        File bfile = new File(txtFilePath + "b.txt");
        try {
            onceResult(afile, bfile);
        } catch(InterruptedException e) {
            System.out.println(e);
        }
    }
}
