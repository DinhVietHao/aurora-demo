package com.group01.aurora_demo.customer.service;

import java.sql.SQLException;
import java.util.List;

import com.group01.aurora_demo.customer.model.Address;
import com.group01.aurora_demo.customer.repository.AddressRepository;

public class AddressService {
    private AddressRepository addressRepository;

    public AddressService() {
        this.addressRepository = new AddressRepository();
    }

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAddressesByUserId(long userId) {
        return this.addressRepository.getAddressesByUserId(userId);
    }

    public void addAddress(long userId, Address address, boolean isDefault) {
        this.addressRepository.addAddress(userId, address, isDefault);
    }

    public void updateAddress(long userId, Address address, boolean isDefault) throws SQLException {
        this.addressRepository.updateAddress(userId, address, isDefault);
    }

    public void deleteAddress(long userId, long addressId) throws SQLException {
        this.addressRepository.deleteAddress(userId, addressId);
    }

    public void setDefaultAddress(long userId, long addressId) throws SQLException {
        this.addressRepository.setDefaultAddress(userId, addressId);
    }

    public Address getAddressById(long userId, long addressId) {
        return this.addressRepository.getAddressById(userId, addressId);
    }

}
