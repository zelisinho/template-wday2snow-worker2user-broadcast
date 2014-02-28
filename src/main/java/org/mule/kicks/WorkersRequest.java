package org.mule.kicks;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.workday.hr.EffectiveAndUpdatedDateTimeDataType;
import com.workday.hr.GetWorkersRequestType;
import com.workday.hr.ResponseFilterType;
import com.workday.hr.TransactionLogCriteriaType;
import com.workday.hr.TransactionLogTypeObjectIDType;
import com.workday.hr.TransactionLogTypeObjectType;
import com.workday.hr.TransactionTypeReferencesType;
import com.workday.hr.WorkerRequestCriteriaType;

public class WorkersRequest {

	public static GetWorkersRequestType create() throws ParseException, DatatypeConfigurationException {

		Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-26");
//		Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-06");
		GregorianCalendar gregorianCalendarStart = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendarStart.setTime(startDate);
		XMLGregorianCalendar xmlCalendarStart = DatatypeFactory.newInstance()
																.newXMLGregorianCalendar(gregorianCalendarStart);

//		Date endDate = new Date();
		Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-26");
		GregorianCalendar gregorianCalendarEnd = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendarEnd.setTime(endDate);
		XMLGregorianCalendar xmlCalendarEnd = DatatypeFactory.newInstance()
																.newXMLGregorianCalendar(gregorianCalendarEnd);

		EffectiveAndUpdatedDateTimeDataType dateRangeData = new EffectiveAndUpdatedDateTimeDataType();
		dateRangeData.setEffectiveFrom(xmlCalendarStart);
		dateRangeData.setEffectiveThrough(xmlCalendarEnd);

//		TransactionLogTypeObjectIDType transactionTypeId = new TransactionLogTypeObjectIDType();
//		transactionTypeId.setType("Business_Process_Type");
//		transactionTypeId.setValue("Hire Employee");

		TransactionLogTypeObjectType transactionTypeReference = new TransactionLogTypeObjectType();
//		transactionTypeReference.getID()
//								.add(transactionTypeId);

		TransactionTypeReferencesType transactionTypeReferences = new TransactionTypeReferencesType();
		transactionTypeReferences.getTransactionTypeReference()
									.add(transactionTypeReference);

		TransactionLogCriteriaType transactionLogCriteria = new TransactionLogCriteriaType();
		transactionLogCriteria.setTransactionTypeReferences(transactionTypeReferences);
		transactionLogCriteria.setTransactionDateRangeData(dateRangeData);

		WorkerRequestCriteriaType workerRequestCriteria = new WorkerRequestCriteriaType();
		workerRequestCriteria.getTransactionLogCriteriaData()
								.add(transactionLogCriteria);

		ResponseFilterType responseFilterType = new ResponseFilterType();
		responseFilterType.setPage(new BigDecimal(1));

		GetWorkersRequestType getWorkersType = new GetWorkersRequestType();
		getWorkersType.setResponseFilter(responseFilterType);
		getWorkersType.setRequestCriteria(workerRequestCriteria);

		return getWorkersType;
	}

}
