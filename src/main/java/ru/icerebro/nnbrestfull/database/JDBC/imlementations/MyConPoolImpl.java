package ru.icerebro.nnbrestfull.database.JDBC.imlementations;

import org.springframework.stereotype.Component;
import ru.icerebro.nnbrestfull.database.JDBC.interfaces.MyConPool;
import ru.icerebro.nnbrestfull.entities.ConstStrings;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

@Component
public class MyConPoolImpl implements MyConPool {

    private LinkedList<Connection> freeConnections;
    private LinkedList<Connection> allConnections;


    private int maxPoolSize;
    private int minPoolSize;

    public MyConPoolImpl() {
        this(50);
    }

    public MyConPoolImpl(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.minPoolSize = maxPoolSize/10;

        freeConnections = new LinkedList<>();
        allConnections = new LinkedList<>();

        try {
            Driver driver = new net.sourceforge.jtds.jdbc.Driver();
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return connection
     * After extracting ResultSet, you must pass connection to freeConnection method
     */
    @Override
    public synchronized Connection getConnection(){
        Connection connection = null;

        //if pool have a free connection to use
        if (!freeConnections.isEmpty())
            connection = freeConnections.removeFirst();

        //open new connection or give busy one
        if (this.allConnections.size() < this.maxPoolSize){
            try {
                connection = DriverManager.getConnection(ConstStrings.URL, ConstStrings.USERNAME, ConstStrings.PASSWORD);
                allConnections.add(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            connection = allConnections.removeFirst();
            allConnections.add(connection);
        }

        return connection;
    }


    /**
     * @param connection will be marked as free or be closed if pool have min free connections
     */
    @Override
    public synchronized void freeConnection(Connection connection) {
        if (freeConnections.size() < minPoolSize)
            freeConnections.add(connection);
        else {
            try {
                allConnections.remove(connection);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
