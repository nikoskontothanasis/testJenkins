//initialBuildType: "last_success" or "previous_build"
def call(initialBuildType) {
  def logEntries=getChangesSinceBuild(initialBuildType)
  
  if (logEntries.isEmpty()) {
    return 'No changes'
  }
  
  //In some cases the same message appears multiple times. Return only unique messages.
  def uniqueMessages=[]
  logEntries.each { logEntry ->
    def commitIdTitle=logEntry.parent.kind=='svn' ? 'Revision' : 'Commit Id'
    def singleLineCommitMsg=logEntry.msg.replaceAll(~/[\r\n]+/, ' ')
    def msg="${commitIdTitle}: ${logEntry.commitId} - Author: ${logEntry.author} - Message: ${singleLineCommitMsg}"
    
    //Do not use a Set for the unique messages, because we want to keep the initial order
    if (!uniqueMessages.contains(msg)) {
      uniqueMessages.add(msg)
    }
  }
  
  return uniqueMessages.join('\n')
}