package test;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.SqlFieldsQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgniteNativeSqlSelectBenchmark {
    private static String str = "select avg(p.age) from Person p, Company c where p.companyId = c.id and p.companyId > ? and p.companyId < ?";

    public static void main(String[] args) {
        String configPath = args[0];
        int warmupIter = Integer.valueOf(args[1]);
        int benchIter = Integer.valueOf(args[2]);
        int companyFrom = Integer.valueOf(args[3]);
        int companyTo = Integer.valueOf(args[4]);

        Ignite client = Ignition.start(configPath);

        IgniteCache<Integer, BinaryObject> person = client.cache("Person").withKeepBinary();

        List<Long> latencies = new ArrayList<>();
        List<Long> warmLatencies = new ArrayList<>();

        long time;

        SqlFieldsQuery query = new SqlFieldsQuery(str)
                .setSchema("PUBLIC")
                .setArgs(companyFrom, companyTo)
                .setLazy(true);

        for (int i = 0; i < warmupIter; i++) {
            time = System.nanoTime();

            System.out.println(person.query(query).getAll());

            time = System.nanoTime() - time;

            System.out.println(i);

            warmLatencies.add(time);
        }

        for (int i = 0; i < benchIter; i++) {
            time = System.nanoTime();

            System.out.println(person.query(query).getAll());

            time = System.nanoTime() - time;

            System.out.println(i);

            latencies.add(time);
        }

        System.out.println("Warn up latencies: ");
        printLatencies(warmLatencies);

        System.out.println("\nReal latencies: ");
        printLatencies(latencies);

        client.close();
    }

    private static void printLatencies(List<Long> latencies) {
        Collections.sort(latencies);

        int size = latencies.size();

        if (size > 0) {
            System.out.println("MIN: " + latencies.get(0) / 1000000 + " ms");
            System.out.println("10%: " + latencies.get((int) (size * 0.1)) / 1000000 + " ms");
            System.out.println("20%: " + latencies.get((int) (size * 0.2)) / 1000000 + " ms");
            System.out.println("30%: " + latencies.get((int) (size * 0.3)) / 1000000 + " ms");
            System.out.println("40%: " + latencies.get((int) (size * 0.4)) / 1000000 + " ms");
            System.out.println("50%: " + latencies.get((int) (size * 0.5)) / 1000000 + " ms");
            System.out.println("60%: " + latencies.get((int) (size * 0.6)) / 1000000 + " ms");
            System.out.println("70%: " + latencies.get((int) (size * 0.7)) / 1000000 + " ms");
            System.out.println("80%: " + latencies.get((int) (size * 0.8)) / 1000000 + " ms");
            System.out.println("90%: " + latencies.get((int) (size * 0.9)) / 1000000 + " ms");
            System.out.println("95%: " + latencies.get((int) (size * 0.95)) / 1000000 + " ms");
            System.out.println("99%: " + latencies.get((int) (size * 0.99)) / 1000000 + " ms");
            System.out.println("MAX: " + latencies.get(size - 1) / 1000000 + " ms");
        }
    }
}
