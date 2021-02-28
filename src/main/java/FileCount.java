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
    private final static BlockingQueue<File> queue = new ArrayBlockingQueue<>(100);
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
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if(dir.toFile().getName().equals("directory")){
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    else return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile()) {
                        try {
                            queue.put(file.toFile());
                            //System.out.println(file.toFile().getName()+ "\t"+queue.size());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return FileVisitResult.SKIP_SUBTREE;

                    } else {

                        return FileVisitResult.CONTINUE;

                    }
                }
            });
            System.out.println("done\n"+queue.size());
            this.done = true;


        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    final Runnable consumer = () -> {
        System.out.println("INNNN");
        while ((!done || !queue.isEmpty() )) {
            try {
                System.out.println("in");
                File file = queue.poll(1,TimeUnit.MILLISECONDS);
                if(file == null) continue;
                System.out.println(file.getName()+"\t"+queue.size());
                process(file);
                System.out.println(file.getName()+" finished");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("OUUUUT");
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

        ExecutorService pool = Executors.newFixedThreadPool(9);
        Thread producerThread = new Thread(producer);
        Thread[] consumerThreads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            consumerThreads[i] = new Thread(consumer);
        }



        pool.execute(producerThread);
        for (int i = 0; i <8; i++) {

            pool.execute(consumerThreads[i]);
        }
        pool.shutdown();
        pool.awaitTermination(1,TimeUnit.HOURS);
        System.out.println("pool done");



    }
}





