def call(String command, failOnError=true) {
  def exitCode=-1
  if (isUnix()) {
	  exitCode=sh script:command, returnStatus:true
  }
  else {
    exitCode=bat script:command, returnStatus:true
  }
  
  if (exitCode!=0 && failOnError) {
    error "Script failed with exit code ${exitCode}"
  }
  
  return exitCode
}