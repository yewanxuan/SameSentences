public class Record implements Comparable<Record>{
    public int lineNum;
    public String content;
    public String md5Encode = "";
    public int md5ToInt = 0;
    public String filename;

    public static int usedMemory() {
        return 100 * 2 + 4 + 16;
    }

    public String toString() {
        return md5Encode + "|" + lineNum + "|" + content + "\n";
    }

    public int getMD5() {
        if (md5ToInt == 0 && md5Encode != "") {
            int offset = this.filename.lastIndexOf('_') + 1;
            this.md5ToInt = Md5.byteToInt(this.md5Encode, offset);
        }
        return this.md5ToInt;
    }

    static public String getResult(Record one, Record two) {
        Record atxt = one;
        Record btxt = two;
        if (one.filename.indexOf("a.txt") == -1) {
            atxt = two;
            btxt = one;
        }
        return one.content + "#" + atxt.lineNum + "#" + btxt.lineNum + "\n";
    }

    Record() { }
    Record(String line, String from) {
        this.filename = from;
        if (line.split("\\|").length == 3) {
            md5Encode = line.split("\\|")[0];
            lineNum = Integer.parseInt(line.split("\\|")[1]);
            content = line.split("\\|")[2];
            md5ToInt = this.getMD5();
        }
    }

    @Override
    public int compareTo(Record record) {
        if(record == null) {
            return -1;
        }
        int ret = this.getMD5() - record.getMD5();
        if(ret == 0) {
            ret = this.content.compareTo(record.content);
        }
        return ret;
    }
}
