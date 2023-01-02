package Controller;

import Conection.BuidConection;
import DAO.AccountDAO;
import Model.Account;
import Model.CheckingAccount;
import Model.SavingsAccount;
import Util.ClearConsole;


import java.sql.Connection;
import java.util.Scanner;

public class Main {

//    static Connection con = new BuidConection().getCon();
    static AccountDAO dao = new AccountDAO();
    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {

        while(true){

            try{
                System.out.println("Banco Gringotts" +
                        "\n1 - Login" +
                        "\n2 - Abrir conta" +
                        "\n0 - Encerrar sessão");
                System.out.print("selecione uma opção: ");
                int opInicio = scan.nextInt();
                scan.nextLine();

                if(opInicio < 0 || opInicio > 2){
                    throw new ArithmeticException("Informe uma opção válida!");
                }

                switch (opInicio){
                    case 1:
                        System.out.println("Login");
                        System.out.println();
                        System.out.println("Informe o tipo da conta que deseja logar:" +
                                "\nCP - Conta Poupança" +
                                "\nCC - Conta Corrente");
                        System.out.print("Tipo: ");
                        String typeAccountAuthentication = scan.nextLine();

                        if(!(typeAccountAuthentication.equals("CP") || typeAccountAuthentication.equals("CC"))){
                            throw new RuntimeException("Informe um tipo válido de conta!");
                        }

                        System.out.print("Informe o cpf do proprietário da conta: ");
                        String cpfAccountAuthentication = scan.nextLine();

                        Account account = authentication(typeAccountAuthentication, cpfAccountAuthentication);
                        if(account != null){
                            while(true){
                                try{

                                    System.out.println("Bem vindo, " + account.getName());
                                    System.out.println("Menu:" +
                                            "\n1 - Realizar depósito" +
                                            "\n2 - Realizar saque" +
                                            "\n0 - Sair");
                                    System.out.print("Selecione uma opção: ");
                                    int op = scan.nextInt();
                                    scan.nextLine();

                                    if(op < 0 || op > 2){
                                        throw new ArithmeticException("Informe uma opção válida");
                                    }

                                    switch(op){
                                        case 1:

                                            double currentBalanceDeposit = deposit(account);
                                            System.out.println("Depósito realizado com sucesso!" +
                                                    "\n Saldo atual: R$ " + currentBalanceDeposit);
                                            break;

                                        case 2:

                                            double currentBalanceWithdraw = withdraw(account.getType());
                                            if(currentBalanceWithdraw != 0){
                                                System.out.println("Saque realizado com sucesso!" +
                                                        "\n Saldo atual: R$ " + currentBalanceWithdraw);
                                            }
                                            break;

                                        case 0:
                                            System.out.println("Usuário deslogado");
                                            op = -1;
                                            break;
                                    }

                                    if(op == -1){
                                        break;
                                    }

                                }catch (RuntimeException e){
                                    if (e.getMessage() == null) {
                                        System.out.println("O tipo do valor informado é diferente do solicitado");
                                    } else {
                                        System.out.println(e.getMessage());
                                    }
                                    break;
                                }

                            }
                        }else{
                            System.out.println("Não existe nenhuma conta com esse cpf");
                        }
                        break;

                    case 2:
                        System.out.println("Abrir conta bancária" +
                                "\n1 - Abrir conta poupança" +
                                "\n2 - Abrir conta corrente");
                        System.out.print("Selecione uma opção: ");
                        int opOpenAccount = scan.nextInt();
                        scan.nextLine();

                        if(opOpenAccount < 1 || opOpenAccount > 2){
                            throw new ArithmeticException("Informe uma opção válida");
                        }

                        switch(opOpenAccount){
                            case 1:

                                System.out.println(addSavingsAccount());
                                break;

                            case 2:

                                System.out.println(addCheckingAccount());
                                break;
                        }
                        break;

                    case 0:
                        System.out.println("Obrigado por utilizar o nosso sistema :)");
                        opInicio = -1;
                        break;
                }

                if(opInicio == -1){
                    break;
                }



            }catch(RuntimeException e){
                if (e.getMessage() == null) {
                    System.out.println("O tipo do valor informado é diferente do solicitado");
                    scan.nextLine();
                } else {
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    public static String addSavingsAccount(){

        Connection con = new BuidConection().getCon();

        System.out.println("Conta Poupança");
        System.out.println();
        System.out.print("Nome: ");
        String nameSavingsAccount = scan.nextLine();
        System.out.print("CPF: ");
        String cpfSavingsAccount = scan.nextLine();
        System.out.print("Data de nascimento: ");
        String birthDateSavingsAccount = scan.nextLine();

        SavingsAccount s = new SavingsAccount(nameSavingsAccount, cpfSavingsAccount, birthDateSavingsAccount);

        if(dao.insertSavingsAccount(s.getName(), s.getCpf(), s.getBirthDate(), s.getBalance(), s.getType(), con) != null){
            return "Conta Aberta com sucesso!";
        }else{
            throw new NullPointerException("Falha ao abrir conta!");
        }
    }

    public static String addCheckingAccount(){

        Connection con = new BuidConection().getCon();

        System.out.println("Conta Corrente");
        System.out.println();
        System.out.print("Nome: ");
        String nameCheckingAccount = scan.nextLine();
        System.out.print("CPF: ");
        String cpfCheckingAccount = scan.nextLine();
        System.out.print("Data de nascimento: ");
        String birthDateCheckingAccount = scan.nextLine();

        CheckingAccount c = new CheckingAccount(nameCheckingAccount, cpfCheckingAccount, birthDateCheckingAccount);

        if(dao.insertCheckingAccount(c.getName(), c.getCpf(), c.getBirthDate(), c.getBalance(), c.getType(), con) != null){
            return "Conta aberta com sucesso!";
        }else{
            throw new NullPointerException("Falha ao abrir conta!");
        }
    }

    public static double deposit(Account account){

        Connection con = new BuidConection().getCon();

        System.out.println("Depósito");
        System.out.println();

        System.out.print("Informe o cpf do proprietário da conta: ");
        String cpf = scan.nextLine();
        System.out.print("Informe o valor do depósito: ");
        double value = scan.nextDouble();
        scan.nextLine();

        if(value <= 0){
            throw new ArithmeticException("O valor do depósito tem que ser maior que 0!");
        }

        double currentBalance = dao.depositAccount(account.getType(), cpf, value, con);
        if(currentBalance != 0){
            return currentBalance;
        }else{
            throw new ArithmeticException("Falha ao realizar depósito!");
        }
    }

    public static double withdraw(String type){

        Connection con = new BuidConection().getCon();

        System.out.println("Saque");
        System.out.println();

        System.out.print("Informe o cpf do proprietário da conta: ");
        String cpf = scan.nextLine();
        System.out.print("Informe o valor do saque: ");
        double value = scan.nextDouble();
        scan.nextLine();

        double currentBalance = dao.withdrawAccount(type, cpf, value, con);
        if(currentBalance != 0){
            return currentBalance;
        }else{
            throw new ArithmeticException("Falha ao realizar saque!");
        }
    }

    public static Account authentication(String type, String cpf){

        Connection con = new BuidConection().getCon();
        Account account = dao.validationAccount(type, cpf, con);
        if(account != null){
            return account;
        }else{
            if(type.equals("CP")){
                throw new RuntimeException("Não existe uma conta poupança com esse cpf");
            }else if(type.equals("CC")){
                throw new RuntimeException("Não existe uma conta corrente com esse cpf");
            }
            return null;

        }
    }
}
