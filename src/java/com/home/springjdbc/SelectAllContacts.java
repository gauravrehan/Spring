package com.home.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.object.MappingSqlQuery;
import javax.sql.DataSource;
import com.home.jdbc.Contact;

public class SelectAllContacts extends MappingSqlQuery<Contact>  {
	
	private static String SQL = "select id, first_name, last_name, birth_date from contact";
	
	public SelectAllContacts(DataSource dataSource)
	{
		super(dataSource, SQL);
	}
	
	@Override
	protected Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
		Contact contact = new Contact();

        contact.setId(rs.getLong("id"));
        contact.setFirstName(rs.getString("first_name"));
        contact.setLastName(rs.getString("last_name"));
        contact.setBirthDate(rs.getDate("birth_date"));

        return contact;
		
	}

}
