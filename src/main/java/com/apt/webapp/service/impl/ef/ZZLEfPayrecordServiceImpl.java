package com.apt.webapp.service.impl.ef;

import javax.jws.WebService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.webapp.service.ef.IZZLEfPayrecordService;



/**
 * 功能说明：
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-27
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "zZLEfPayrecordService", endpointInterface = "com.apt.webapp.service.ef.IZZLEfPayrecordService")
public class ZZLEfPayrecordServiceImpl implements IZZLEfPayrecordService {
	
}
