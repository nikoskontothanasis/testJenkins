//initialBuildType: "last_success" or "previous_build"
def call(initialBuildType) {
  def previousBuilds=[]
  
  if (initialBuildType=='last_success') {
    previousBuilds=getBuildsSinceBuild('SUCCESS')
  } else if (initialBuildType=='previous_build') {
    previousBuilds=[currentBuild, currentBuild.getPreviousBuild()]
  }
  
  def logEntries=[]
  previousBuilds.each { bld ->
    def changeLogSets = bld.rawBuild.changeSets
    changeLogSets.each { changeLogSet ->
      changeLogSet.items.each { logEntry ->
        logEntries.add(logEntry)
      }
    }
  }
  
  return logEntries;
}