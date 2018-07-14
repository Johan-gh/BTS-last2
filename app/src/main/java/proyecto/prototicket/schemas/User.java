package proyecto.prototicket.schemas;

/**
 * Created by JOHAN on 31/01/2018.
 */
public class User {

    private String secondary_hash;
    private  String token;

    public String getToken() {
        return token;
    }

    public void setToken(String tokken) {
        this.token = tokken;
    }

    public String getSecondaryHash() {
        return secondary_hash;
    }

    public void setSecondaryHash(String secondaryHash) {
        this.secondary_hash = secondaryHash;
    }
}