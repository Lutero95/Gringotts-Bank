package DAO;

import Model.Account;
import Model.CheckingAccount;
import Model.SavingsAccount;

import java.sql.*;
import java.util.concurrent.ExecutionException;


public class AccountDAO {
    public Integer insertSavingsAccount(String name, String cpf, String birthDate, double balance, String type, Connection con){
        Integer idClient = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer validationCpf = null;

        String sqlSelect = "select id from savings_account where cpf = ?";
        String sqlInsert = "insert into savings_account(client_name, cpf, birthDate, balance, type_account) values(?,?,?,?,?) returning id";

        try{
            ps = con.prepareStatement(sqlSelect);

            if(cpf.length() != 11){
                throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
            }else{
                ps.setString(1, cpf);
            }

            rs = ps.executeQuery();

            if(rs.next()){
                validationCpf = rs.getInt("id");
            }

            if(validationCpf != null){
                throw new SQLException("Não é possível cadastrar duas contas poupança com o mesmo cpf");
            }

            ps.close();

            ps = con.prepareStatement(sqlInsert);

            ps.setString(1, name);
            ps.setString(2, cpf);

            if(birthDate.length() != 10){
                throw new SQLException("A data de nascimento precisa ser composta de" +
                        " 10 caracteres, incluindo as barras de separação. Ex: 12/04/2003");
            }else{
                ps.setString(3, birthDate);
            }

            ps.setDouble(4, balance);
            ps.setString(5, type);


            rs = ps.executeQuery();

            if (rs.next()) {
                idClient = rs.getInt("id");
            }

            ps.close();
            con.close();
            return idClient;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return idClient;
        }
    }

    public Integer insertCheckingAccount(String name, String cpf, String birthDate, double balance, String type, Connection con){

        Integer idClient = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer validationCpf = null;

        String sqlSelect = "select id from checking_account where cpf = ?";
        String sqlInsert = "insert into checking_account(client_name, cpf, birthDate, balance, type_account) values(?,?,?,?,?) returning id";

        try{
            ps = con.prepareStatement(sqlSelect);

            if(cpf.length() != 11){
                throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
            }else{
                ps.setString(1, cpf);
            }

            rs = ps.executeQuery();

            if(rs.next()){
                validationCpf = rs.getInt("id");
            }

            if(validationCpf != null){
                throw new SQLException("Não é possível cadastrar duas contas corrente com o mesmo cpf");
            }

            ps.close();

            ps = con.prepareStatement(sqlInsert);

            ps.setString(1, name);
            ps.setString(2, cpf);

            if(birthDate.length() != 10){
                throw new SQLException("A data de nascimento precisa ser composta de" +
                        " 10 caracteres, incluindo as barras de separação. Ex: 12/04/2003");
            }else{
                ps.setString(3, birthDate);
            }

            ps.setDouble(4, balance);
            ps.setString(5, type);


            rs = ps.executeQuery();

            if (rs.next()) {
                idClient = rs.getInt("id");
            }

            ps.close();
            con.close();
            return idClient;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return idClient;
        }
    }

    public double depositAccount(String type, String cpf, double value, Connection con){

        double currentBalance = 0;
        PreparedStatement ps = null;
        String sqlUpdate1 = "update savings_account set balance = ? where cpf = ?";
        String sqlSelect1 = "select balance from savings_account where cpf = ?";
        String sqlUpdate2 = "update checking_account set balance = ? where cpf = ?";
        String sqlSelect2 = "select balance from checking_account where cpf = ?";
        ResultSet rs = null;

        try{

            if(type.equals("CP")){


                ps = con.prepareStatement(sqlSelect1);
                if(cpf.length() != 11){
                    throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
                }else{
                    ps.setString(1, cpf);
                }
                System.out.println("Chegou aqui");
                rs = ps.executeQuery();

                double balanceSavings = 0;
                if (rs.next()) {
                    balanceSavings = rs.getDouble("balance");
                }

                ps.close();

                ps = con.prepareStatement(sqlUpdate1);

                currentBalance = balanceSavings + value;
                ps.setDouble(1, balanceSavings + value);

                ps.setString(2, cpf);

            }else if(type.equals("CC")){
                ps = con.prepareStatement(sqlSelect2);
                if(cpf.length() != 11){
                    throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
                }else{
                    ps.setString(1, cpf);
                }

                rs = ps.executeQuery();

                double balanceCheckings = 0;
                if (rs.next()) {
                    balanceCheckings = rs.getDouble("balance");
                }

                ps.close();

                ps = con.prepareStatement(sqlUpdate2);

                currentBalance = balanceCheckings + value;
                ps.setDouble(1, currentBalance);

                ps.setString(2, cpf);

            }

            ps.executeQuery();

            ps.close();
            con.close();

            return currentBalance;
        }catch(Exception e){
            if(!e.getMessage().equals("Nenhum resultado foi retornado pela consulta.")){
                System.out.println(e.getMessage());
            }
            return currentBalance;
        }
    }

