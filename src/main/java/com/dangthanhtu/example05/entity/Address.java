package com.dangthanhtu.example05.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 2, message = "Street name must contain at least 2 characters")
    private String street;

    @NotBlank
    @Size(min = 2, message = "City name must contain at least 2 characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "District name must contain at least 2 characters")
    private String district;

    @NotBlank
    @Size(min = 2, message = "Ward name must contain at least 2 characters")
    private String ward;

    @NotBlank
    @Size(min = 2, message = "Country name must contain at least 2 characters")
    private String country;

    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String country, String district, String city, String ward, String street) {
        this.country = country;
        this.district = district;
        this.city = city;
        this.ward = ward;
        this.street = street;
    }
}