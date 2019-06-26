import os
import sys
import weblogic.security.internal.SerializedSystemIni
import weblogic.security.internal.encryption.ClearOrEncryptedService

# ******************************************* function declarations *******************************************
def createDataSource():
	print("STEP 1")
	jdbcResourcePath='/JDBCSystemResources/' + dataSourceName + '/JDBCResource/' + dataSourceName
	
	#Skip creation if the datasource already exists
	if getMBean(jdbcResourcePath)!=None:
		print 'The datasource "' + jdbcResourcePath + '" already exists, skipping creation'
		return
	
	print 'Creating datasource', dataSourceName
	edit()
	startEdit()
	
	cd('/')
	dataSourceObj=cmo.createJDBCSystemResource(dataSourceName)
	cd(jdbcResourcePath)
	cmo.setName(dataSourceName)
	if (clusterEnv=="true"):
		dataSourceObj.addTarget(getMBean('/Clusters/'+targetCluster))
	else:
		for targetServer in allTargetServers:
			dataSourceObj.addTarget(getMBean('/Servers/'+targetServer))	
	
	cd(jdbcResourcePath + '/JDBCDataSourceParams/' + dataSourceName)
	dataSourceJNDI='jdbc/'+dataSourceName
	set('JNDINames',jarray.array([String(dataSourceJNDI)], String))
	if (dataSourceXA=='true'):
		set('GlobalTransactionsProtocol', java.lang.String('OnePhaseCommit'))
	else:
		set('GlobalTransactionsProtocol', java.lang.String('None'))
	 
	cd(jdbcResourcePath + '/JDBCDriverParams/' + dataSourceName )
	cmo.setDriverName(dataSourceDriver)
	
	if (dataSourceType=='Oracle'):
		set('Url','jdbc:oracle:thin:@'+dataSourceDB+':'+dataSourcePort+'/'+dataSourceSID)
	elif (dataSourceType=='MySQL'):
		set('Url','jdbc:mysql://'+dataSourceDB+':'+dataSourcePort+'/'+dataSourceSID+'?useUnicode=true&characterEncoding=utf8')
	
	osb_domain=getMBean('/').getRootDirectory()
	set('PasswordEncrypted',encrypt(dataSourcePass,osb_domain))
	cd(jdbcResourcePath + '/JDBCDriverParams/' + dataSourceName + '/Properties/' + dataSourceName)
	cmo.createProperty('user')
	cd(jdbcResourcePath + '/JDBCDriverParams/' + dataSourceName + '/Properties/' + dataSourceName + '/Properties/user')
	cmo.setValue(dataSourceUser)
	
	cd(jdbcResourcePath + '/JDBCConnectionPoolParams/' + dataSourceName)
	cmo.setInitialCapacity(0)
	cmo.setMaxCapacity(int(dataSourceMaxCapacity))
	 
	save()	
	activate()
	print '********************* DataSource: '+dataSourceName+' with JNDI: '+dataSourceJNDI+' was successfully created *********************'

# ******************************************* end of function declarations *******************************************
	
execfile('C:/Users/nkontotha/Desktop/wlst/common.py')

global inputPropertyFile
inputPropertyFile='C:/Users/nkontotha/Desktop/wlst/resource.properties'
loadProperties(inputPropertyFile)
allTargetServers=String(targetServers).split(',')
connectWeblogicAdmin()
if (dataSourceXA=='true'):
	dataSourceDriver='oracle.jdbc.xa.client.OracleXADataSource'
else:
	dataSourceDriver='oracle.jdbc.OracleDriver'
try:
	createDataSource()
except:
	handleExceptionOnEdit('Creation of datasource %s failed' % dataSourceName)

