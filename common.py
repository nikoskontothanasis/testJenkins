import os, sys, string, traceback, shutil

def undoPendingChanges():
	edit()
	startEdit()
	
	try:
		undo('true','y')
	except:
		pass

	try:
		stopEdit('y')
	except:
		pass

def connectWeblogicAdmin():
	#return
	#Try to get Admin Server credentials with below order:
	#  1. Use adminCredentialsKeyFile and adminCredentialsConfigFile that may be defined in the input property file
	#  2. Use adminUsername and adminPassword that may be defined in the input property file
	#  3. Use ADMIN_USER and ADMIN_PASSWORD environment variables
	if ('adminCredentialsKeyFile' in globals()) and ('adminCredentialsConfigFile' in globals()):
		assert adminCredentialsKeyFile.strip()!='', 'The value of adminCredentialsKeyFile parameter is empty'
		assert adminCredentialsConfigFile.strip()!='', 'The value of adminCredentialsConfigFile parameter is empty'
		
		for filePath in [adminCredentialsKeyFile, adminCredentialsConfigFile]:
			assert os.path.isfile(filePath), 'The file "%s" does not exist' % filePath
		
		connect(userConfigFile=adminCredentialsConfigFile, userKeyFile=adminCredentialsKeyFile, url=adminServerUrl)
	elif ('adminUsername' in globals()) and ('adminPassword' in globals()):
		connect(adminUsername, adminPassword, adminServerUrl)
	else:
		adminUsernameLocal=System.getenv('ADMIN_USER')
		adminPasswordLocal=System.getenv('ADMIN_PASSWORD')
		
		if adminUsernameLocal==None or adminPasswordLocal==None:
			raise Exception('Missing credentials for Admin server. Set:\n\t1. The "adminCredentialsKeyFile" and "adminCredentialsConfigFile" parameters in the %s file, or\n\t2. The "adminUsername" and "adminPassword" parameters in the %s, or \n\t3. The "ADMIN_USER" and "ADMIN_PASSWORD" environment variables' % (inputPropertyFile, inputPropertyFile))
		
		connect(adminUsernameLocal, adminPasswordLocal, adminServerUrl)

def handleExceptionOnEdit(msg, failOnError=true):
	exc_info=sys.exc_info()
	typ, val = exc_info[:2]
	#Do not assign the traceback element to local variable, to avoid possibility of circular reference:
	#http://www.jython.org/docs/library/sys.html, "Warning: Assigning the traceback return value to a local variable in a function that is handling an exception will cause a circular reference"
	errorMsg='****************************\n%s: %s\n****************************' % (msg, string.join(traceback.format_exception(typ,val,sys.exc_info()[2])))
	
	undoPendingChanges()
	
	if failOnError:
		raise errorMsg
	else:
		print errorMsg

def handleExceptionOnEditedAdapter(msg, failOnError, planPath, planBackupPath):
	exc_info=sys.exc_info()
	typ, val = exc_info[:2]
	#Do not assign the traceback element to local variable, to avoid possibility of circular reference:
	#http://www.jython.org/docs/library/sys.html, "Warning: Assigning the traceback return value to a local variable in a function that is handling an exception will cause a circular reference"
	errorMsg='****************************\n%s: %s\n****************************' % (msg, string.join(traceback.format_exception(typ,val,sys.exc_info()[2])))
	
	undoPendingChanges()
	
	if planBackupPath==None:
		print 'Deleting new plan file %s' % planPath
		os.remove(planPath)
	else:
		print 'Reverting file %s to original plan' % planPath
		shutil.copy2(planBackupPath, planPath)
	
	if failOnError:
		raise errorMsg
	else:
		print errorMsg

#Load properties from file
global inputPropertyFile
inputPropertyFile='resource.properties'
loadProperties(inputPropertyFile)
