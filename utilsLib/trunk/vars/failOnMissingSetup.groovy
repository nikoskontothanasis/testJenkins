void call(requiredPipelineParameters, requiredPipelineEnvVariables, requiredCredentialIds, requiredConfigFileIds, requiredToolIds) {
  missingParameters=[]
  requiredPipelineParameters.each { paramName ->
    if (params.get(paramName) == null) {
      missingParameters.add(paramName)
    }
  }
  
  missingEnvVariables=[]
  requiredPipelineEnvVariables.each { envVarName ->
    if (env.getProperty(envVarName) == null) {
      missingEnvVariables.add(envVarName)
    }
  }
  
  missingCredentialIds=[]
  requiredCredentialIds.each { cred ->
    try {
      withCredentials([usernamePassword(credentialsId: cred.value, usernameVariable: 'SOME_USER', passwordVariable: 'SOME_PASSWORD')]) {
        true
      }
    } catch (org.jenkinsci.plugins.credentialsbinding.impl.CredentialNotFoundException e) {
      missingCredentialIds.add(cred.value)
    }
  }
  
  missingConfigFileIds=[]
  requiredConfigFileIds.each { it ->
    try {
      configFileProvider([configFile(fileId: it.value, variable: 'SOME_FILE')]) {
        true
      }
    } catch (Exception e) {
      missingConfigFileIds.add(it.value)
    }
  }
  
  missingToolIds=[]
  requiredToolIds.each { it ->
    try {
      tool name: it.value
    } catch (Exception e) {
      missingToolIds.add(it.value)
    }
  }
  
  errorMessage='"The following required setup is missing:'
  
  errorsFound=false
  if (missingParameters.size() > 0) {
    errorMessage+="\nPipeline parameters: ${missingParameters}"
    errorsFound=true
  }
  
  if (missingEnvVariables.size() > 0) {
    errorMessage+="\nPipeline env variables: ${missingEnvVariables}"
    errorsFound=true
  }
  
  if (missingCredentialIds.size() > 0) {
    errorMessage+="\nCredential IDs: ${missingCredentialIds}"
    errorsFound=true
  }
  
  if (missingConfigFileIds.size() > 0) {
    errorMessage+="\nConfig file IDs (missing or failed to retrieve): ${missingConfigFileIds}"
    errorsFound=true
  }
  
  if (missingToolIds.size() > 0) {
    errorMessage+="\nTool IDs: ${missingToolIds}"
    errorsFound=true
  }
  
  if (errorsFound) {
    error errorMessage
  }
}
