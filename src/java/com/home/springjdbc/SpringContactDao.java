package com.home.springjdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.home.jdbc.Contact;
import com.home.jdbc.ContactTelDetail;

public class SpringContactDao implements ContactDao, InitializingBean {

	// simple jdbctemplate
	private JdbcTemplate jdbcTemplate;
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private SelectAllContacts selectAllContacts;
	
	private SelectContactByFirstName selectContactByFirstName;
	
	private UpdateContact updateContact;
	
	private InsertContact insertContact;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	public void setSelectAllContacts(SelectAllContacts selectAllContacts)
	{this.selectAllContacts = selectAllContacts;}
	
	public void setSelectContactByFirstName(SelectContactByFirstName selectContactByFirstName)
	{this.selectContactByFirstName = selectContactByFirstName;}
	
	public void setUpdateContact(UpdateContact updateContact)
	{
		this.updateContact = updateContact;
	}

	public void setInsertContact(InsertContact insertContact)
	{
		this.insertContact = insertContact;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (jdbcTemplate == null) {
			throw new BeanCreationException("Null JdbcTemplate on ContactDao");
		}

		if (namedParameterJdbcTemplate == null) {
			throw new BeanCreationException("Null namedParameterJdbcTemplate on ContactDao");
		}
	}

	@Override
	public String findFirstNameById(Long id) {
		return jdbcTemplate.queryForObject("select first_name from contact where id = ?", new Object[] { id },
				String.class);
	}

	@Override
	public String findLastNameByIdNamedParm(Long id) {
		String sql = "select last_name from contact where id = :contactId";

		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("contactId", id);

		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
	}

	@Override
	public List<Contact> findAll() {
		String sql = "select id, first_name, last_name, birth_date from contact";
		// below is the simple inner class example
		// return namedParameterJdbcTemplate.query(sql, new ContactMapper());

		// lambda expression
		return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
			Contact contact = new Contact();
			contact.setId(rs.getLong("id"));
			contact.setFirstName(rs.getString("first_name"));
			contact.setLastName(rs.getString("last_name"));
			contact.setBirthDate(rs.getDate("birth_date"));

			return contact;
		});
	}

	private static final class ContactMapper implements RowMapper<Contact> {
		@Override
		public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
			Contact contact = new Contact();
			contact.setId(rs.getLong("id"));
			contact.setFirstName(rs.getString("first_name"));
			contact.setLastName(rs.getString("last_name"));
			contact.setBirthDate(rs.getDate("birth_date"));

			return contact;
		}
	}

	@Override
	public List<Contact> findAllWithDetail() {
		String sql = "select c.id, c.first_name, c.last_name, c.birth_date"
				+ ", t.id as contact_tel_id, t.tel_type, t.tel_number from contact c "
				+ "left join contact_tel_detail t on c.id = t.contact_id";

		return namedParameterJdbcTemplate.query(sql, new ContactWithDetailExtractor());
	}

	private static final class ContactWithDetailExtractor implements ResultSetExtractor<List<Contact>> {
		@Override
		public List<Contact> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Long, Contact> map = new HashMap<Long, Contact>();
			Contact contact = null;

			while (rs.next()) {
				Long id = rs.getLong("id");
				contact = map.get(id);

				if (contact == null) {
					contact = new Contact();
					contact.setId(id);
					contact.setFirstName(rs.getString("first_name"));
					contact.setLastName(rs.getString("last_name"));
					contact.setBirthDate(rs.getDate("birth_date"));
					contact.setContactTelDetails(new ArrayList<ContactTelDetail>());
					map.put(id, contact);
				}

				Long contactTelDetailId = rs.getLong("contact_tel_id");

				if (contactTelDetailId > 0) {
					ContactTelDetail contactTelDetail = new ContactTelDetail();
					contactTelDetail.setId(contactTelDetailId);
					contactTelDetail.setContactId(id);
					contactTelDetail.setTelType(rs.getString("tel_type"));
					contactTelDetail.setTelNumber(rs.getString("tel_number"));
					contact.getContactTelDetails().add(contactTelDetail);
				}
			}

			return new ArrayList<Contact>(map.values());
		}

	}

	@Override
	public List<Contact> findByFirstName(String firstName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("first_name", firstName);
		return selectContactByFirstName.executeByNamedParam(params);
	}

	@Override
	public void insert(Contact contact) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("first_name", contact.getFirstName());
        paramMap.put("last_name", contact.getLastName());
        paramMap.put("birth_date", contact.getBirthDate());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        insertContact.updateByNamedParam(paramMap, keyHolder);
        contact.setId(keyHolder.getKey().longValue());
	}

	@Override
	public void insertWithDetail(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Contact contact) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("first_name", contact.getFirstName());
        paramMap.put("last_name", contact.getLastName());
        paramMap.put("birth_date", contact.getBirthDate());
        paramMap.put("id", contact.getId());

        updateContact.updateByNamedParam(paramMap);
		
	}

	@Override
	public List<Contact> findAllbyMappingQuery() {
		// TODO Auto-generated method stub
		return selectAllContacts.execute();
	}
}