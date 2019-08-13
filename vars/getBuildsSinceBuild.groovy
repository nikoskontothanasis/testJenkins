//Return all the builds since the last previous build that finished with the targetResult
def call(targetResult) {
  def passedBuilds = []
  def earliestBuild=currentBuild
  
  while ((earliestBuild != null) && (earliestBuild.result != targetResult)) {
    passedBuilds.add(earliestBuild)
    earliestBuild=earliestBuild.getPreviousBuild()
  }
  
  return passedBuilds;
}
