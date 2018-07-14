package proyecto.prototicket.schemas;

/**
 * Created by JOHAN on 31/01/2018.
 */

public class LoginRetrofit {
    private String username,password;

    public LoginRetrofit(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsuario() {
        return username;
    }

    public void setUsuario(String username) {
        this.username = username;
    }

    public String getPass() {
        return password;
    }

    public void setPass(String password) {
        this.password = password;
    }
}
