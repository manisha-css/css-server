package com.cynosure.service.impl;

import com.cynosure.dto.ContactUsDto;
import com.cynosure.repo.ContactUsRepo;
import com.cynosure.service.IContactUsService;
import com.cynosure.util.UtilityService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("contactUsServiceImpl")
@Transactional
public class ContactUsServiceImpl implements IContactUsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsServiceImpl.class);
  @Autowired ContactUsRepo contactUsRepo;

  @Autowired ModelMapper commonModelMapper;

  @Autowired UtilityService utilityService;

  @Override
  public void save(ContactUsDto contactUsDto) {
    LOGGER.debug("Inside Save contactus");
    contactUsRepo.save(utilityService.convertToContactUs(contactUsDto));
  }
}
