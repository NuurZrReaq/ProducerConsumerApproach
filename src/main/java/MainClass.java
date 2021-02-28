
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;

public class MainClass {


    public static void main(String[] args) throws IOException, InterruptedException {



        FileCount fileCount = new FileCount(Paths.get("C:\\Users\\NoorZ\\Documents\\dir").toRealPath());
        Long current = System.currentTimeMillis();
       // BigInteger[] lowerCount = fileCount.countLowerCase(Paths.get("C:\\Users\\NoorZ\\Documents\\dir\\dir - Copy\\file.txt").toFile());
        fileCount.start();
        Long after = System.currentTimeMillis();
        System.out.println("time in millis = " + (after - current));
        for(int i=0;i<FileCount.lowerCountResult.length;i++){
            System.out.print((char)(i+'a') + "\t"+ FileCount.lowerCountResult[i]+'\n');
        }



    }




}


