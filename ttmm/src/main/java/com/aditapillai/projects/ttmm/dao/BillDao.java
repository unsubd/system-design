package com.aditapillai.projects.ttmm.dao;

import com.aditapillai.projects.ttmm.models.Bill;
import org.springframework.data.repository.CrudRepository;

public interface BillDao extends CrudRepository<Bill, String> {
}
