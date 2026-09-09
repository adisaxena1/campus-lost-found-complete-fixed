package com.campus.lostfound.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="items")
public class Item {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String title;
    @Column(length=1500) private String description;
    @Column(nullable=false) private String category;
    @Column(nullable=false) private String location;
    @Column(nullable=false) private LocalDate date;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ItemStatus status = ItemStatus.ACTIVE;

    @ManyToOne(optional=false)
    private User reporter;

    public Long getId(){return id;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getCategory(){return category;}
    public String getLocation(){return location;}
    public LocalDate getDate(){return date;}
    public String getImageUrl(){return imageUrl;}
    public ItemType getType(){return type;}
    public ItemStatus getStatus(){return status;}
    public User getReporter(){return reporter;}
    public void setTitle(String v){title=v;}
    public void setDescription(String v){description=v;}
    public void setCategory(String v){category=v;}
    public void setLocation(String v){location=v;}
    public void setDate(LocalDate v){date=v;}
    public void setImageUrl(String v){imageUrl=v;}
    public void setType(ItemType v){type=v;}
    public void setStatus(ItemStatus v){status=v;}
    public void setReporter(User v){reporter=v;}
}
