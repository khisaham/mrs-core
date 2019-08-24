/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.db.hibernate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.mrs.Concept;
import org.mrs.EncounterType;
import org.mrs.Field;
import org.mrs.FieldAnswer;
import org.mrs.FieldType;
import org.mrs.Form;
import org.mrs.FormField;
import org.mrs.FormResource;
import org.mrs.api.APIException;
import org.mrs.api.db.DAOException;
import org.mrs.api.db.FormDAO;
import org.mrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate-specific Form-related functions. This class should not be used directly. All calls
 * should go through the {@link org.mrs.api.FormService} methods.
 *
 * @see org.mrs.api.db.FormDAO
 * @see org.mrs.api.FormService
 */
public class HibernateFormDAO implements FormDAO {
	
	private static final Logger log = LoggerFactory.getLogger(HibernateFormDAO.class);
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Returns the form object originally passed in, which will have been persisted.
	 *
	 * @see org.mrs.api.FormService#createForm(org.mrs.Form)
	 */
	@Override
	public Form saveForm(Form form) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(form);
		return form;
	}
	
	/**
	 * @see org.mrs.api.FormService#duplicateForm(org.mrs.Form)
	 */
	@Override
	public Form duplicateForm(Form form) throws DAOException {
		return (Form) sessionFactory.getCurrentSession().merge(form);
	}
	
	/**
	 * @see org.mrs.api.FormService#deleteForm(org.mrs.Form)
	 */
	@Override
	public void deleteForm(Form form) throws DAOException {
		sessionFactory.getCurrentSession().delete(form);
	}
	
	/**
	 * @see org.mrs.api.FormService#getForm(java.lang.Integer)
	 */
	@Override
	public Form getForm(Integer formId) throws DAOException {
		return (Form) sessionFactory.getCurrentSession().get(Form.class, formId);
	}
	
	/**
	 * @see org.mrs.api.FormService#getFormFields(Form)
	 */
	@SuppressWarnings("unchecked")
	public List<FormField> getFormFields(Form form) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(FormField.class, "ff")
		        .add(Restrictions.eq("ff.form", form)).list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFields(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Field> getFields(String search) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Field.class);
		criteria.add(Restrictions.like("name", search, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.FormService#getFieldsByConcept(org.mrs.Concept)
	 */
	@SuppressWarnings("unchecked")
	public List<Field> getFieldsByConcept(Concept concept) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Field.class);
		criteria.add(Restrictions.eq("concept", concept));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.FormService#getField(java.lang.Integer)
	 * @see org.mrs.api.db.FormDAO#getField(java.lang.Integer)
	 */
	@Override
	public Field getField(Integer fieldId) throws DAOException {
		return (Field) sessionFactory.getCurrentSession().get(Field.class, fieldId);
	}
	
	/**
	 * @see org.mrs.api.FormService#getAllFields(boolean)
	 * @see org.mrs.api.db.FormDAO#getAllFields(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Field> getAllFields(boolean includeRetired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Field.class);
		
		if (!includeRetired) {
			crit.add(Restrictions.eq("retired", false));
		}
		
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.FormService#getFieldType(java.lang.Integer)
	 * @see org.mrs.api.db.FormDAO#getFieldType(java.lang.Integer)
	 */
	@Override
	public FieldType getFieldType(Integer fieldTypeId) throws DAOException {
		return (FieldType) sessionFactory.getCurrentSession().get(FieldType.class, fieldTypeId);
	}
	
	/**
	 * @see org.mrs.api.FormService#getFieldTypes()
	 * @see org.mrs.api.db.FormDAO#getAllFieldTypes(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FieldType> getAllFieldTypes(boolean includeRetired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FieldType.class);
		
		if (!includeRetired) {
			crit.add(Restrictions.eq("retired", false));
		}
		
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.FormService#getFormField(java.lang.Integer)
	 * @see org.mrs.api.db.FormDAO#getFormField(java.lang.Integer)
	 */
	@Override
	public FormField getFormField(Integer formFieldId) throws DAOException {
		return (FormField) sessionFactory.getCurrentSession().get(FormField.class, formFieldId);
	}
	
	/**
	 * @see org.mrs.api.FormService#getFormField(org.mrs.Form, org.mrs.Concept,
	 *      java.util.Collection, boolean)
	 * @see org.mrs.api.db.FormDAO#getFormField(org.mrs.Form, org.mrs.Concept,
	 *      java.util.Collection, boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public FormField getFormField(Form form, Concept concept, Collection<FormField> ignoreFormFields, boolean force)
	        throws DAOException {
		if (form == null) {
			log.debug("form is null, no fields will be matched");
			return null;
		}
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormField.class, "ff").createAlias("field",
		    "field").add(Restrictions.eq("field.concept", concept)).add(Restrictions.eq("form", form));
		
		// get the list of all formfields with this concept for this form
		List<FormField> formFields = crit.list();
		
		String err = "FormField warning.  No FormField matching concept '" + concept + "' for form '" + form + "'";
		
		if (formFields.isEmpty()) {
			log.debug(err);
			return null;
		}
		
		// save the first formfield in case we're not a in a "force" situation
		FormField backupPlan = formFields.get(0);
		
		// remove the formfields we're supposed to ignore from the return list
		formFields.removeAll(ignoreFormFields);
		
		// if we ended up removing all of the formfields, check to see if we're
		// in a "force" situation
		if (formFields.isEmpty()) {
			if (!force) {
				return backupPlan;
			} else {
				log.debug(err);
				return null;
			}
		} else {
			// if formFields.size() is still greater than 0
			return formFields.get(0);
		}
	}
	
	/**
	 * @see org.mrs.api.FormService#getForms()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Form> getAllForms(boolean includeRetired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class);
		
		if (!includeRetired) {
			crit.add(Restrictions.eq("retired", false));
		}
		
		crit.addOrder(Order.asc("name"));
		crit.addOrder(Order.asc("formId"));
		
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormsContainingConcept(org.mrs.Concept)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Form> getFormsContainingConcept(Concept c) throws DAOException {
		String q = "select distinct ff.form from FormField ff where ff.field.concept = :concept";
		Query query = sessionFactory.getCurrentSession().createQuery(q);
		query.setEntity("concept", c);
		
		return query.list();
	}
	
	/**
	 * @see org.mrs.api.FormService#saveField(org.mrs.Field)
	 * @see org.mrs.api.db.FormDAO#saveField(org.mrs.Field)
	 */
	@Override
	public Field saveField(Field field) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(field);
		return field;
	}
	
	/**
	 * @see org.mrs.api.FormService#deleteField(org.mrs.Field)
	 * @see org.mrs.api.db.FormDAO#deleteField(org.mrs.Field)
	 */
	@Override
	public void deleteField(Field field) throws DAOException {
		sessionFactory.getCurrentSession().delete(field);
	}
	
	/**
	 * @see org.mrs.api.FormService#createFormField(org.mrs.FormField)
	 */
	@Override
	public FormField saveFormField(FormField formField) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(formField);
		return formField;
	}
	
	/**
	 * @see org.mrs.api.FormService#deleteFormField(org.mrs.FormField)
	 * @see org.mrs.api.db.FormDAO#deleteFormField(org.mrs.FormField)
	 */
	@Override
	public void deleteFormField(FormField formField) throws DAOException {
		sessionFactory.getCurrentSession().delete(formField);
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getAllFormFields()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FormField> getAllFormFields() throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormField.class);
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFields(java.util.Collection, java.util.Collection,
	 *      java.util.Collection, java.util.Collection, java.util.Collection, java.lang.Boolean,
	 *      java.util.Collection, java.util.Collection, java.lang.Boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Field> getFields(Collection<Form> forms, Collection<FieldType> fieldTypes, Collection<Concept> concepts,
	        Collection<String> tableNames, Collection<String> attributeNames, Boolean selectMultiple,
	        Collection<FieldAnswer> containsAllAnswers, Collection<FieldAnswer> containsAnyAnswer, Boolean retired)
	        throws DAOException {
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Field.class);
		
		if (!forms.isEmpty()) {
			crit.add(Restrictions.in("form", forms));
		}
		
		if (!fieldTypes.isEmpty()) {
			crit.add(Restrictions.in("fieldType", fieldTypes));
		}
		
		if (!concepts.isEmpty()) {
			crit.add(Restrictions.in("concept", concepts));
		}
		
		if (!tableNames.isEmpty()) {
			crit.add(Restrictions.in("tableName", tableNames));
		}
		
		if (!attributeNames.isEmpty()) {
			crit.add(Restrictions.in("attributeName", attributeNames));
		}
		
		if (selectMultiple != null) {
			crit.add(Restrictions.eq("selectMultiple", selectMultiple));
		}
		
		if (!containsAllAnswers.isEmpty()) {
			throw new APIException("Form.getFields.error", new Object[] { "containsAllAnswers" });
		}
		
		if (!containsAnyAnswer.isEmpty()) {
			throw new APIException("Form.getFields.error", new Object[] { "containsAnyAnswer" });
		}
		
		if (retired != null) {
			crit.add(Restrictions.eq("retired", retired));
		}
		
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getForm(java.lang.String, java.lang.String)
	 */
	@Override
	public Form getForm(String name, String version) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class);
		
		crit.add(Restrictions.eq("name", name));
		crit.add(Restrictions.eq("version", version));
		
		return (Form) crit.uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getForms(java.lang.String, java.lang.Boolean,
	 *      java.util.Collection, java.lang.Boolean, java.util.Collection, java.util.Collection,
	 *      java.util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Form> getForms(String partialName, Boolean published, Collection<EncounterType> encounterTypes,
	        Boolean retired, Collection<FormField> containingAnyFormField, Collection<FormField> containingAllFormFields,
	        Collection<Field> fields) throws DAOException {
		
		Criteria crit = getFormCriteria(partialName, published, encounterTypes, retired, containingAnyFormField,
		    containingAllFormFields, fields);
		
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormCount(java.lang.String, java.lang.Boolean,
	 *      java.util.Collection, java.lang.Boolean, java.util.Collection, java.util.Collection,
	 *      java.util.Collection)
	 */
	@Override
	public Integer getFormCount(String partialName, Boolean published, Collection<EncounterType> encounterTypes,
	        Boolean retired, Collection<FormField> containingAnyFormField, Collection<FormField> containingAllFormFields,
	        Collection<Field> fields) throws DAOException {
		
		Criteria crit = getFormCriteria(partialName, published, encounterTypes, retired, containingAnyFormField,
		    containingAllFormFields, fields);
		
		crit.setProjection(Projections.count("formId"));
		
		return OpenmrsUtil.convertToInteger((Long) crit.uniqueResult());
	}
	
	/**
	 * Convenience method to create the same hibernate criteria object for both getForms and
	 * getFormCount
	 *
	 * @param partialName
	 * @param published
	 * @param encounterTypes
	 * @param retired
	 * @param containingAnyFormField
	 * @param containingAllFormFields
	 * @param fields
	 * @return
	 */
	private Criteria getFormCriteria(String partialName, Boolean published, Collection<EncounterType> encounterTypes,
	        Boolean retired, Collection<FormField> containingAnyFormField, Collection<FormField> containingAllFormFields,
	        Collection<Field> fields) {
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class, "f");
		if (StringUtils.isNotEmpty(partialName)) {
			crit.add(Restrictions.or(Restrictions.like("name", partialName, MatchMode.START), Restrictions.like("name", " "
			        + partialName, MatchMode.ANYWHERE)));
		}
		if (published != null) {
			crit.add(Restrictions.eq("published", published));
		}
		
		if (!encounterTypes.isEmpty()) {
			crit.add(Restrictions.in("encounterType", encounterTypes));
		}
		
		if (retired != null) {
			crit.add(Restrictions.eq("retired", retired));
		}
		
		// TODO junit test
		if (!containingAnyFormField.isEmpty()) {
			// Convert form field persistents to integers
			Set<Integer> anyFormFieldIds = new HashSet<>();
			for (FormField ff : containingAnyFormField) {
				anyFormFieldIds.add(ff.getFormFieldId());
			}
			
			DetachedCriteria subquery = DetachedCriteria.forClass(FormField.class, "ff");
			subquery.setProjection(Projections.property("ff.form.formId"));
			subquery.add(Restrictions.in("ff.formFieldId", anyFormFieldIds));
			crit.add(Subqueries.propertyIn("f.formId", subquery));
		}
		
		if (!containingAllFormFields.isEmpty()) {
			
			// Convert form field persistents to integers
			Set<Integer> allFormFieldIds = new HashSet<>();
			for (FormField ff : containingAllFormFields) {
				allFormFieldIds.add(ff.getFormFieldId());
			}
			DetachedCriteria subquery = DetachedCriteria.forClass(FormField.class, "ff");
			subquery.setProjection(Projections.count("ff.formFieldId")).
			add(Property.forName("ff.form.formId").eqProperty("f.formId")).
			add(Restrictions.in("ff.formFieldId", allFormFieldIds));
			crit.add(Subqueries.eq((long) containingAllFormFields.size(), subquery));
			
		}
		
		// get all forms (dupes included) that have this field on them
		if (!fields.isEmpty()) {
			Criteria crit2 = crit.createCriteria("formFields", "ff");
			crit2.add(Restrictions.eqProperty("ff.form.formId", "form.formId"));
			crit2.add(Restrictions.in("ff.field", fields));
		}
		
		return crit;
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFieldByUuid(java.lang.String)
	 */
	@Override
	public Field getFieldByUuid(String uuid) {
		return (Field) sessionFactory.getCurrentSession().createQuery("from Field f where f.uuid = :uuid").setString("uuid",
		    uuid).uniqueResult();
	}
	
	@Override
	public FieldAnswer getFieldAnswerByUuid(String uuid) {
		return (FieldAnswer) sessionFactory.getCurrentSession().createQuery("from FieldAnswer f where f.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFieldTypeByUuid(java.lang.String)
	 */
	@Override
	public FieldType getFieldTypeByUuid(String uuid) {
		return (FieldType) sessionFactory.getCurrentSession().createQuery("from FieldType ft where ft.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFieldTypeByName(java.lang.String)
	 */
	@Override
	public FieldType getFieldTypeByName(String name) {
		return (FieldType) sessionFactory.getCurrentSession().createQuery("from FieldType ft where ft.name = :name")
		        .setString("name", name).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormByUuid(java.lang.String)
	 */
	@Override
	public Form getFormByUuid(String uuid) {
		return (Form) sessionFactory.getCurrentSession().createQuery("from Form f where f.uuid = :uuid").setString("uuid",
		    uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormFieldByUuid(java.lang.String)
	 */
	@Override
	public FormField getFormFieldByUuid(String uuid) {
		return (FormField) sessionFactory.getCurrentSession().createQuery("from FormField ff where ff.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormsByName(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Form> getFormsByName(String name) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class);
		
		crit.add(Restrictions.eq("name", name));
		crit.add(Restrictions.eq("retired", false));
		crit.addOrder(Order.desc("version"));
		
		return crit.list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#deleteFieldType(org.mrs.FieldType)
	 */
	@Override
	public void deleteFieldType(FieldType fieldType) throws DAOException {
		sessionFactory.getCurrentSession().delete(fieldType);
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#saveFieldType(org.mrs.FieldType)
	 */
	@Override
	public FieldType saveFieldType(FieldType fieldType) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(fieldType);
		return fieldType;
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormFieldsByField(Field)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FormField> getFormFieldsByField(Field field) {
		return sessionFactory.getCurrentSession().createQuery("from FormField f where f.field = :field").setEntity("field",
		    field).list();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormResource(java.lang.Integer)
	 */
	@Override
	public FormResource getFormResource(Integer formResourceId) {
		return (FormResource) sessionFactory.getCurrentSession().get(FormResource.class, formResourceId);
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormResourceByUuid(java.lang.String)
	 */
	@Override
	public FormResource getFormResourceByUuid(String uuid) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormResource.class).add(
		    Restrictions.eq("uuid", uuid));
		return (FormResource) crit.uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormResource(org.mrs.Form, java.lang.String)
	 */
	@Override
	public FormResource getFormResource(Form form, String name) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormResource.class).add(
		    Restrictions.and(Restrictions.eq("form", form), Restrictions.eq("name", name)));
		
		return (FormResource) crit.uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#saveFormResource(org.mrs.FormResource)
	 */
	@Override
	public FormResource saveFormResource(FormResource formResource) {
		sessionFactory.getCurrentSession().saveOrUpdate(formResource);
		return formResource;
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#deleteFormResource(org.mrs.FormResource)
	 */
	@Override
	public void deleteFormResource(FormResource formResource) {
		sessionFactory.getCurrentSession().delete(formResource);
	}
	
	/**
	 * @see org.mrs.api.db.FormDAO#getFormResourcesForForm(org.mrs.Form)
	 */
	@Override
	public Collection<FormResource> getFormResourcesForForm(Form form) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormResource.class).add(
		    Restrictions.eq("form", form));
		return crit.list();
	}
	
}
