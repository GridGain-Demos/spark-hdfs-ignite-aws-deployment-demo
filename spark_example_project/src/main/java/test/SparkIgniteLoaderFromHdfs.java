package test;

import org.apache.ignite.spark.IgniteDataFrameSettings;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.SaveMode.Append;

public class SparkIgniteLoaderFromHdfs {
    public static void main(String[] args) {
        //args
        String hdfs = args[0];
        String clientCfg = args[1];

        //hdfs files names
        String person_data = hdfs + "/Person";
        String company_data = hdfs + "/Company";

        SparkConf sparkConf = new SparkConf()
                .setAppName("SparkIgniteLoaderFromHdfs");

        SparkSession session = SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();

        Dataset<Row> person, company;

        person = session.read()
                .format("parquet")
                .load(person_data);

        DataFrameWriter< Row > df1 = person
                .write()
                .format(IgniteDataFrameSettings.FORMAT_IGNITE())
                .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), clientCfg)
                .option(IgniteDataFrameSettings.OPTION_TABLE(), "Person")
                .mode(Append);

        df1.save();

        company = session.read()
                .format("parquet")
                .load(company_data);

        DataFrameWriter< Row > df2 = company
                .write()
                .format(IgniteDataFrameSettings.FORMAT_IGNITE())
                .option(IgniteDataFrameSettings.OPTION_CONFIG_FILE(), clientCfg)
                .option(IgniteDataFrameSettings.OPTION_TABLE(), "Company")
                .mode(Append);

        df2.save();

        session.close();
    }
}