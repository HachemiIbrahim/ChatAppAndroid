package Model;

public class Group {
    private String GroupName;
    private String imageURL;

    public Group() {
    }

    public Group(String GroupName, String imageURL) {
        this.GroupName = GroupName;
        this.imageURL = imageURL;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
