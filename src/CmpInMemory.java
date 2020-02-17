import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class CmpInMemory {
    File base;
    File compare;
    File result = new File(Config.get("txtFilePath") + "result.txt");
    int memBucket;

    CmpInMemory(File aFile, File bFile) {
        base = aFile;
        compare = bFile;
        if (aFile.length() > bFile.length()) {
            base = bFile;
            compare = aFile;
        }
        memBucket = (int)((base.length() / Record.usedMemory()) *
                           Float.parseFloat(Config.get("memRedundancy")) + 1);
    }

    private Map<Integer, TreeSet<Record>> baseHashMap(File base) {
        Map<Integer, TreeSet<Record>> memHash = new HashMap();
        java.io.FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new java.io.FileReader(base);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                Record record = new Record(line, base.getName());
                TreeSet<Record> records = memHash.get(record.getMD5() % memBucket);
                if (records == null) {
                    records = new TreeSet();
                }
                records.add(record);
                memHash.put(record.getMD5() % memBucket, records);
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
        return memHash;
    }

    private void findInHashMap(Map<Integer, TreeSet<Record>>memHash) {

        java.io.FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new java.io.FileReader(compare);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            FileWriter fileWritter = new FileWriter(result,true);

            while (line != null) {
                Record record = new Record(line, base.getName());
                line = bufferedReader.readLine();

                TreeSet<Record> saves = memHash.get(record.getMD5() % memBucket);
                if (saves != null) {
                    saves.contains(record);
                    Record save = saves.ceiling(record);
                    if (saves.contains(record) == false) {
                        continue;
                    } else if (save.compareTo(record) == 0) {
                        fileWritter.write(Record.getResult(save, record));
                        System.out.println(Record.getResult(save, record));
                    }
                }
            }
            fileWritter.flush();
            fileWritter.close();


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

    public void saveSameText() {
        Map<Integer, TreeSet<Record>> memHash = baseHashMap(base);
        findInHashMap(memHash);
    }

    public static void main(String args[]) {

    }
}
