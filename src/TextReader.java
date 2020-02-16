import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class TextReader extends Thread{
    static final String txtFilePath = Config.get("txtFilePath");

    String filePath;
    String fileFrom;
    LinkedBlockingQueue<Record> queue;
    TextReader(String fileName, LinkedBlockingQueue<Record> queue) {
        this.fileFrom = fileName;
        this.filePath = txtFilePath + fileName;
        this.queue = queue;
    }

    public void run() {
        java.io.FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        int count = 0;
        try {
            fileReader = new java.io.FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                Record record = new Record();
                record.content = line.strip();
                record.lineNum = ++count;
                record.from = fileFrom;
                queue.put(record);
                line = bufferedReader.readLine();

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
