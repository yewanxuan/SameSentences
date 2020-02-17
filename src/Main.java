import java.io.File;

public class Main {
    static final Long usedMemroy = Long.parseLong(Config.get("usedMemroy")) / 2;
    static final String txtFilePath = Config.get("txtFilePath");

    public static void main(String args[]) throws InterruptedException {
        long preTime = System.currentTimeMillis();
        Operator.cleanTextPath();

        File afile = new File(txtFilePath + "a.txt");
        File bfile = new File(txtFilePath + "b.txt");
        long minFileLength = afile.length() < bfile.length() ? afile.length(): bfile.length();

        if (minFileLength < usedMemroy) {
            Operator.hashToDisk(1, afile, bfile);
            Operator.onceResult(new File(txtFilePath + "a.txt_0"), new File(txtFilePath + "b.txt_0"));
        } else {
            try {
                Operator.onceResult(afile, bfile);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - preTime);
    }
}
