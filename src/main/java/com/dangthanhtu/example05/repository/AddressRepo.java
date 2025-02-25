package com.dangthanhtu.example05.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dangthanhtu.example05.entity.Address;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {
    Address findByCountryAndDistrictAndCityAndWardAndStreet(String country, String district, String city, String ward, String street);
}