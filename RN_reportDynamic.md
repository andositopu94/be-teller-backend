Release note - report dynamic 1.0 :
WHAT'S NEW :

1. API Report Pendapatan yang dynamic. 
dengan url : http://<backend>/downloads/pdf/income/report/<trx-type>/?startDate=&endDate=&reportType=&subTransactionType=
Frontend perlu menyesuaikan format saat request report pendapatan karena berbeda dengan api existing.

request param untuk tentukan view trx
a. trx-type : path_variable : mandatory
b. subTransactionType : request_param : optional
c. reportType : request_param : optional

request param untuk query
a. startDate : format MM/dd/YYYY : request_param : optional
b. endDate : format MM/dd/YYYY : request_param : optional
c. BRANCH_CODE : request param : optional
d. CHANNEL : request param : optional
e. STATUS_LOG : request param : optional

contoh query dengan query req param:
http://<backend>/downloads/pdf/income/report/<trx-type>/?startDate=&endDate=&reportType=&subTransactionType=&BRANCH_CODE=100
http://<backend>/downloads/pdf/income/report/<trx-type>/?startDate=&endDate=&reportType=&subTransactionType=&CHANNEL=TELLER
http://<backend>/downloads/pdf/income/report/<trx-type>/?startDate=&endDate=&reportType=&subTransactionType=&BRANCH_CODE=100&STATUS_LOG=0

2. Perlu ditambahkan view transaksi pada database sebelum transaksi. dengan format nama view V1_<*trx_type*>_<sub_trx_type>_<report_type_TRANSACTION
contoh penamaan :
a. MPN EDNP : 
	-trx_type 		: MPN
	-report type 	: EDNP
	> view name : V1_MPN_EDNP_TRANSACTION
b. PLN POSTPAID :
	-trx_type 		: PLN
	-sub_trx_type 	: POSTPAID
	> view name : V1_PLN_POSTPAID_TRANSACTION
b. PBB STS POSTPAID :	
	-trx_type 		: PBBSTS
	-sub_trx_type 	: PBBSTS
	> view name	: V1_PBBSTS_PBBSTS_TRANSACTION
	
contoh view yg sudah ada : 
a. v1_PBB-STS_PBB-STS_transaction.sql
b. v1_mpn_ednp_transaction.sql
c. v1_pln_postpaid_transaction.sql


BUGFIXING
- 
(*ada untuk report pendapatan tapi karena akan diubah semua ke dynamic report pendapatan jadi semua tidak diperlukan lagi)


