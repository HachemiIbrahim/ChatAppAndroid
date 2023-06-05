package Model;

public class User {
    private String Name;
    private String Username;
    private String Email;
    private String id;
    private String imageURL;

    public User(){}

    public User(String Name,String Username,String Email,String id,String imageURL){
        this.Name = Name;
        this.Username = Username;
        this.Email = Email;
        this.id = id;
        this.imageURL = imageURL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
