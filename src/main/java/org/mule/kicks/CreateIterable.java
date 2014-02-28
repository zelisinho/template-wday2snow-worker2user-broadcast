package org.mule.kicks;


import java.util.ArrayList;
import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.common.collect.Lists;
import com.workday.hr.ContactInformationDataType;
import com.workday.hr.EmailAddressInformationDataType;
import com.workday.hr.LegalNameDataType;
import com.workday.hr.PersonNameDataType;
import com.workday.hr.PersonNameDetailDataType;
import com.workday.hr.PersonalInformationDataType;
import com.workday.hr.WorkerDataType;
import com.workday.hr.WorkerType;


public class CreateIterable extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		
//		final Insert user = new Insert();
//		user.setFirstName("Miguel");
//		user.setMiddleName("Astic");
//		user.setLastName("Oliva");

		PersonNameDetailDataType nameDetailedData = new PersonNameDetailDataType();
		nameDetailedData.setFirstName("Javier");
		nameDetailedData.setLastName("Casal");
		nameDetailedData.setMiddleName("Jose");
		nameDetailedData.setSecondaryLastName("HELLO");

		LegalNameDataType legalNameData = new LegalNameDataType();
		legalNameData.setNameDetailData(nameDetailedData);

		PersonNameDataType nameData = new PersonNameDataType();
		nameData.setLegalNameData(legalNameData);
		
		EmailAddressInformationDataType emailAddrInfo = new EmailAddressInformationDataType();
		emailAddrInfo.setEmailAddress("javier@mail.com");
		
		List<EmailAddressInformationDataType> emailAddressData = new ArrayList<EmailAddressInformationDataType>();
		emailAddressData.add(emailAddrInfo);
		
		ContactInformationDataType informationData = new ContactInformationDataType();
		informationData.setEmailAddressData(emailAddressData);
		
		PersonalInformationDataType personalInfo = new PersonalInformationDataType();
		personalInfo.setNameData(nameData);
		personalInfo.setContactData(informationData);
		
		WorkerDataType workerData = new WorkerDataType();
		workerData.setPersonalData(personalInfo);
		
		WorkerType user = new WorkerType();
		user.setWorkerData(workerData);
		
		
		Iterable<WorkerType> iterable = Lists.newArrayList(user);
		
		message.setPayload(iterable);
		return message;
	}
	

}
