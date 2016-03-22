package com.home.springjdbc;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.home.jdbc.Contact;
import com.home.jdbc.ContactTelDetail;

public class SpringJdbcSample {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:com/home/springjdbc/spring-jdbc-context.xml");
        ctx.refresh();

        ContactDao contactDao = ctx.getBean("contactDao", ContactDao.class);

//        basic jdbcTemplate
        System.out.println("First name for contact id 1 is: " +
            contactDao.findFirstNameById(1l));
//        named parameter jdbc template
        System.out.println("Last name for contact id 2 is: " +
        	contactDao.findLastNameByIdNamedParm(2l));
        
//        Rowmapper example
        List<Contact> contacts = contactDao.findAll();
        printContacts(contacts);
        
        
        //ResultSetExtractor example
        System.out.println("Contacts with details");
        List<Contact> contactsWithDetails = contactDao.findAllWithDetail();
        printContacts(contactsWithDetails);
        
        //MappingSqlQuery example
        System.out.println("MappingSQLQuery example");
        List<Contact> selectAllContactsMappingQery = contactDao.findAllbyMappingQuery();
        printContacts(selectAllContactsMappingQery);
        
        
      //MappingSqlQuery example
        System.out.println("MappingSQLQuery example with named paran");
        List<Contact> selectContactFirstName = contactDao.findByFirstName("Chris");
        printContacts(selectContactFirstName);
        
//      Sqlupdate for updating the tables
        Contact contact = new Contact();
        contact.setId(1l);
        contact.setFirstName("Chris");
        contact.setLastName("John");
        contact.setBirthDate(new Date(
            (new GregorianCalendar(1977, 10, 1)).getTime().getTime()));

        contactDao.update(contact);
        List<Contact> contactsCheck = contactDao.findAll();
        printContacts(contactsCheck);

//        Insert new contact
        Contact contactNew = new Contact();
        contactNew.setFirstName("Rod");
        contactNew.setLastName("Johnson");
        contactNew.setBirthDate(new Date((new GregorianCalendar(2001, 10, 1)).getTime().getTime()));

        contactDao.insert(contactNew);
        List<Contact> contactCheck = contactDao.findAll();
        printContacts(contactCheck);

        
        
        
        }
    
    
    private static void printContacts(List<Contact> contacts)
    {
    	for (Contact contact: contacts) {
            System.out.println(contact);

            if (contact.getContactTelDetails() != null) {
                for (ContactTelDetail contactTelDetail:
                    contact.getContactTelDetails()) {
                    System.out.println("---" + contactTelDetail);
                }
            }

            System.out.println();
        }
    }
}