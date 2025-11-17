package group_5.banking_system_application.Ui;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordAuthUtil {
    public static String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }
    public static boolean checkPassword(String password,String hashedPassword){
        return BCrypt.checkpw(password,hashedPassword);
    }
}
