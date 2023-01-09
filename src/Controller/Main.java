package Controller;


import DAO.AccountDAO;
import Model.Account;
import Model.CheckingAccount;
import Model.Credit;
import Model.SavingsAccount;


import java.util.Scanner;

public class Main {

    static AccountDAO dao = new AccountDAO();
    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {
        int opInicio = -1;
        do{
            try{
                System.out.println("Banco Gringotts" +
                        "\n1 - Login" +
                        "\n2 - Abrir conta" +
                        "\n0 - Sair");
                System.out.print("selecione uma opção: ");
                opInicio = Integer.parseInt(scan.nextLine());
//                scan.nextLine();

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

                            int op = -1;
                            do{
                                try{

                                    System.out.println("Bem vindo, " + account.getName() + "\n");
                                    System.out.print("Menu:" +
                                            "\n1 - Realizar depósito" +
                                            "\n2 - Realizar saque");
                                    if(account.getType().equals("CC")){
                                        System.out.print("\n3 - Solicitar crédito" +
                                                "\n4 - Pagar emprestimo");
                                    }
                                    System.out.println("\n0 - Logout");
                                    System.out.print("Selecione uma opção: ");
                                    op = Integer.parseInt(scan.nextLine());

                                    if(account.getType().equals("CC")){
                                        if(op < 0 || op > 4){
                                            throw new ArithmeticException("Informe uma opção válida");
                                        }
                                    }else{
                                        if(op < 0 || op > 2){
                                            throw new ArithmeticException("Informe uma opção válida");
                                        }
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

                                        case 3:
                                            double creditValue = credit();
                                            System.out.println("Empréstimo realizado com sucesso!");
                                            System.out.println("Limite utilizado: R$ "
                                             + creditValue + "/10000.00");
                                            break;

                                        case 4:
                                            System.out.println("Pagar emprestimo\n");
                                            System.out.println("Débito atual: R$ " + showDebitAccount(account.getCpf()) + "\n");

                                            System.out.print("Informe o cpf do proprietário da conta: ");
                                            String cpf = scan.nextLine();

                                            System.out.print("informe o valor que deseja abater da dívida: ");
                                            double value = Double.parseDouble(scan.nextLine());

                                            System.out.println("Pagamento realizado com sucesso" +
                                                    "\nDébito restante: R$ " + payDebitAccount(cpf, value));
                                    }

                                }catch (RuntimeException e){
                                    if (e.getMessage() == null) {
                                        System.out.println("O tipo do valor informado é diferente do solicitado!");
//                                        break;

                                    } else {
                                        System.out.println("Erro: " + e.getMessage());
                                    }
//                                    break;
                                }

                            }while(op != 0);

                            System.out.println("Usuário deslogado");

                        }else{
                            System.out.println("Não existe nenhuma conta com esse cpf");
                        }
                        break;

                    case 2:

                        try{
                            System.out.println("Abrir conta bancária\n" +
                                    "\nCP - Abrir conta poupança" +
                                    "\nCC - Abrir conta corrente");
                            System.out.print("Selecione uma opção: ");
                            String opOpenAccount = scan.nextLine();

                            if((!opOpenAccount.equals("CP")) && (!opOpenAccount.equals("CC"))){
                                throw new ArithmeticException("Informe uma opção válida");
                            }

                            switch(opOpenAccount){
                                case "CP":

                                    System.out.println(addSavingsAccount());
                                    break;

                                case "CC":

                                    System.out.println(addCheckingAccount());
                            }
                            break;

                        }catch(RuntimeException e){
                            if(e.getMessage() == null){
                                System.out.println("O tipo do valor informado é diferente do solicitado!");
                                break;
                            }else{
                                System.out.println("Erro: " + e.getMessage());
                            }
                            break;
                        }
                }



            }catch(RuntimeException e){
                if (e.getMessage() == null) {
                    System.out.println("O tipo do valor informado é diferente do solicitado!");
//                    break;
                } else {
                    System.out.println(e.getMessage());
                }
            }
        }while (opInicio != 0);

        System.out.println("Obrigado por utilizar o nosso sistema :)");

    }

    public static String addSavingsAccount(){

        System.out.println("Conta Poupança\n");
        System.out.print("Nome: ");
        String nameSavingsAccount = scan.nextLine();
        System.out.print("CPF: ");
        String cpfSavingsAccount = scan.nextLine();
        System.out.print("Data de nascimento: ");
        String birthDateSavingsAccount = scan.nextLine();

        SavingsAccount s = new SavingsAccount(nameSavingsAccount, cpfSavingsAccount, birthDateSavingsAccount);

        if(dao.insertSavingsAccount(s.getName(), s.getCpf(), s.getBirthDate(), s.getBalance(), s.getType()) != null){
            return "Conta Aberta com sucesso!";
        }else{
            throw new NullPointerException("Falha ao abrir conta!");
        }
    }

    public static String addCheckingAccount(){

        System.out.println("Conta Corrente\n");
        System.out.print("Nome: ");
        String nameCheckingAccount = scan.nextLine();
        System.out.print("CPF: ");
        String cpfCheckingAccount = scan.nextLine();
        System.out.print("Data de nascimento: ");
        String birthDateCheckingAccount = scan.nextLine();

        CheckingAccount c = new CheckingAccount(nameCheckingAccount, cpfCheckingAccount, birthDateCheckingAccount);

        if(dao.insertCheckingAccount(c.getName(), c.getCpf(), c.getBirthDate(), c.getBalance(), c.getType()) != null){
            return "Conta aberta com sucesso!";
        }else{
            throw new NullPointerException("Falha ao abrir conta!");
        }
    }

    public static double deposit(Account account){

        System.out.println("Depósito");
        System.out.println();

        System.out.print("Informe o cpf do proprietário da conta: ");
        String cpf = scan.nextLine();
        System.out.print("Informe o valor do depósito: ");
        double value = Double.parseDouble(scan.nextLine());

        if(value <= 0){
            throw new ArithmeticException("O valor do depósito tem que ser maior que 0!");
        }

        double currentBalance = dao.depositAccount(account.getType(), cpf, value);
        if(currentBalance != 0){
            return currentBalance;
        }else{
            throw new ArithmeticException("Falha ao realizar depósito!");
        }
    }

    public static double withdraw(String type){

        System.out.println("Saque");
        System.out.println();

        System.out.print("Informe o cpf do proprietário da conta: ");
        String cpf = scan.nextLine();
        System.out.print("Informe o valor do saque: ");
        double value = Double.parseDouble(scan.nextLine());

        double currentBalance = dao.withdrawAccount(type, cpf, value);
        if(currentBalance != 0){
            return currentBalance;
        }else{
            throw new ArithmeticException("Falha ao realizar saque!");
        }
    }

    public static Account authentication(String type, String cpf){

        Account account = dao.validationAccount(type, cpf);
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

    public static double credit(){

        System.out.println("Sistema de liberação de crédito\n");

        System.out.print("Informe o cpf do proprietário da conta: ");
        String cpf = scan.nextLine();
        System.out.print("Valor do cŕedito: ");
        double value = Double.parseDouble(scan.nextLine());
        Credit c = new Credit(cpf, value);

        if(value <= 0){
            throw new ArithmeticException("O valor do crédito tem que ser maior que 0!");
        }

        double creditValue = dao.getCredit(c.getCpf(), c.getCredit());
        if(creditValue != 0){
            return creditValue;
        }else{
            throw new RuntimeException("Não foi possível obter cŕedito");
        }
    }

    public static double showDebitAccount(String cpf){
        double debitValue = dao.showDebit(cpf);
        if(debitValue != -1){
            return debitValue;
        }else{
            throw new RuntimeException("Houve um erro inesperado. Não foi possível verificar o seu débito");
        }
    }

    public static double payDebitAccount(String cpf, double value){

        if(value <= 0){
            throw new ArithmeticException("O valor do depósito tem que ser maior que 0!");
        }

        double debitValue = dao.payDebit(cpf, value);
        if(debitValue != -1){
            return debitValue;
        }else{
            throw new RuntimeException("Houve um erro inesperado. Não foi possível realizar o pagamento do seu débito");
        }
    }
}
