package com.dangthanhtu.example05.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "brands")
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;
    private String image; 
    private String brandName; 
}
