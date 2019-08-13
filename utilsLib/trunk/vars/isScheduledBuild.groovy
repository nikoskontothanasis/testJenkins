boolean call() {
  def result=false
  def causes = currentBuild.rawBuild.getCauses()
  
  causes.each { cause ->
    if (cause instanceof hudson.triggers.TimerTrigger$TimerTriggerCause || cause instanceof hudson.triggers.SCMTrigger$SCMTriggerCause) {
      result=true
    }
  }
  
  return result
}
