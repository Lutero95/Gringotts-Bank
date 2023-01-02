package Model;

import java.sql.Date;

public class CheckingAccount extends Account{

    public CheckingAccount(String name, String cpf, String birthDate){
        this.setName(name);
        this.setCpf(cpf);
        this.setBirthDate(birthDate);
        this.setBalance(0);
        this.setType("CC");
    }
}
