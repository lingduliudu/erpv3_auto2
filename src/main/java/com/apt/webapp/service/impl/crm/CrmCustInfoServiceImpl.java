package com.apt.webapp.service.impl.crm;

import javax.jws.WebService;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.webapp.service.crm.ICrmCustInfoService;



@Service
@Transactional
@WebService(serviceName = "crmCustInfoService", endpointInterface = "com.apt.webapp.service.crm.ICrmCustInfoService")
public class CrmCustInfoServiceImpl  implements ICrmCustInfoService{
	
}
