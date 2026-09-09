package com.campus.lostfound.controller;

import com.campus.lostfound.model.*;
import com.campus.lostfound.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ItemRepository items;
    private final UserRepository users;

    public AdminController(ItemRepository items,UserRepository users){this.items=items;this.users=users;}

    @GetMapping("/stats")
    public Map<String,Object> stats(){
        return Map.of(
            "users",users.count(),
            "totalItems",items.count(),
            "lost",items.countByType(ItemType.LOST),
            "found",items.countByType(ItemType.FOUND),
            "recovered",items.countByStatus(ItemStatus.RECOVERED),
            "active",items.countByStatus(ItemStatus.ACTIVE)
        );
    }
}
