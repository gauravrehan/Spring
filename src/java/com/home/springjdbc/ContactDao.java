package com.home.springjdbc;

import java.util.List;
import com.home.jdbc.Contact;

public interface ContactDao {
    String findFirstNameById(Long id);
    String findLastNameByIdNamedParm(Long id);
    List<Contact> findAll();
    List<Contact> findAllWithDetail();
    
    List<Contact> findAllbyMappingQuery();
    List<Contact> findByFirstName(String firstName);
    
    
    void insert(Contact contact);
    void insertWithDetail(Contact contact);
    void update(Contact contact);

}