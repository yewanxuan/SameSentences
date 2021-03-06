import java.io.File;
import java.io.IOException;

public class Operator {
    static final Long usedMemroy = Long.parseLong(Config.get("usedMemroy"));
    static final String txtFilePath = Config.get("txtFilePath");

    protected static void hashToDisk(int diskBucketNum, File afile, File bfile) throws InterruptedException {
        HashToDisk atxtApart = new HashToDisk(diskBucketNum, afile);
        atxtApart.run();
        HashToDisk btxtApart = new HashToDisk(diskBucketNum, bfile);
        btxtApart.run();
    }

    public static void onceResult(File afile, File bfile) throws InterruptedException {
        if(!afile.exists() || !bfile.exists()) {
            return;
        }
        long minFileLength = afile.length() < bfile.length() ? afile.length(): bfile.length();
        if (minFileLength < usedMemroy) {
            CmpInMemory compareInMemory = new CmpInMemory(afile, bfile);
            compareInMemory.saveSameText();
            return;
        }

        int diskBucketNum = (int) (minFileLength / usedMemroy *
                                   Float.parseFloat(Config.get("diskRedundancy"))+ 1);
        hashToDisk(diskBucketNum, afile, bfile);
        for (int i = 0; i < diskBucketNum; i++) {
            File aFilepart = new File(afile.getAbsolutePath() + "_" + i);
            File bFilepart = new File(bfile.getAbsolutePath() + "_" + i);
            if (!aFilepart.exists() || !bFilepart.exists()) {
                // file not exists
            } else if (aFilepart.length() < usedMemroy || bFilepart.length() < usedMemroy) {
                CmpInMemory compareInMemory = new CmpInMemory(aFilepart, bFilepart);
                compareInMemory.saveSameText();
            } else {
                onceResult(aFilepart, bFilepart);
            }
            aFilepart.delete();
            bFilepart.delete();
        }
        return;
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
