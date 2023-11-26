package org.task2.task23.service;

import com.github.javafaker.Faker;
import org.apache.commons.dbutils.DbUtils;
import org.task2.util.DbProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class InitializerImpl implements Initializer {

    public final static int TABLE_I_COUNT_BY_DEFAULT = 30;
    public final static int CUSTOMER_COUNT_BY_DEFAULT = 400;
    private int tableICount;
    private int customerCount;

    private Connection connection = null;
    private final Random random = new Random();
    private final Faker faker = new Faker();

    static private final DbProperties dbProperties
            = DbProperties.loadProperties("db.properties");

    @Override
    public void validateData() throws SQLException {

        try {
            connection = dbProperties.getConnection();
            if (!doesTableExist("customer")) {
                createTableCustomer();
            }
            validateTablesI();
            if (!doesTableExist("table_many")) {
                createTableMany();
            }

            validateTableCustomerData();

        }
        finally {
            DbUtils.closeQuietly(connection);
        }
    }

    private boolean doesTableExist(String tableName) throws SQLException {
        PreparedStatement sqlStatement = null;
        ResultSet resultSet = null;
        try {
            sqlStatement = connection.prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ?)"
            );
            sqlStatement.setString(1, tableName);
            resultSet = sqlStatement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);

        } catch (SQLException e) {
            DbUtils.closeQuietly(sqlStatement);
            DbUtils.closeQuietly(resultSet);
            throw e;
        }
    }
    private void validateTablesI() throws SQLException{
        tableICount = 0;
        while (doesTableExist("table_" + tableICount)) {
            tableICount++;
        }
        while(tableICount < TABLE_I_COUNT_BY_DEFAULT) {
            createTableI(tableICount++);
        }
    }

    private void createTableCustomer() throws SQLException{

        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = connection.prepareStatement(
                            """
                CREATE TABLE customer (
                    customer_id VARCHAR(200) NOT NULL PRIMARY KEY,
                    col_1 INTEGER,
                    col_2 INTEGER,
                    col_3 INTEGER,
                    col_4 INTEGER,
                    col_5 INTEGER
                )""");
            sqlStatement.execute();

        } catch (SQLException e) {
            DbUtils.closeQuietly(sqlStatement);
            throw e;
        }
    }

    private void createTableI(int i) throws SQLException{

        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = connection.prepareStatement(
                    "CREATE TABLE table_" +  i +
                            """
                                    (   customer_id VARCHAR(200) NOT NULL PRIMARY KEY REFERENCES customer (customer_id),
                                        col_1 INTEGER,
                                        col_2 INTEGER,
                                        col_3 INTEGER,
                                        col_4 INTEGER,
                                        col_5 INTEGER
                                    )
                                    """);
            sqlStatement.execute();

        } catch (SQLException e) {
            DbUtils.closeQuietly(sqlStatement);
            throw e;
        }
    }

    private void createTableMany() throws SQLException{

        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = connection.prepareStatement("""
                CREATE TABLE table_many (
                    customer_id VARCHAR(200) NOT NULL REFERENCES customer (customer_id),
                    groupId INT NOT NULL
                )""");
            sqlStatement.execute();

            sqlStatement = connection.prepareStatement("""
                    ALTER TABLE table_many
                                        ADD CONSTRAINT unique_customer_group
                                        UNIQUE (customer_id, groupId)
                """);
            sqlStatement.execute();
        } catch (SQLException e) {
            DbUtils.closeQuietly(sqlStatement);
            throw e;
        }
    }

    private void validateTableCustomerData() throws SQLException{
        PreparedStatement sqlStatement = null;
        ResultSet resultSet = null;
        try {
            sqlStatement = connection.prepareStatement(
                    "SELECT COUNT(*) as row_count  FROM Customer"
            );
            resultSet = sqlStatement.executeQuery();
            resultSet.next();
            customerCount = resultSet.getInt(1);

            while (customerCount++ < CUSTOMER_COUNT_BY_DEFAULT) {
                generateCustomer();
            }

        } catch (SQLException e) {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(sqlStatement);
            throw e;
        }

    }

    /** @return value of the generated customer_id.
     */
    private String generateCustomer() throws SQLException {
        PreparedStatement sqlStatement = null;

        try {
            sqlStatement = connection.prepareStatement("""
                INSERT INTO Customer (customer_id, col_1, col_2, col_3, col_4, col_5)
                            VALUES (?, ?, ?, ?, ?, ?)
            """);
            String customerId = generateCustomerId();
            sqlStatement.setString(1, customerId);
            for (int i = 2; i < 7; i++) {
                sqlStatement.setInt(i, random.nextInt());
            }
            sqlStatement.execute();
            return customerId;

        } catch (SQLException e) {
            DbUtils.closeQuietly(sqlStatement);
            throw e;
        }

    }

    private String generateCustomerId() throws SQLException {

        PreparedStatement sqlStatement = null;
        ResultSet resultSet = null;
        String name = null;
        try {
            sqlStatement = connection.prepareStatement(
                    "SELECT COUNT(*) as row_count FROM Customer WHERE customer_id=?"
            );
            do {
                name = faker.harryPotter().character() + random.nextInt();
                sqlStatement.setString(1, name);
                resultSet = sqlStatement.executeQuery();
                resultSet.next();
            }
            while (resultSet.getInt(1) > 0);
            return name;

        } catch (SQLException e) {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(sqlStatement);
            throw e;
        }

    }

}
