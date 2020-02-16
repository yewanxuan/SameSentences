import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class TextReader extends Thread{
    File file;
    String fileFrom;
    LinkedBlockingQueue<Record> queue;
    boolean original = false;
    TextReader(File file, LinkedBlockingQueue<Record> queue) {
        this.fileFrom = file.getName();
        this.file = file;
        this.queue = queue;
        if (this.fileFrom.indexOf("_") == -1) {
            this.original = true;
        }
    }

    public void run() {
        java.io.FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        int count = 0;
        try {
            fileReader = new java.io.FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                if (line == "\n") {
                    line = bufferedReader.readLine();
                    continue;
                }
                if (original) {
                    Record record = new Record();
                    record.content = line.strip();
                    record.lineNum = ++count;
                    record.from = fileFrom;
                    queue.put(record);
                } else {
                    Record record = new Record(line, fileFrom);
                    queue.put(record);
                }
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
