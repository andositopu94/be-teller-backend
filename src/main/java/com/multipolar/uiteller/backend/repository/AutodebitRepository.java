package com.multipolar.uiteller.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multipolar.uiteller.backend.model.Autodebit;

public interface AutodebitRepository extends JpaRepository<Autodebit, Long> {
	
	/****************************** ORACLE VERSION ********************************************/
	@Query(value="SELECT ad.* FROM autodebit ad where ad.delflag = 0 " +
					"and not exists " + 
					"(select al.id from autodebit_log al " + 
					"where al.autodebit_id = ad.id " + 
					"and (al.rc = '00' or al.rc = '88') " +
					"and TO_CHAR(al.created_at, 'mm') = TO_CHAR(SYSDATE, 'mm') " +
					"AND TO_CHAR(al.created_at, 'yyyy') = TO_CHAR(SYSDATE, 'yyyy')) " +
					"and TO_CHAR(SYSDATE, 'dd') between ad.start_date and ad.end_date",
			nativeQuery=true)
	List<Autodebit> fetchAutodebitData();
	/******************************************************************************************/
	
	/****************************** MySQL VERSION *******************************************
	@Query(value="SELECT ad.* " +
			"FROM autodebit ad " +
			"where ad.delflag = 0 " +
			"and not exists " + 
			"(select al.id from autodebit_log al " +
			"where al.autodebit_id = ad.id " +
			"and al.rc = '00' or al.rc = '88' " +
			"and YEAR(al.created_at) = YEAR(CURRENT_DATE()) " + 
			"AND MONTH(al.created_at) = MONTH(CURRENT_DATE()) " +
			"AND current_date() between ad.start_date and ad.end_date)",
	nativeQuery=true)
	List<Autodebit> fetchAutodebitData();
	*****************************************************************************************/
	
	List<Autodebit> findByTransactionName(String trxname);
	
	List<Autodebit> findByBranchCode(String branchCode);
	
	List<Autodebit> findByTransactionNameAndBranchCode(String trxname, String branchCode);
	
	List<Autodebit> findByTransactionNameAndCustomerId(String trxName, String custId);
}
