package test;

import model.Company;
import model.Person;
import org.apache.spark.sql.SparkSession;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class SparkHDFSLoaderMultithreaded {
    private static SparkSession session;
    private static AtomicLong upper = new AtomicLong(Long.MAX_VALUE);
    private static AtomicLong lower = new AtomicLong(0);
    private static AtomicLong companyUpper = new AtomicLong();
    private static AtomicLong batchSize = new AtomicLong(0);
    private static AtomicLong expectedCount = new AtomicLong(0);
    private static AtomicLong pCount = new AtomicLong(0);
    private static AtomicLong cCount = new AtomicLong(0);
    private static String hdsfPath = "";

    public static class GeneratorPersonTask implements Runnable{
        @Override
        public void run() {
            Random random = new Random();

            List<Person> currentPersonList = new ArrayList<>();

            long count = 0;

            if (pCount.get() >= expectedCount.get())
                return;

            while (count < batchSize.get()) {
                long personsInCompany = random.nextInt(5);

                long companyId = lower.getAndIncrement();

                for (long j = 0; j < personsInCompany; j++) {
                    Person cur = new Person();

                    long personId = upper.getAndDecrement();

                    cur.setId(personId);
                    cur.setCity(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                    cur.setFirstname(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                    cur.setAge(18L + random.nextInt(50));
                    cur.setCompanyId(companyId);
                    cur.setSecondname(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                    cur.setHiredDate(new Date().toString());

                    currentPersonList.add(cur);

                    pCount.getAndIncrement();
                    count++;
                }
            }

            System.out.println("Loaded Persons " + pCount);

            session.createDataFrame(currentPersonList, Person.class)
                    .write()
                    .mode("append")
                    .parquet(hdsfPath + "/Person");
        }
    }

    public static class GeneratorCompanyTask implements Runnable{
        @Override
        public void run() {
            List<Company> currentCompanyList = new ArrayList<>();

            long count = 0;

            if (cCount.get() > expectedCount.get())
                return;

            while (count < batchSize.get()) {
                long companyId = cCount.incrementAndGet();

                Company cur = new Company(
                        companyId,
                        UUID.randomUUID().toString() + UUID.randomUUID().toString(),
                        UUID.randomUUID().toString() + UUID.randomUUID().toString());

                currentCompanyList.add(cur);

                count++;
            }

            System.out.println("Loaded Companies " + cCount);

            session.createDataFrame(currentCompanyList, Company.class)
                    .write()
                    .option("mapreduce.fileoutputcommitter.algorithm.version", "2")
                    .mode("append")
                    .parquet(hdsfPath + "/Company");
        }
    }

    public static void main(String[] args) {
        hdsfPath = args[0];
        companyUpper.set(Long.valueOf(args[2]));
        batchSize.set(Long.valueOf(args[1]));
        expectedCount.set(Long.valueOf(args[2]));

        session = SparkSession.builder()
                .getOrCreate();

        session.sparkContext().setLocalProperty("spark.scheduler.pool", "production");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (pCount.get() < expectedCount.get() ||  cCount.get() < expectedCount.get()) {
                        Future f1 = executor.submit(new GeneratorPersonTask());
                        Future f2 = executor.submit(new GeneratorCompanyTask());
                        f2.get();
                        f1.get();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Loaded Persons " + pCount);
        System.out.println("Loaded Companies " + cCount);

        executor.shutdown();

        session.close();
    }
}
