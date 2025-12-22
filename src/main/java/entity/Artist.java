package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Artist {
    @Id @GeneratedValue
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public List<Song> getBooks() {
        return songs;
    }

    @OneToMany(mappedBy = "Artist", fetch = FetchType.EAGER)
    private List<Song> songs = new ArrayList<>();

    public Artist() {}
    public Artist(String name) {
        this.name = name;
    }
}
