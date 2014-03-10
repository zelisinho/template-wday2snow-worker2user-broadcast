package org.mule.kicks;

import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.servicenow.sysuser.GetRecords;
import com.workday.hr.WorkerDataType;
import com.workday.hr.WorkerType;

public class CreateGetRecords extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		@SuppressWarnings("unchecked")
		List<WorkerType> payload = (List<WorkerType>) message.getPayload();
		WorkerType workerType = payload.get(0);
		WorkerDataType workerData = workerType.getWorkerData();
		
		GetRecords query = new GetRecords();

		query.setFirstName(workerData.getPersonalData().getNameData().getLegalNameData().getNameDetailData().getFirstName());
		query.setLastName(workerData.getPersonalData().getNameData().getLegalNameData().getNameDetailData().getLastName());
		query.setEmail(workerData.getPersonalData().getContactData().getEmailAddressData().get(0).getEmailAddress());
		
		message.setPayload(query);
		return message;
	}

}
