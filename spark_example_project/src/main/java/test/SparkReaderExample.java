package test;

import org.apache.ignite.spark.IgniteDataFrameSettings;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SparkReaderExample {
    public static void main(String[] args) {
        String configPath = args[0];
        int warmupIter = Integer.valueOf(args[1]);
        int benchIter = Integer.valueOf(args[2]);
        int companyFrom = Integer.valueOf(args[3]);
        int companyTo = Integer.valueOf(args[4]);

        List<Long> latencies = new ArrayList<>();
        List<Long> warmLatencies = new ArrayList<>();

        SparkSession session = SparkSession.builder()
                .getOrCreate();

        long time;

        //initial sql request will also contains the Ignite node start up time
        Dataset<Row> person = session.read()
                .format(IgniteDataFrameSettings.FORMAT_IGNITE()) //Data source type.
                .option(IgniteDataFrameSettings.OPTION_TABLE(), "person") //Table to read.
                .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), configPath) //Ignite config.
                .load();

        Dataset<Row> company = session.read()
                .format(IgniteDataFrameSettings.FORMAT_IGNITE()) //Data source type.
                .option(IgniteDataFrameSettings.OPTION_TABLE(), "company") //Table to read.
                .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), configPath) //Ignite config.
                .load();

        Dataset<Row> join = person.join(company).where(person.col("companyId").equalTo(company.col("id"))
                .and(person.col("companyId").gt(companyFrom))
                .and(person.col("companyId").lt(companyTo)))
                .select(org.apache.spark.sql.functions.avg(person.col("age")));

        time = System.nanoTime();

        //show - termination operation in spark (all previous operation will be done here)
        join.show();

        System.out.println("Initial query + start time: " + (System.nanoTime() - time) / 1000000 + " ms.");

        //warm up iterations
        for (int i = 0; i < warmupIter; i++) {
            person = session.read()
                    .format(IgniteDataFrameSettings.FORMAT_IGNITE()) //Data source type.
                    .option(IgniteDataFrameSettings.OPTION_TABLE(), "person") //Table to read.
                    .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), configPath) //Ignite config.
                    .load();

            company = session.read()
                    .format(IgniteDataFrameSettings.FORMAT_IGNITE()) //Data source type.
                    .option(IgniteDataFrameSettings.OPTION_TABLE(), "company") //Table to read.
                    .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), configPath) //Ignite config.
                    .load();

            join = person.join(company).where(person.col("companyId").equalTo(company.col("id"))
                    .and(person.col("companyId").gt(companyFrom))
                    .and(person.col("companyId").lt(companyTo)))
                    .select(org.apache.spark.sql.functions.avg(person.col("age")));

            time = System.nanoTime();

            join.show();

            time = System.nanoTime() - time;

            warmLatencies.add(time);
        }


        for (int i = 0; i < benchIter; i++) {
            person = session.read()
                    .format(IgniteDataFrameSettings.FORMAT_IGNITE()) //Data source type.
                    .option(IgniteDataFrameSettings.OPTION_TABLE(), "person") //Table to read.
                    .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), configPath) //Ignite config.
                    .load();

            company = session.read()
                    .format(IgniteDataFrameSettings.FORMAT_IGNITE()) //Data source type.
                    .option(IgniteDataFrameSettings.OPTION_TABLE(), "company") //Table to read.
                    .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), configPath) //Ignite config.
                    .load();

            join = person.join(company).where(person.col("companyId").equalTo(company.col("id"))
                    .and(person.col("companyId").gt(companyFrom))
                    .and(person.col("companyId").lt(companyTo)))
                    .select(org.apache.spark.sql.functions.avg(person.col("age")));

            time = System.nanoTime();

            join.show();

            time = System.nanoTime() - time;

            latencies.add(time);
        }

        System.out.println("Warn up latencies: ");
        printLatencies(warmLatencies);

        System.out.println("\nReal latencies: ");
        printLatencies(latencies);

        session.close();

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
