package group_5.banking_system_application.Ui;

public class Beneficiary {

    private String beneficiaryId;
    private String beneficiaryName;
    private String accountNum;
    private String email;

    public Beneficiary(String beneficiaryId, String beneficiaryName, String accountNum, String email) {
        this.beneficiaryId = beneficiaryId;
        this.beneficiaryName = beneficiaryName;
        this.accountNum = accountNum;
        this.email = email;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

