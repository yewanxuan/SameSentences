import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HashToDisk {
    int bucketNum;
    File file;
    boolean original = false;
    static final String txtFilePath = Config.get("txtFilePath");
    String filename;
    String pathPrefix;
    HashToDisk(int bucket, File file) {
        this.filename = file.getName();
        this.pathPrefix = txtFilePath + filename;
        this.bucketNum = bucket;
        this.file = file;
        if (this.filename.indexOf("_") == -1) {
            this.original = true;
        }
    }

    private void writeApart(Record record) {
        int bucket = 0;
        if (bucketNum != 0) {
            bucket = record.getMD5() % bucketNum;
        }
        try{
            String input = record.toString();

            File file = new File(pathPrefix + "_" + bucket);
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
                    record.filename = filename;
                    record.md5Encode = Md5.md5Encode(record.content);
                    writeApart(record);
                } else {
                    Record record = new Record(line, filename);
                    writeApart(record);
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
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