    public double withdrawAccount(String type, String cpf, double value, Connection con){

        double currentBalance = 0;

        PreparedStatement ps = null;
        String sqlUpdate1 = "update savings_account set balance = ? where cpf = ?";
        String sqlSelect1 = "select balance from savings_account where cpf = ?";
        String sqlUpdate2 = "update checking_account set balance = ? where cpf = ?";
        String sqlSelect2 = "select balance from checking_account where cpf = ?";
        ResultSet rs = null;

        try{
            if(type.equals("CP")){
                ps = con.prepareStatement(sqlSelect1);
                if(cpf.length() != 11){
                    throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
                }else{
                    ps.setString(1, cpf);
                }

                rs = ps.executeQuery();

                double balanceSavings = 0;
                if (rs.next()) {
                    balanceSavings = rs.getDouble("balance");
                }

                ps.close();

                ps = con.prepareStatement(sqlUpdate1);

                if(balanceSavings < value){
                    throw new ArithmeticException("O valor do saque não pode ser maior que o saldo!");
                }

                currentBalance = balanceSavings - value;
                ps.setDouble(1, currentBalance);

                ps.setString(2, cpf);

            }else if(type.equals("CC")){
                ps = con.prepareStatement(sqlSelect2);
                if(cpf.length() != 11){
                    throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
                }else{
                    ps.setString(1, cpf);
                }

                rs = ps.executeQuery();

                double balanceCheckings = 0;
                if (rs.next()) {
                    balanceCheckings = rs.getDouble("balance");
                }

                ps.close();

                ps = con.prepareStatement(sqlUpdate2);

                if(balanceCheckings < value){
                    throw new ArithmeticException("O valor do saque não pode ser maior que o saldo!");
                }

                currentBalance = balanceCheckings - value;
                ps.setDouble(1, currentBalance);

                ps.setString(2, cpf);

            }

            ps.executeQuery();

            ps.close();
            con.close();

            return currentBalance;
        }catch(Exception e){
            if(!e.getMessage().equals("Nenhum resultado foi retornado pela consulta.")){
                System.out.println(e.getMessage());
            }
            return currentBalance;
        }
    }

    public Account validationAccount(String type, String cpf, Connection con){
        Integer idClient = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlSelect1 = "select client_name, cpf, birthdate from savings_account where cpf = ?";
        String sqlSelect2 = "select client_name, cpf, birthdate from checking_account where cpf = ?";
        Account account = null;

        try{
            if(type.equals("CP")){
                ps = con.prepareStatement(sqlSelect1);

                if(cpf.length() != 11){
                    throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
                }else{
                    ps.setString(1, cpf);
                }

                rs = ps.executeQuery();

                if(rs.next()){
                    account = new SavingsAccount(rs.getString("client_name"), rs.getString("cpf"), rs.getString("birthdate"));
                }

                ps.close();
            }else if(type.equals("CC")){
                ps = con.prepareStatement(sqlSelect2);
                if(cpf.length() != 11){
                    throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
                }else{
                    ps.setString(1, cpf);
                }

                rs = ps.executeQuery();

                if(rs.next()){
                    account = new CheckingAccount(rs.getString("client_name"), rs.getString("cpf"), rs.getString("birthdate"));
                }

                ps.close();
            }

            con.close();
            return account;

        }catch(Exception e){
            if(!e.getMessage().equals("Nenhum resultado foi retornado pela consulta.")){
                System.out.println(e.getMessage());
            }
            return account;
        }
    }
}
