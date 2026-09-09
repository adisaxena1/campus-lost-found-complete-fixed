package com.campus.lostfound.controller;

import com.campus.lostfound.model.*;
import com.campus.lostfound.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemRepository items;
    private final UserRepository users;

    public ItemController(ItemRepository items, UserRepository users){this.items=items;this.users=users;}

    record ItemRequest(String title,String description,String category,String location,LocalDate date,String imageUrl,ItemType type){}

    @GetMapping
    public List<Map<String,Object>> all(@RequestParam(required=false) String type,
                                        @RequestParam(required=false) String category,
                                        @RequestParam(required=false) String search){
        return items.findAllByOrderByDateDesc().stream()
            .filter(i->type==null || i.getType().name().equalsIgnoreCase(type))
            .filter(i->category==null || i.getCategory().equalsIgnoreCase(category))
            .filter(i->search==null || (i.getTitle()+" "+i.getDescription()+" "+i.getLocation()).toLowerCase().contains(search.toLowerCase()))
            .map(this::view).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return items.findById(id).<ResponseEntity<?>>map(i->ResponseEntity.ok(view(i)))
            .orElseGet(()->ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ItemRequest r, Authentication auth){
        User u=users.findByEmail(auth.getName()).orElseThrow();
        Item i=new Item();
        copy(i,r); i.setReporter(u);
        if(i.getDate()==null)i.setDate(LocalDate.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(view(items.save(i)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody ItemRequest r,Authentication auth){
        return items.findById(id).map(i->{
            if(!i.getReporter().getEmail().equals(auth.getName()))
                return ResponseEntity.status(403).body(Map.of("message","Not your report"));
            copy(i,r); return ResponseEntity.ok(view(items.save(i)));
        }).orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,Authentication auth){
        return items.findById(id).map(i->{
            if(!i.getReporter().getEmail().equals(auth.getName())) return ResponseEntity.status(403).build();
            items.delete(i); return ResponseEntity.noContent().build();
        }).orElseGet(()->ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/recover")
    public ResponseEntity<?> recover(@PathVariable Long id,Authentication auth){
        return items.findById(id).map(i->{
            if(!i.getReporter().getEmail().equals(auth.getName())) return ResponseEntity.status(403).body(Map.of("message","Not your report"));
            i.setStatus(ItemStatus.RECOVERED); return ResponseEntity.ok(view(items.save(i)));
        }).orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<?> matches(@PathVariable Long id){
        Optional<Item> base=items.findById(id);
        if(base.isEmpty()) return ResponseEntity.notFound().build();
        Item b=base.get();
        List<Map<String,Object>> result=items.findAllByOrderByDateDesc().stream()
            .filter(i->!i.getId().equals(id) && i.getStatus()==ItemStatus.ACTIVE && i.getType()!=b.getType())
            .map(i->new AbstractMap.SimpleEntry<>(i,score(b,i)))
            .filter(e->e.getValue()>=2)
            .sorted((a,c)->Integer.compare(c.getValue(),a.getValue()))
            .limit(5)
            .map(e->view(e.getKey()))
            .toList();
        return ResponseEntity.ok(result);
    }

    private int score(Item a,Item b){
        int s=0;
        if(a.getCategory().equalsIgnoreCase(b.getCategory())) s+=3;
        if(a.getLocation().equalsIgnoreCase(b.getLocation())) s+=3;
        String x=(a.getTitle()+" "+a.getDescription()).toLowerCase();
        String y=(b.getTitle()+" "+b.getDescription()).toLowerCase();
        for(String w:x.split("\\W+")) if(w.length()>3 && y.contains(w)) s++;
        if(a.getDate()!=null&&b.getDate()!=null && Math.abs(a.getDate().toEpochDay()-b.getDate().toEpochDay())<=7)s++;
        return s;
    }

    private void copy(Item i,ItemRequest r){
        i.setTitle(r.title());i.setDescription(r.description());i.setCategory(r.category());
        i.setLocation(r.location());i.setDate(r.date());i.setImageUrl(r.imageUrl());i.setType(r.type());
    }

    private Map<String,Object> view(Item i){
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("id",i.getId());m.put("title",i.getTitle());m.put("description",i.getDescription());
        m.put("category",i.getCategory());m.put("location",i.getLocation());m.put("date",i.getDate());
        m.put("imageUrl",i.getImageUrl());m.put("type",i.getType());m.put("status",i.getStatus());
        m.put("reporterName",i.getReporter().getName());m.put("reporterEmail",i.getReporter().getEmail());
        return m;
    }
}
