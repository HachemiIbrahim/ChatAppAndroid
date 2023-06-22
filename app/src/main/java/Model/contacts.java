package Model;

public class contacts {
    private String Username,imageURL ;

    public contacts() {
    }

    public contacts(String Username, String imageURL) {
        this.Username = Username;
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {        this.Username = Username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
