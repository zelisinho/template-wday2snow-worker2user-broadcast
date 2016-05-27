/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.construct.Flow;
import org.mule.context.notification.NotificationException;

import com.mulesoft.module.batch.BatchTestHelper;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Template that make calls to external systems.
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private static final long TIMEOUT_MILLIS = 30000;
	private static final long DELAY_MILLIS = 500;
	protected static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	protected static final int TIMEOUT_SEC = 240;
	private static final String PHONE_NUMBER = "232-2323";
	private static final String STREET = "999 Main St";
	private static final String CITY = "San Francisco";
	private static final String LAST_NAME = "Test1";
	private static final String FIRST_NAME = "Test1";
	private BatchTestHelper helper;
	private Map<String, String> user = new HashMap<String, String>();	
	private static String WORKDAY_ID;	
    private String EMAIL = "drvukovic332211" + System.currentTimeMillis() + "@live.com";	
    private String SNOW_ID;
    
    @BeforeClass
    public static void beforeTestClass() {
        System.setProperty("poll.startDelayMillis", "8000");
        System.setProperty("poll.frequencyMillis", "30000");
        Date initialDate = new Date(System.currentTimeMillis() - 60*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(initialDate);
        System.setProperty(
        		"watermark.defaultExpression", 
        		"#[groovy: new GregorianCalendar("
        				+ cal.get(Calendar.YEAR) + ","
        				+ cal.get(Calendar.MONTH) + ","
        				+ cal.get(Calendar.DAY_OF_MONTH) + ","
        				+ cal.get(Calendar.HOUR_OF_DAY) + ","
        				+ cal.get(Calendar.MINUTE) + ","
        				+ cal.get(Calendar.SECOND) + ") ]");    
    }

    @Before
    public void setUp() throws Exception {
		final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    	   logger.error("Error occured while reading mule.test.properties", e);
    	} 
    	WORKDAY_ID = props.getProperty("wday.testuser.id");
    	helper = new BatchTestHelper(muleContext);
		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();
		
		createTestDataInSandBox();
    }
    
	@SuppressWarnings("unchecked")
	@Test
    public void testMainFlow() throws Exception {
		Thread.sleep(10000);
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();
		helper.awaitJobTermination(TIMEOUT_MILLIS, DELAY_MILLIS);
		helper.assertJobWasSuccessful();	
		
		// get requests from ServiceNow
		Flow getSnowRequestsFlow =  (Flow) muleContext.getRegistry().lookupObject("getSnowUsers");
		MuleEvent response = getSnowRequestsFlow.process(getTestEvent(new DefaultMuleMessage(EMAIL, muleContext)));
		List<Map<String,String>> snowRes = ((List<Map<String,String>>)response.getMessage().getPayload());
		logger.info("snow requests: " + snowRes.size());
		
		Assert.assertTrue("There should be a user in ServiceNow.", snowRes.size() == 1);
		Assert.assertEquals("First name should be set", FIRST_NAME, snowRes.get(0).get("first_name"));
		Assert.assertEquals("Last name should be set", LAST_NAME, snowRes.get(0).get("last_name"));
		Assert.assertEquals("City should be set", CITY, snowRes.get(0).get("city"));
		Assert.assertEquals("Street should be set", user.get("Street"), snowRes.get(0).get("street"));		
		Assert.assertEquals("Home Phone number should be set", "+1  " + PHONE_NUMBER, snowRes.get(0).get("home_phone"));		
		
		SNOW_ID = snowRes.get(0).get("sys_id");
    }    

    @After
    public void tearDown() throws MuleException, Exception {
    	deleteTestDataFromSandBox();
    }
    
    private void registerListeners() throws NotificationException {
		muleContext.registerListener(pipelineListener);
	}
    
    private void createTestDataInSandBox() throws MuleException, Exception {
		logger.info("updating a workday employee...");
		try {
			runFlow("updateWorkdayEmployee", getTestEvent(prepareEdit(), MessageExchangePattern.REQUEST_RESPONSE));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private Map<String, String> prepareEdit(){			
		user.put("Location", "San_Francisco_site");
		user.put("Phone", PHONE_NUMBER);
		user.put("Email", EMAIL);
		user.put("ExtId__c", WORKDAY_ID);
		user.put("Street", STREET + System.currentTimeMillis());
		user.put("Country", "USA");
		user.put("State", "USA-CA");
		user.put("City", CITY);
		user.put("PostalCode", "94105");
		return user;
	}
        
    private void deleteTestDataFromSandBox() throws MuleException, Exception {
    	logger.info("deleting test data...");						
		runFlow("deleteSnowUsers", getTestEvent(SNOW_ID));
	}
}
