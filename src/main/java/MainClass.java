import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MainClass {


    public static void main(String[] args) throws IOException, InterruptedException {



        FileCount fileCount = new FileCount(Paths.get("C:\\Users\\NoorZ\\Documents\\dir").toRealPath());
        Long current = System.currentTimeMillis();
        fileCount.start();
        Long after = System.currentTimeMillis();
        System.out.println("time in millis = " + (after - current));
        for(int i=0;i<FileCount.lowerCountResult.length;i++){
            System.out.print((char)(i+'a') + "\t"+FileCount.lowerCountResult[i]+'\n');
        }



    }




}


