package com.campus.lostfound.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false) private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role = Role.USER;

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public String getPassword(){ return password; }
    public Role getRole(){ return role; }
    public void setId(Long id){ this.id=id; }
    public void setName(String v){ name=v; }
    public void setEmail(String v){ email=v; }
    public void setPassword(String v){ password=v; }
    public void setRole(Role v){ role=v; }
}
