import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextApart extends Thread{
    LinkedBlockingQueue<Record> queue;
    int bucketNum;
    AtomicBoolean readDone;

    static final String txtFilePath = Config.get("txtFilePath");

    TextApart(LinkedBlockingQueue queue, int bucket, AtomicBoolean readDone) {
        this.queue = queue;
        this.bucketNum = bucket;
        this.readDone = readDone;
    }

    private void writeApart(Record record) {
        int bucket = 0;
        if (bucketNum != 0) {
            bucket = record.getMD5() % bucketNum;
        }
        try{
            String input = record.toString();
            String txtPrefix = txtFilePath + record.from;
            File file = new File(txtPrefix + "_" + bucket);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            fileWritter.write(input);
            fileWritter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!readDone.get() || !queue.isEmpty()) {
            Record record;
            try {
                record = queue.take();
                record.md5Encode = Md5.md5Encode(record.content);
                writeApart(record);
            } catch (InterruptedException e) {
                System.out.println("HashApart ThreadInterrupted");
                break;
            }
        }
        System.out.println("HashApart Done");
    }

    public static void cleanTextPath() {
        File folder = new File(txtFilePath);
        File [] files = folder.listFiles();
        for (File file: files) {
            if (file.getName().indexOf("a.txt_") != -1 ||
                    file.getName().indexOf("b.txt_") != -1 ||
                    file.getName().indexOf("result.txt") != -1) {
                file.delete();
            }
        }
    }


}
