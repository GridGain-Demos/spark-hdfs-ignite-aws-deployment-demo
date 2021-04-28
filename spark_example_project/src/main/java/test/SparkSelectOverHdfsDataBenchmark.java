package test;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SparkSelectOverHdfsDataBenchmark {
    public static void main(String[] args) {
        //args
        String hdfs = args[0];
        int warmupIter = Integer.valueOf(args[1]);
        int benchIter = Integer.valueOf(args[2]);
        int companyFrom = Integer.valueOf(args[3]);
        int companyTo = Integer.valueOf(args[4]);

        //latencies
        List<Long> latencies = new ArrayList<>();
        List<Long> warmLatencies = new ArrayList<>();

        //hdfs files names
        String person_data = hdfs + "/Person";
        String company_data = hdfs + "/Company";

        SparkConf sparkConf = new SparkConf()
                .setAppName("SparkSelectOverHdfsDataBenchmark");

        SparkSession session = SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();

        Dataset<Row> person, company, join;

        long time;

        //warm up iterations
        for (int i = 0; i < warmupIter; i++) {
            time = System.nanoTime();

            person = session.read()
                    .format("parquet")
                    .load(person_data);

            person = person.repartition(person.col("companyId"));

            company = session.read()
                    .format("parquet")
                    .load(company_data);

            company = company.repartition(company.col("id"));

            join = person.join(company).where(person.col("companyId").equalTo(company.col("id"))
                    .and(person.col("companyId").gt(companyFrom))
                    .and(person.col("companyId").lt(companyTo)))
                    .select(org.apache.spark.sql.functions.avg("age"));

            //show - termination operation in spark (all previous operation will be done here)
            join.show();

            time = System.nanoTime() - time;

            warmLatencies.add(time);

            //remove all cache for all dataframes
            session.catalog().clearCache();
        }


        for (int i = 0; i < benchIter; i++) {
            time = System.nanoTime();

            person = session.read()
                    .format("parquet")
                    .load(person_data);

            person = person.repartition(person.col("companyId"));

            company = session.read()
                    .format("parquet")
                    .load(company_data);

            company = company.repartition(company.col("id"));

            join = person.join(company).where(person.col("companyId").equalTo(company.col("id"))
                    .and(person.col("companyId").gt(companyFrom))
                    .and(person.col("companyId").lt(companyTo)))
                    .select(org.apache.spark.sql.functions.avg("age"));

            //show - termination operation in spark (all previous operation will be done here)
            join.show();

            time = System.nanoTime() - time;

            latencies.add(time);

            //remove all cache for all dataframes because we benchmarked reading from HDFS here
            session.catalog().clearCache();
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

