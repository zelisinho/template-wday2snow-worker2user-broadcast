package org.mule.kicks;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.workday.hr.EffectiveAndUpdatedDateTimeDataType;
import com.workday.hr.EmployeeGetType;
import com.workday.hr.EmployeeReferenceType;
import com.workday.hr.ExternalIntegrationIDReferenceDataType;
import com.workday.hr.GetWorkersRequestType;
import com.workday.hr.IDType;
import com.workday.hr.ResponseFilterType;
import com.workday.hr.TransactionLogCriteriaType;
import com.workday.hr.TransactionLogTypeObjectIDType;
import com.workday.hr.TransactionLogTypeObjectType;
import com.workday.hr.TransactionTypeReferencesType;
import com.workday.hr.WorkerRequestCriteriaType;

public class WorkersRequest {

	public static GetWorkersRequestType create() throws ParseException, DatatypeConfigurationException {
		GetWorkersRequestType getWorkersType = new GetWorkersRequestType();
		ResponseFilterType responseFilterType = new ResponseFilterType();
		WorkerRequestCriteriaType workerRequestCriteria = new WorkerRequestCriteriaType();
		TransactionLogCriteriaType transactionLogCriteria = new TransactionLogCriteriaType();
		TransactionTypeReferencesType transactionTypeReferences = new TransactionTypeReferencesType();
		TransactionLogTypeObjectType transactionTypeReference = new TransactionLogTypeObjectType();
		TransactionLogTypeObjectIDType transactionTypeId = new TransactionLogTypeObjectIDType();

		transactionTypeId.setType("Business_Process_Type");
		transactionTypeId.setValue("Hire Employee");
		EffectiveAndUpdatedDateTimeDataType dateRangeData = new EffectiveAndUpdatedDateTimeDataType();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = simpleDateFormat.parse("2014-01-06");
		Date endDate = new Date();
		GregorianCalendar gregorianCalendarStart = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendarStart.setTime(startDate);

		GregorianCalendar gregorianCalendarEnd = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendarEnd.setTime(endDate);

		XMLGregorianCalendar xmlCalendarStart = DatatypeFactory.newInstance()
																.newXMLGregorianCalendar(gregorianCalendarStart);
		XMLGregorianCalendar xmlCalendarEnd = DatatypeFactory.newInstance()
																.newXMLGregorianCalendar(gregorianCalendarEnd);
		dateRangeData.setEffectiveFrom(xmlCalendarStart);
		dateRangeData.setEffectiveThrough(xmlCalendarEnd);
		transactionTypeReference.getID()
								.add(transactionTypeId);
		transactionTypeReferences.getTransactionTypeReference()
									.add(transactionTypeReference);
		transactionLogCriteria.setTransactionTypeReferences(transactionTypeReferences);
		transactionLogCriteria.setTransactionDateRangeData(dateRangeData);
		workerRequestCriteria.getTransactionLogCriteriaData()
								.add(transactionLogCriteria);
		responseFilterType.setPage(new BigDecimal(1));
		getWorkersType.setResponseFilter(responseFilterType);
		getWorkersType.setRequestCriteria(workerRequestCriteria);

		return getWorkersType;
	}

}
