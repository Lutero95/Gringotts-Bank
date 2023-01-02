package Conection;

import Model.SavingsAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class BuidConection {
    private String url;
    private String user;
    private String password;
    private Connection con;


    public BuidConection(){
        url = "jdbc:postgresql://localhost:5432/bank";
        user = "postgres";
        password = "12345";


        try {
            con = DriverManager.getConnection(url, user, password);
//            System.out.println("Conexão com banco realizada com sucesso!");

        }catch (Exception e) {
            System.out.println("Erro ao realizar a conexão com o banco!");
            System.out.println(e.getMessage());
        }

    }

    public Connection getCon(){
        return this.con;
    }
}
