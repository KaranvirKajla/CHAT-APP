package Models;

public class Group {
    private String creator;
    private String name;
    private String id;
    private String imageUrl;
    private String description;
   // private String email[];


    public Group() {
    }


    public Group(String creator, String name, String id, String imageUrl, String description) {
        this.creator = creator;
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
