package DAO;

import Conection.BuidConection;
import Model.Account;
import Model.CheckingAccount;
import Model.SavingsAccount;

import java.sql.*;

public class AccountDAO {
    public Integer insertSavingsAccount(String name, String cpf, String birthDate, double balance, String type){
        Connection con = new BuidConection().getCon();

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
                throw new SQLException("Não é possível cadastrar mais de uma conta poupança com o mesmo cpf");
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

    public Integer insertCheckingAccount(String name, String cpf, String birthDate, double balance, String type){

        Connection con = new BuidConection().getCon();

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
                throw new SQLException("Não é possível cadastrar mais de uma conta corrente com o mesmo cpf");
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

    public double depositAccount(String type, String cpf, double value){

        Connection con = new BuidConection().getCon();

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
            if(!e.getMessage().equals("No results were returned by the query.")){
                System.out.println(e.getMessage());
            }
            return currentBalance;
        }
    }

    public double withdrawAccount(String type, String cpf, double value){

        Connection con = new BuidConection().getCon();

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
            if(!e.getMessage().equals("No results were returned by the query.")){
                System.out.println(e.getMessage());
            }
            return currentBalance;
        }
    }

    public Account validationAccount(String type, String cpf){

        Connection con = new BuidConection().getCon();

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
            if(!e.getMessage().equals("No results were returned by the query.")){
                System.out.println(e.getMessage());
            }
            return account;
        }
    }

    public Double getCredit(String cpf, double value){
        Connection con = new BuidConection().getCon();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer idClient = null;
        double credit = 0;

        String sqlValidation = "select id from checking_account where cpf = ?";
        String sqlSelect = "select debit_balance from credit_checking_account where cpf_account = ?";
        String sqlInsert = "insert into credit_checking_account(cpf_account, debit_balance)" +
                "values(?,?) returning debit_balance";
        try{
            ps = con.prepareStatement(sqlValidation);

            if(cpf.length() != 11){
                throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
            }else{
                ps.setString(1, cpf);
            }

            rs = ps.executeQuery();

            if(rs.next()){
                idClient = rs.getInt("id");
            }

            ps.close();

            if(idClient == null){
                throw new SQLException("Não existe nenhuma conta corrente registrada com esse cpf!");
            }else{
                ps = con.prepareStatement(sqlSelect);

                ps.setString(1, cpf);

                rs = ps.executeQuery();
                if(rs != null){
                    if(rs.next()){
                        credit = rs.getDouble("debit_balance");
                    }

                    ps.close();

                }
                ps = con.prepareStatement(sqlInsert);

                if((credit + value) > 10000){
                    throw new ArithmeticException("Limite máximo de crédito atingido. Pague sua fatura ou tente solicitar um valor menor");
                }else{
                    ps.setString(1, cpf);
                    ps.setDouble(2, value);

                    rs = ps.executeQuery();

                    if(rs.next()){
                        credit = rs.getDouble("debit_balance");
                    }

                    ps.close();
                    con.close();
                }
            }

            return credit;

        }catch(Exception e){
            if(!e.getMessage().equals("No results were returned by the query.")){
                System.out.println(e.getMessage());
            }
            return credit;
        }
    }

    public double showDebit(String cpf){

        Connection con = new BuidConection().getCon();
        PreparedStatement ps = null;
        ResultSet rs = null;
        double debit = 0;

        String sqlSelect = "select debit_balance from credit_checking_account where cpf_account = ?";

        try {
            ps = con.prepareStatement(sqlSelect);

            ps.setString(1, cpf);

            rs = ps.executeQuery();

            if(rs.next()){
                debit = rs.getDouble("debit_balance");
            }

            ps.close();
            con.close();

            return debit;
        }catch (Exception e){
            if(!e.getMessage().equals("No results were returned by the query.")){
                System.out.println(e.getMessage());
            }
            debit = -1;
            return debit;
        }

    }
    public double payDebit(String cpf, double value){
        Connection con = new BuidConection().getCon();
        PreparedStatement ps = null;
        ResultSet rs = null;
        double balance = 0;
        double debit = 0;

        String sqlSelect = "select debit_balance db, balance b from credit_checking_account " +
                "inner join checking_account on cpf_account = cpf where cpf_account = ?";
        String sqlUpdateCredit = "update credit_checking_account set debit_balance = ? where cpf_account = ? returning debit_balance";
        String sqlUpdateAccount = "update checking_account set balance = ? where cpf = ?";

        try {

            ps = con.prepareStatement(sqlSelect);
            if(cpf.length() != 11){
                throw new ArithmeticException("O CPF precisa ser composto de 11 números!");
            }else{
                ps.setString(1, cpf);
            }

            rs = ps.executeQuery();

            if(rs.next()){
                balance = rs.getDouble("b");
                debit = rs.getDouble("db");
            }

            ps.close();

            ps = con.prepareStatement(sqlUpdateCredit);

            if(value > balance){
                throw new ArithmeticException("Vocẽ não possuí saldo suficiente para realizar o pagamento");
            }else{
                balance = balance - value;
            }

            if(debit < value){
                throw new ArithmeticException("O valor do pagamento não pode ser superior a débito existente");
            }else{
                debit = debit - value;
            }

            ps.setDouble(1, debit);
            ps.setString(2, cpf);

            rs = ps.executeQuery();

            if(rs.next()){
                debit = rs.getDouble(("debit_balance"));
            }

            ps.close();

            ps = con.prepareStatement(sqlUpdateAccount);

            ps.setDouble(1, balance);
            ps.setString(2, cpf);

            ps.executeQuery();
            System.out.println("chegou aqui");

            ps.close();
            con.close();

            return debit;

        }catch(Exception e){
            if(!e.getMessage().equals("No results were returned by the query.")){
                System.out.println(e.getMessage());
                debit = -1;
            }
            System.out.println(e.getMessage());
            return debit;
        }
    }
}
