def call() {
  //Try to unstash the global variables file. The stash may not exist for stage re-execution, in which case it is
  //fine to use the latest file that should already exist in the workspace
  try {
    unstash 'globalVariablesStash'
  } catch(Exception e) {
    if (!e.getMessage().startsWith('No such saved stash')) {
      throw e
    }
  }

  def variables=readFile(file: 'globalVariables')
  /*
  Use "groovy.json.JsonSlurperClassic" class instead of "groovy.json.JsonSlurper" to avoid error
  "java.io.NotSerializableException: groovy.json.internal.LazyMap" on re-executed stages
  */
  
  groovy.json.internal.LazyMap jsonLazyMap=new groovy.json.JsonSlurperClassic().parseText(variables)
  java.util.LinkedHashMap linkedHashMap=new java.util.LinkedHashMap(jsonLazyMap)
  
  return linkedHashMap
}
