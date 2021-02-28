import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.*;

public class FileCount {

    private Path dir;
    static BigInteger[] lowerCountResult = new BigInteger[26];
    private final static BlockingQueue<File> queue = new ArrayBlockingQueue<>(4);
    private static boolean done = false;

    public FileCount(Path dir) {
        this.dir = dir;
        init(lowerCountResult);
    }

    final Runnable producer = () -> {
        System.out.println(Thread.currentThread().getId()+"  \n"+Thread.currentThread().getName());
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile()) {
                        try {
                            queue.put(file.toFile());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return FileVisitResult.SKIP_SUBTREE;

                    } else {

                        return FileVisitResult.CONTINUE;

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    final Runnable consumer = () -> {

        while (!done) {
            try {
                File file = queue.take();

                process(file);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    };

    private void process(File file) {

        BigInteger[] lowerCount = countLowerCase(file);
        incrementLetterCount(lowerCount);


    }


    public BigInteger[] countLowerCase(File file) {
        BigInteger[] tempLetterCount = new BigInteger[26];
        init(tempLetterCount);
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream fileReader = new BufferedInputStream(fin);
            int c;
            while ((c = fileReader.read()) != -1) {

                if (Character.isLowerCase((char) c) && c >= 'a' && c <= 'z') {

                    try {
                        tempLetterCount[(char) c - 'a'] = tempLetterCount[(char) c - 'a'].add(new BigInteger("1"));
                    } catch (Exception e) {

                        e.printStackTrace();
                        // System.out.println((char)c+ " --------------------- ");


                    }

                }
            }
            fileReader.close();
            fin.close();

            return tempLetterCount;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new BigInteger[26];


    }


    private void init(BigInteger[] bigIntegers) {
        for (int i = 0; i < bigIntegers.length; i++) {
            bigIntegers[i] = new BigInteger("0");
        }

    }


    private static synchronized void incrementLetterCount(BigInteger[]... tempLetterCounts) {

        for (BigInteger[] temp : tempLetterCounts) {
            for (int i = 0; i < 26; i++) {
                lowerCountResult[i] = lowerCountResult[i].add(temp[i]);
            }
        }


    }


    public void start() throws InterruptedException {

        ExecutorService pool = Executors.newCachedThreadPool();
        Thread producerThread = new Thread(producer);
        Thread[] consumerThreads = new Thread[16];
        for (int i = 0; i < 16; i++) {
            consumerThreads[i] = new Thread(consumer);
        }



        pool.execute(producerThread);
        for (int i = 0; i <16; i++) {

            pool.execute(consumerThreads[i]);
        }
        pool.awaitTermination(1,TimeUnit.HOURS);
        pool.shutdown();


    }
}





