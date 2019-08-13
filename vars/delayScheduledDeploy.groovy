//In case of scheduled execution execute the deployment at a specified time after the job is triggered
boolean call(def delayMinutes) {
  def deploymentDelay=delayMinutes.toDouble()
  
  if (deploymentDelay>0 && isScheduledBuild()) {
    echo "This is a scheduled build with deployment delay of ${deploymentDelay} minutes. Wait until the delay has passed to proceed with deployment"
    def deploymentDelayMillis=deploymentDelay*60*1000
    
    waitUntil {
      //def buildDurationMillis=currentBuild.startTimeInMillis
      //Use the timeInMillis which counts since the scheduled time, instead of the startTimeInMillis which counts since the actual start time of the build
      def buildDurationMillis=getCurrentTimeMillis() - currentBuild.timeInMillis
      
      if (buildDurationMillis >= deploymentDelayMillis) {
        return true
      }
      else {
        //Sleep for 60 seconds
        sleep 60
        return false
      }
    }
    echo 'Continuing execution...'
  }
}
